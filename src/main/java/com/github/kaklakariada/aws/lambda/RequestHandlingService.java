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
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.services.lambda.runtime.Context;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.kaklakariada.aws.lambda.controller.LambdaController;
import com.github.kaklakariada.aws.lambda.exception.BadRequestException;
import com.github.kaklakariada.aws.lambda.exception.InternalServerErrorException;
import com.github.kaklakariada.aws.lambda.exception.LambdaException;
import com.github.kaklakariada.aws.lambda.inject.CurrentRequestParamsSupplier;
import com.github.kaklakariada.aws.lambda.inject.Injector;
import com.github.kaklakariada.aws.lambda.listener.DelegateListener;
import com.github.kaklakariada.aws.lambda.listener.RequestProcessingListener;
import com.github.kaklakariada.aws.lambda.model.request.ApiGatewayRequest;
import com.github.kaklakariada.aws.lambda.model.response.ApiGatewayResponse;
import com.github.kaklakariada.aws.lambda.service.ServiceCache;
import com.github.kaklakariada.aws.lambda.service.ServiceFactory;
import com.github.kaklakariada.aws.lambda.service.ServiceParams;

public class RequestHandlingService {
	private static final Logger LOG = LoggerFactory.getLogger(RequestHandlingService.class);

	private static final boolean FAIL_ON_UNKNOWN_JSON_PROPERTIES = false;

	private final ObjectMapper objectMapper;
	private final ControllerAdapter handler;
	private final RequestProcessingListener listener;

	public static <P extends ServiceParams> RequestHandlingService create(LambdaController controller,
			ServiceFactory<P> serviceFactory) {
		final List<RequestProcessingListener> listeners = new ArrayList<>();
		if (serviceFactory != null) {
			final CurrentRequestParamsSupplier<P> serviceParamsSupplier = new CurrentRequestParamsSupplier<>(
					serviceFactory);
			listeners.add(serviceParamsSupplier);
			final Injector<P> injector = new Injector<>(new ServiceCache<>(serviceFactory), serviceParamsSupplier);
			LOG.debug("Injecting services into controller {} using service factory {}", controller, serviceFactory);
			injector.injectServices(controller);
		} else {
			LOG.debug("No service factory available, don't inject services");
		}
		final ObjectMapper objectMapper = createObjectMapper();
		final ControllerAdapter adapter = ControllerAdapter.create(objectMapper, controller);
		return new RequestHandlingService(objectMapper, adapter, new DelegateListener(listeners));
	}

	private static ObjectMapper createObjectMapper() {
		final ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, FAIL_ON_UNKNOWN_JSON_PROPERTIES);
		return objectMapper;
	}

	RequestHandlingService(ObjectMapper objectMapper, ControllerAdapter handler, RequestProcessingListener listener) {
		this.objectMapper = objectMapper;
		this.handler = handler;
		this.listener = listener;
	}

	public void handleRequest(InputStream input, OutputStream output, Context context) {
		final ApiGatewayRequest request = parseRequest(input);
		listener.beforeRequest(request, context);
		final ApiGatewayResponse response = handleRequest(request, context);
		listener.afterRequest(request, response, context);
		sendResponse(output, response);
	}

	private ApiGatewayRequest parseRequest(InputStream input) {
		return parseJsonString(readStream(input), ApiGatewayRequest.class);
	}

	private ApiGatewayResponse handleRequest(final ApiGatewayRequest request, Context context) {
		try {
			final Object result = handler.handleRequest(request, context);
			if (result instanceof ApiGatewayResponse) {
				LOG.trace("Result type is already ApiGatewayResponse: don't serialize body.");
				return (ApiGatewayResponse) result;
			}
			final String responseBody = serializeResult(result);
			return ApiGatewayResponse.ok(responseBody);
		} catch (final LambdaException e) {
			LOG.error("Error processing request: {}", e.getMessage(), e);
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
