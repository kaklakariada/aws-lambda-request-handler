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
import com.github.kaklakariada.aws.lambda.arg.AllArgValueAdapter;
import com.github.kaklakariada.aws.lambda.arg.AllArgValueAdapterFactory;
import com.github.kaklakariada.aws.lambda.controller.LambdaController;
import com.github.kaklakariada.aws.lambda.controller.RequestHandlerMethod;
import com.github.kaklakariada.aws.lambda.exception.ConfigurationErrorException;
import com.github.kaklakariada.aws.lambda.exception.InternalServerErrorException;
import com.github.kaklakariada.aws.lambda.request.ApiGatewayRequest;

public class ControllerAdapter<I, O> {

	private final LambdaController<I, O> controller;
	private final Method handlerMethod;
	private final Class<O> responseType;
	private final AllArgValueAdapter argValueAdapter;

	private ControllerAdapter(LambdaController<I, O> controller, Method handlerMethod,
			AllArgValueAdapter argValueAdapter, Class<I> requestType, Class<O> responseType) {
		this.controller = controller;
		this.handlerMethod = handlerMethod;
		this.argValueAdapter = argValueAdapter;
		this.responseType = responseType;
	}

	public static <I, O> ControllerAdapter<I, O> create(LambdaController<I, O> controller, Class<I> requestType,
			Class<O> responseType) {
		final Method handlerMethod = getHandlerMethod(controller, requestType, responseType);
		final AllArgValueAdapter argValueAdapter = getArgValueAdapter(handlerMethod, requestType);
		return new ControllerAdapter<I, O>(controller, handlerMethod, argValueAdapter, requestType, responseType);
	}

	private static <I, O> Method getHandlerMethod(LambdaController<I, O> controller, Class<I> requestType,
			Class<O> responseType) {
		final List<Method> handlerMethods = Arrays.stream(controller.getClass().getMethods())
				.filter(method -> method.getAnnotation(RequestHandlerMethod.class) != null).collect(toList());
		if (handlerMethods.isEmpty() || handlerMethods.size() > 1) {
			throw new ConfigurationErrorException("Class " + controller.getClass().getName()
					+ " must have exactly one public method annotated with " + RequestHandlerMethod.class.getName());
		}
		final Method method = handlerMethods.get(0);
		verifyReturnType(responseType, method);
		return method;
	}

	private static <I> AllArgValueAdapter getArgValueAdapter(Method handlerMethod, Class<I> requestType) {
		return new AllArgValueAdapterFactory(requestType).getAdapter(handlerMethod);
	}

	private static <O> void verifyReturnType(Class<O> responseType, final Method method) {
		if (!responseType.isAssignableFrom(method.getReturnType())) {
			throw new ConfigurationErrorException(
					"Return type '" + method.getReturnType().getName() + "' of handler method '" + method
							+ "' is not compatible with response type " + responseType.getName());
		}
	}

	public O handleRequest(ApiGatewayRequest request, I body, Context context) {
		final Object[] args = argValueAdapter.getArgumentValue(request, body, context);
		try {
			final Object result = handlerMethod.invoke(controller, args);
			return responseType.cast(result);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			throw new InternalServerErrorException("Error invoking handler method", e);
		}
	}
}
