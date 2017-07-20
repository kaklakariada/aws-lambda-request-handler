package com.github.kaklakariada.aws.lambda.service;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class ServiceRegistry {

	private final Map<Class<?>, ServiceInitializer<?>> services = new HashMap<>();

	public <T> ServiceInitializer<T> init(Supplier<T> factory) {
		return new ServiceInitializer<>(factory);
	}

	public <T> void addService(Class<T> type, Supplier<T> factory) {
		addService(type, init(factory));
	}

	public <T> void addService(Class<T> type, ServiceInitializer<T> initializer) {
		if (services.containsKey(type)) {
			throw new IllegalStateException("Type " + type.getName() + " already registered");
		}
		services.put(type, initializer);
	}

	public <T> T getService(Class<T> type) {
		final ServiceInitializer<T> defaultInitializer = init(() -> {
			throw new IllegalStateException();
		});
		@SuppressWarnings("unchecked")
		final ServiceInitializer<T> initializer = (ServiceInitializer<T>) services.getOrDefault(type,
				defaultInitializer);
		return initializer.get();
	}
}
