package com.github.kaklakariada.aws.lambda.service;

import java.util.HashMap;
import java.util.Map;

import com.github.kaklakariada.aws.lambda.model.request.ApiGatewayRequest;

public class ServiceCache<P extends ServiceParams> {
	private final Map<P, ServiceRegistry> serviceRegistries = new HashMap<>();
	private final ServiceFactory<P> factory;

	public ServiceCache(ServiceFactory<P> factory) {
		this.factory = factory;
	}

	public <T> T getService(Class<T> type, ApiGatewayRequest request, Map<String, String> env) {
		return getService(type, factory.extractServiceParams(request, env));
	}

	private <T> T getService(Class<T> type, P params) {
		ServiceRegistry registry = serviceRegistries.get(params);
		if (registry == null) {
			registry = new ServiceRegistry();
			factory.registerServices(registry, params);
			serviceRegistries.put(params, registry);
		}
		return registry.getService(type);
	}
}
