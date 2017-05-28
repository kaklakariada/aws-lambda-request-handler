package com.github.kaklakariada.aws.lambda.arg.adapter;

import java.lang.annotation.Annotation;
import java.lang.reflect.Parameter;

import com.amazonaws.services.lambda.runtime.Context;
import com.github.kaklakariada.aws.lambda.arg.SingleArgValueAdapter;
import com.github.kaklakariada.aws.lambda.controller.QueryStringParameter;
import com.github.kaklakariada.aws.lambda.request.ApiGatewayRequest;

public class QueryStringArgAdapaterFactory extends ArgAdapterFactory {

	@Override
	public Class<?> getSupportedArgType() {
		return String.class;
	}

	@Override
	public Class<? extends Annotation> getSupportedArgAnnotation() {
		return QueryStringParameter.class;
	}

	@Override
	public SingleArgValueAdapter createAdapter(Parameter param) {
		final String parameterName = param.getAnnotation(QueryStringParameter.class).value();
		return (ApiGatewayRequest request, Context context) -> request.getQueryStringParameters().get(parameterName);
	}
}
