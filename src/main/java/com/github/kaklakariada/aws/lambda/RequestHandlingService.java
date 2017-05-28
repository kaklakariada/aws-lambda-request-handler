/**
 * aws-lambda-request-handler - Request handler for AWS Lambda Proxy model
 * Copyright (C) 2017 Christoph Pirkl <christoph at users.sourceforge.net>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.github.kaklakariada.aws.lambda;

import static java.util.Collections.emptyMap;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.services.lambda.runtime.Context;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.kaklakariada.aws.lambda.controller.LambdaController;
import com.github.kaklakariada.aws.lambda.exception.BadRequestException;
import com.github.kaklakariada.aws.lambda.exception.InternalServerErrorException;
import com.github.kaklakariada.aws.lambda.exception.LambdaException;
import com.github.kaklakariada.aws.lambda.request.ApiGatewayRequest;

public class RequestHandlingService {
	private static final Logger LOG = LoggerFactory.getLogger(RequestHandlingService.class);

	private final ObjectMapper objectMapper;
	private final ControllerAdapter handler;

	public static RequestHandlingService create(LambdaController controller) {
		final ControllerAdapter adapter = ControllerAdapter.create(controller);
		return new RequestHandlingService(adapter);
	}

	RequestHandlingService(ControllerAdapter handler) {
		this(new ObjectMapper(), handler);
	}

	RequestHandlingService(ObjectMapper objectMapper, ControllerAdapter handler) {
		this.objectMapper = objectMapper;
		this.handler = handler;
	}

	public void handleRequest(InputStream input, OutputStream output, Context context) {
		final ApiGatewayResponse response = handleRequest(input, context);
		sendResponse(output, response);
	}

	private ApiGatewayResponse handleRequest(InputStream input, Context context) {
		try {
			final ApiGatewayRequest request = parseJsonString(readStream(input), ApiGatewayRequest.class);
			final Object result = handler.handleRequest(request, context);
			return new ApiGatewayResponse(serializeResult(result));
		} catch (final LambdaException e) {
			LOG.error("Error processing request: " + e.getMessage());
			return buildErrorResponse(e, context);
		} catch (final Exception e) {
			LOG.error("Error processing request: " + e.getMessage(), e);
			return buildErrorResponse(new InternalServerErrorException("Error", e), context);
		}
	}

	private String readStream(InputStream input) {
		try (BufferedReader buffer = new BufferedReader(new InputStreamReader(input))) {
			return buffer.lines().collect(Collectors.joining("\n"));
		} catch (final IOException e) {
			throw new InternalServerErrorException("Error reading input stream", e);
		}
	}

	private void sendResponse(OutputStream output, ApiGatewayResponse response) {
		try {
			objectMapper.writeValue(output, response);
		} catch (final Exception e) {
			LOG.error("Error serializing response: " + e.getMessage(), e);
			throw new InternalServerErrorException("Error serializing response", e);
		}
	}

	private <T> T parseJsonString(String input, Class<T> type) {
		try {
			return objectMapper.readValue(input, type);
		} catch (final Exception e) {
			LOG.error("Error parsing input '" + input + "': " + e.getMessage(), e);
			throw new BadRequestException("Error parsing request", e);
		}
	}

	private ApiGatewayResponse buildErrorResponse(LambdaException e, Context context) {
		final ErrorResponseBody errorResult = ErrorResponseBody.create(e, context);
		return new ApiGatewayResponse(e.getErrorCode(), emptyMap(), serializeResult(errorResult));
	}

	private String serializeResult(Object result) {
		try {
			return objectMapper.writeValueAsString(result);
		} catch (final Exception e) {
			LOG.error("Error serializing response: " + e.getMessage(), e);
			throw new InternalServerErrorException("Error serializing response", e);
		}
	}
}
