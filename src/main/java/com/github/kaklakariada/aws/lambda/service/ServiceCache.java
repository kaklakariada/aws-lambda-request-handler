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

import com.github.kaklakariada.aws.lambda.model.request.ApiGatewayRequest;

public class ServiceCache<P extends ServiceParams> {
	private final Map<P, ServiceRegistry> serviceRegistries = new HashMap<>();
	private final ServiceFactory<P> factory;

	public ServiceCache(ServiceFactory<P> factory) {
		this.factory = factory;
	}

	public P getParams(ApiGatewayRequest request, Map<String, String> env) {
		return factory.extractServiceParams(request, env);
	}

	public <T> T getService(Class<T> type, P params) {
		ServiceRegistry registry = serviceRegistries.get(params);
		if (registry == null) {
			registry = new ServiceRegistry();
			factory.registerServices(registry, params);
			serviceRegistries.put(params, registry);
		}
		return registry.getService(type);
	}
}
