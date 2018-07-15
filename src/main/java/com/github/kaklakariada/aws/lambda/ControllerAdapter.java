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
package com.github.kaklakariada.aws.lambda;

import static java.util.stream.Collectors.toList;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import com.amazonaws.services.lambda.runtime.Context;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.kaklakariada.aws.lambda.arg.AllArgValueAdapter;
import com.github.kaklakariada.aws.lambda.arg.AllArgValueAdapterFactory;
import com.github.kaklakariada.aws.lambda.controller.LambdaController;
import com.github.kaklakariada.aws.lambda.controller.RequestHandlerMethod;
import com.github.kaklakariada.aws.lambda.exception.ConfigurationErrorException;
import com.github.kaklakariada.aws.lambda.exception.InternalServerErrorException;
import com.github.kaklakariada.aws.lambda.model.request.ApiGatewayRequest;

public class ControllerAdapter {

	private final LambdaController controller;
	private final Method handlerMethod;
	private final AllArgValueAdapter argValueAdapter;

	private ControllerAdapter(LambdaController controller, Method handlerMethod, AllArgValueAdapter argValueAdapter) {
		this.controller = controller;
		this.handlerMethod = handlerMethod;
		this.argValueAdapter = argValueAdapter;
	}

	public static ControllerAdapter create(ObjectMapper objectMapper, LambdaController controller) {
		final Method handlerMethod = getHandlerMethod(controller);
		final AllArgValueAdapter argValueAdapter = getArgValueAdapter(objectMapper, handlerMethod);
		return new ControllerAdapter(controller, handlerMethod, argValueAdapter);
	}

	private static Method getHandlerMethod(LambdaController controller) {
		final List<Method> handlerMethods = Arrays.stream(controller.getClass().getMethods())
				.filter(method -> method.getAnnotation(RequestHandlerMethod.class) != null).collect(toList());
		if (handlerMethods.isEmpty() || handlerMethods.size() > 1) {
			throw new ConfigurationErrorException("Class " + controller.getClass().getName()
					+ " must have exactly one public method annotated with " + RequestHandlerMethod.class.getName());
		}
		return handlerMethods.get(0);
	}

	private static <I> AllArgValueAdapter getArgValueAdapter(ObjectMapper objectMapper, Method handlerMethod) {
		return new AllArgValueAdapterFactory(objectMapper).getAdapter(handlerMethod);
	}

	public Object handleRequest(ApiGatewayRequest request, Context context) {
		final Object[] args = argValueAdapter.getArgumentValue(request, context);
		try {
			return handlerMethod.invoke(controller, args);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			throw new InternalServerErrorException("Error invoking handler method", e);
		}
	}
}
