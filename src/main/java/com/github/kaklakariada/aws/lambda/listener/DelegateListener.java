package com.github.kaklakariada.aws.lambda.listener;

import java.util.List;

import com.amazonaws.services.lambda.runtime.Context;
import com.github.kaklakariada.aws.lambda.model.request.ApiGatewayRequest;
import com.github.kaklakariada.aws.lambda.model.response.ApiGatewayResponse;

public class DelegateListener implements RequestProcessingListener {

	private final List<RequestProcessingListener> delegates;

	public DelegateListener(List<RequestProcessingListener> delegates) {
		this.delegates = delegates;
	}

	@Override
	public void beforeRequest(ApiGatewayRequest request, Context context) {
		delegates.forEach(d -> d.beforeRequest(request, context));
	}

	@Override
	public void afterRequest(ApiGatewayRequest request, ApiGatewayResponse response, Context context) {
		delegates.forEach(d -> d.afterRequest(request, response, context));
	}
}
