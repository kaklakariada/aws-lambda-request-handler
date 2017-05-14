package com.github.kaklakariada.aws.lambda;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.amazonaws.services.lambda.runtime.Context;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.kaklakariada.aws.lambda.request.ApiGatewayRequest;

@RunWith(JUnitPlatform.class)
public class RequestHandlingServiceTest {

	@Mock
	private ControllerAdapter<TestRequest, TestResponse> controllerMock;
	@Mock
	private Context contextMock;
	@Mock
	private ApiGatewayRequest apiGatewayRequestMock;
	@Mock
	private TestRequest bodyMock;
	@Mock
	private TestResponse responseMock;

	private RequestHandlingService<TestRequest, TestResponse> service;
	private ObjectMapper objectMapper;
	private JsonNodeFactory jsonFactory;

	@BeforeEach
	public void setup() {
		MockitoAnnotations.initMocks(this);
		objectMapper = new ObjectMapper();
		jsonFactory = JsonNodeFactory.instance;
		service = new RequestHandlingService<TestRequest, TestResponse>(controllerMock, TestRequest.class,
				TestResponse.class);
		when(controllerMock.handleRequest(same(apiGatewayRequestMock), same(bodyMock), same(contextMock)))
				.thenReturn(new TestResponse());
	}

	@Test
	public void testRequest() {
		assertEquals(response(200, "null"), handleRequest(jsonFactory.objectNode()));
	}

	private ObjectNode response(int status, String body) {
		final ObjectNode headers = jsonFactory.objectNode();
		final ObjectNode response = jsonFactory.objectNode();
		response.set("statusCode", jsonFactory.numberNode(status));
		response.set("headers", headers);
		response.set("body", jsonFactory.textNode(body));
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

	private static class TestRequest {
	}

	private static class TestResponse {
	}
}
