package com.github.kaklakariada.aws.lambda.arg.adapter;

import java.lang.reflect.Parameter;

import com.amazonaws.services.lambda.runtime.Context;
import com.github.kaklakariada.aws.lambda.arg.SingleArgValueAdapter;
import com.github.kaklakariada.aws.lambda.request.ApiGatewayRequest;

public class ContextArgAdapaterFactory extends ArgAdapterFactory {

	@Override
	public Class<?> getSupportedArgType() {
		return Context.class;
	}

	@Override
	public SingleArgValueAdapter createAdapter(Parameter param) {
		return (ApiGatewayRequest request, Context context) -> context;
	}
}
