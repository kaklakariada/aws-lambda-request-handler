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
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.amazonaws.services.lambda.runtime.Context;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.kaklakariada.aws.lambda.listener.RequestProcessingListener;
import com.github.kaklakariada.aws.lambda.model.response.ApiGatewayResponse;

public class RequestHandlingServiceTest {

	private static final String RESPONSE_MESSAGE = "msg";

	@Mock
	private ControllerAdapter controllerMock;
	@Mock
	private Context contextMock;
	@Mock
	private TestResponse responseMock;
	@Mock
	private RequestProcessingListener listenerMock;

	private RequestHandlingService service;
	private ObjectMapper objectMapper;
	private JsonNodeFactory jsonFactory;

	@BeforeEach
	public void setup() {
		MockitoAnnotations.initMocks(this);
		objectMapper = new ObjectMapper();
		jsonFactory = JsonNodeFactory.instance;
		service = new RequestHandlingService(objectMapper, controllerMock, listenerMock);
		when(controllerMock.handleRequest(any(), same(contextMock))).thenReturn(new TestResponse(RESPONSE_MESSAGE));
	}

	@Test
	public void testRequest() {
		assertEquals(response(200, "{\"message\":\"" + RESPONSE_MESSAGE + "\"}"),
				handleRequest(jsonFactory.objectNode()));
	}

	@Test
	public void testRequestReturnApiGatewayResponseUnchanged() {
		final ApiGatewayResponse response = new ApiGatewayResponse(201, emptyMap(), "response2");
		when(controllerMock.handleRequest(any(), same(contextMock))).thenReturn(response);
		assertEquals(response(201, "response2"), handleRequest(jsonFactory.objectNode()));
	}

	@Test
	public void testListenerCalled() {
		handleRequest(jsonFactory.objectNode());

		final InOrder inOrder = inOrder(listenerMock);
		inOrder.verify(listenerMock).beforeRequest(any(), same(contextMock));
		inOrder.verify(listenerMock).afterRequest(any(), any(), same(contextMock));
		inOrder.verifyNoMoreInteractions();
	}

	private ObjectNode response(int status, String body) {
		final ObjectNode headers = jsonFactory.objectNode();
		final ObjectNode response = jsonFactory.objectNode();
		response.set("statusCode", jsonFactory.numberNode(status));
		response.set("headers", headers);
		response.set("body", jsonFactory.textNode(body));
		response.set("isBase64Encoded", jsonFactory.booleanNode(false));
		return response;
	}

	private JsonNode handleRequest(ObjectNode request) {
		String requestString;
		try {
			requestString = objectMapper.writeValueAsString(request);
		} catch (final JsonProcessingException e) {
			throw new AssertionError("Error serializing request " + request, e);
		}
		final String response = handleRequest(requestString);
		try {
			return objectMapper.readTree(response);
		} catch (final IOException e) {
			throw new AssertionError("Error parsing json '" + response + "'", e);
		}
	}

	private String handleRequest(String request) {
		final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		service.handleRequest(new ByteArrayInputStream(request.getBytes(StandardCharsets.UTF_8)), outputStream,
				contextMock);
		return new String(outputStream.toByteArray(), StandardCharsets.UTF_8);
	}

	static class TestResponse {
		private final String message;

		TestResponse(String message) {
			this.message = message;
		}

		public String getMessage() {
			return message;
		}
	}
}
