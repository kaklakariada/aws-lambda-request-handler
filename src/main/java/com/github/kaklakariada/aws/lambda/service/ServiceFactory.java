package com.github.kaklakariada.aws.lambda.service;

import java.util.Map;

import com.github.kaklakariada.aws.lambda.model.request.ApiGatewayRequest;

public interface ServiceFactory<P extends ServiceParams> {
	P extractServiceParams(ApiGatewayRequest request, Map<String, String> env);

	void registerServices(ServiceRegistry registry, P params);
}
