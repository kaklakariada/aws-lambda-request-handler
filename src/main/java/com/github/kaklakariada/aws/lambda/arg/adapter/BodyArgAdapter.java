package com.github.kaklakariada.aws.lambda.arg.adapter;

import java.lang.annotation.Annotation;
import java.lang.reflect.Parameter;
import java.util.Base64;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.services.lambda.runtime.Context;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.kaklakariada.aws.lambda.arg.SingleArgValueAdapter;
import com.github.kaklakariada.aws.lambda.controller.RequestBody;
import com.github.kaklakariada.aws.lambda.exception.BadRequestException;
import com.github.kaklakariada.aws.lambda.request.ApiGatewayRequest;

public class BodyArgAdapter implements ArgAdapterFactory {

	private static final Logger LOG = LoggerFactory.getLogger(BodyArgAdapter.class);

	private final ObjectMapper objectMapper;

	public BodyArgAdapter() {
		this(new ObjectMapper());
	}

	BodyArgAdapter(ObjectMapper objectMapper) {
		this.objectMapper = objectMapper;
	}

	@Override
	public Class<? extends Annotation> getSupportedArgAnnotation() {
		return RequestBody.class;
	}

	@Override
	public SingleArgValueAdapter createAdapter(Parameter param) {
		final Class<?> bodyType = param.getType();
		return (ApiGatewayRequest request, Context context) -> {
			return parseBody(request, bodyType);
		};
	}

	private Object parseBody(ApiGatewayRequest request, Class<?> bodyType) {
		final String body = request.getBody();
		if (body == null || body.isEmpty()) {
			return null;
		}
		if (!request.getIsBase64Encoded()) {
			return parseJsonString(body, bodyType);
		}
		if (!bodyType.equals(byte[].class)) {
			throw new IllegalStateException(
					"Got base64 encoded body but expected request type is " + bodyType.getName());
		}
		return base64Decode(body);
	}

	private Object parseJsonString(String input, Class<?> bodyType) {
		try {
			return objectMapper.readValue(input, bodyType);
		} catch (final Exception e) {
			LOG.error("Error parsing input '" + input + "': " + e.getMessage(), e);
			throw new BadRequestException("Error parsing request", e);
		}
	}

	private byte[] base64Decode(String body) {
		return Base64.getDecoder().decode(body);
	}
}
