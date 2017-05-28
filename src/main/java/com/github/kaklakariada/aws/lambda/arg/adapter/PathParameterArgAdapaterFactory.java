package com.github.kaklakariada.aws.lambda.arg.adapter;

import java.lang.annotation.Annotation;
import java.lang.reflect.Parameter;

import com.amazonaws.services.lambda.runtime.Context;
import com.github.kaklakariada.aws.lambda.arg.SingleArgValueAdapter;
import com.github.kaklakariada.aws.lambda.controller.PathParameter;
import com.github.kaklakariada.aws.lambda.request.ApiGatewayRequest;

public class PathParameterArgAdapaterFactory extends ArgAdapterFactory {

	@Override
	public Class<?> getSupportedArgType() {
		return String.class;
	}

	@Override
	public Class<? extends Annotation> getSupportedArgAnnotation() {
		return PathParameter.class;
	}

	@Override
	public SingleArgValueAdapter createAdapter(Parameter param) {
		final String parameterName = param.getAnnotation(PathParameter.class).value();
		return (ApiGatewayRequest request, Context context) -> request.getPathParameters().get(parameterName);
	}
}
