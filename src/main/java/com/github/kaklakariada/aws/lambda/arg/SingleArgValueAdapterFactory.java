package com.github.kaklakariada.aws.lambda.arg;

import java.lang.reflect.Parameter;

import com.amazonaws.services.lambda.runtime.Context;
import com.github.kaklakariada.aws.lambda.controller.RequestBody;
import com.github.kaklakariada.aws.lambda.exception.ConfigurationErrorException;
import com.github.kaklakariada.aws.lambda.request.ApiGatewayRequest;

public class SingleArgValueAdapterFactory {

	private final Class<?> requestType;

	public SingleArgValueAdapterFactory(Class<?> requestType) {
		this.requestType = requestType;
	}

	public SingleArgValueAdapter getAdapter(Parameter param) {
		if (param.getAnnotation(RequestBody.class) != null) {
			if (!param.getType().isAssignableFrom(requestType)) {
				throw new ConfigurationErrorException("Body argument of handler method " + param.getType().getName()
						+ " is not compatible with request type " + requestType.getName());
			}
			return (ApiGatewayRequest request, Object body, Context context) -> body;
		}
		if (param.getType().isAssignableFrom(Context.class)) {
			return (ApiGatewayRequest request, Object body, Context context) -> context;
		}
		if (param.getType().isAssignableFrom(ApiGatewayRequest.class)) {
			return (ApiGatewayRequest request, Object body, Context context) -> request;
		}

		throw new ConfigurationErrorException("Could not find adapter for parameter " + param + " of handler method");
	}
}
