/**
 * aws-lambda-request-handler - Request handler for AWS Lambda Proxy model
 * Copyright (C) 2017 Christoph Pirkl <christoph at users.sourceforge.net>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
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
