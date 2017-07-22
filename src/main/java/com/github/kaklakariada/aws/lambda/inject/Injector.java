package com.github.kaklakariada.aws.lambda.inject;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.function.Supplier;

import javax.inject.Inject;

import com.github.kaklakariada.aws.lambda.controller.LambdaController;
import com.github.kaklakariada.aws.lambda.exception.InternalServerErrorException;
import com.github.kaklakariada.aws.lambda.service.ServiceCache;
import com.github.kaklakariada.aws.lambda.service.ServiceParams;

public class Injector<P extends ServiceParams> {
	private final ServiceCache<P> cache;
	private final Supplier<P> serviceParams;

	public Injector(ServiceCache<P> cache, Supplier<P> serviceParams) {
		this.cache = cache;
		this.serviceParams = serviceParams;
	}

	public void injectServices(LambdaController controller) {
		Arrays.stream(controller.getClass().getDeclaredFields())
				.filter(field -> field.getAnnotation(Inject.class) != null) //
				.filter(field -> field.getType().equals(Supplier.class)) //
				.forEach(field -> inject(controller, field));
	}

	private void inject(LambdaController controller, Field field) {
		final ParameterizedType type = (ParameterizedType) field.getGenericType();
		assert type.getActualTypeArguments().length == 1;
		final Type typeToInject = type.getActualTypeArguments()[0];
		final Supplier<?> serviceSupplier = getServiceSupplier((Class<?>) typeToInject);
		inject(controller, field, serviceSupplier);
	}

	private void inject(LambdaController controller, Field field, final Supplier<?> serviceSupplier) {
		field.setAccessible(true);
		try {
			field.set(controller, serviceSupplier);
		} catch (IllegalArgumentException | IllegalAccessException e) {
			throw new InternalServerErrorException("Error injecting field " + field, e);
		}
	}

	private <T> Supplier<T> getServiceSupplier(Class<T> typeToInject) {
		return () -> {
			final P params = serviceParams.get();
			if (params == null) {
				throw new IllegalStateException("No service param found");
			}
			return cache.getService(typeToInject, params);
		};
	}
}
