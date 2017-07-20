package com.github.kaklakariada.aws.lambda.service;

import java.util.function.Supplier;

public class ServiceInitializer<T> implements Supplier<T> {

	private final Supplier<T> factory;
	private T instance;

	ServiceInitializer(Supplier<T> factory) {
		this.factory = factory;
	}

	@Override
	public T get() {
		if (instance == null) {
			instance = factory.get();
		}
		return instance;
	}
}
