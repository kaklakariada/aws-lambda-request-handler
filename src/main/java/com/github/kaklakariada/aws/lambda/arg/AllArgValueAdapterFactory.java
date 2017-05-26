package com.github.kaklakariada.aws.lambda.arg;

import static java.util.stream.Collectors.toList;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import com.amazonaws.services.lambda.runtime.Context;
import com.github.kaklakariada.aws.lambda.request.ApiGatewayRequest;

public class AllArgValueAdapterFactory {

	private final SingleArgValueAdapterFactory singleArgValueAdapterFactory;

	public AllArgValueAdapterFactory(Class<?> requestType) {
		this(new SingleArgValueAdapterFactory(requestType));
	}

	AllArgValueAdapterFactory(SingleArgValueAdapterFactory singleArgValueAdapterFactory) {
		this.singleArgValueAdapterFactory = singleArgValueAdapterFactory;
	}

	public AllArgValueAdapter getAdapter(Method handlerMethod) {
		final List<SingleArgValueAdapter> adapters = Arrays.stream(handlerMethod.getParameters())
				.map(singleArgValueAdapterFactory::getAdapter) //
				.collect(toList());

		return (ApiGatewayRequest request, Object body, Context context) -> adapters.stream()
				.map(adapter -> adapter.getArgumentValue(request, body, context)) //
				.toArray();
	}
}
