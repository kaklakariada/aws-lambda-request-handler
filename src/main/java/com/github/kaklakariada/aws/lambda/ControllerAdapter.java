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
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.List;

import com.amazonaws.services.lambda.runtime.Context;
import com.github.kaklakariada.aws.lambda.controller.LambdaController;
import com.github.kaklakariada.aws.lambda.controller.RequestBody;
import com.github.kaklakariada.aws.lambda.controller.RequestHandlerMethod;
import com.github.kaklakariada.aws.lambda.exception.ConfigurationErrorException;
import com.github.kaklakariada.aws.lambda.exception.InternalServerErrorException;
import com.github.kaklakariada.aws.lambda.request.ApiGatewayRequest;

public class ControllerAdapter<I, O> {

	private final LambdaController<I, O> controller;
	private final Method handlerMethod;
	private final Class<I> requestType;
	private final Class<O> responseType;

	private ControllerAdapter(LambdaController<I, O> controller, Method handlerMethod, Class<I> requestType,
			Class<O> responseType) {
		this.controller = controller;
		this.handlerMethod = handlerMethod;
		this.requestType = requestType;
		this.responseType = responseType;
	}

	public static <I, O> ControllerAdapter<I, O> create(LambdaController<I, O> controller, Class<I> requestType,
			Class<O> responseType) {
		final Method handlerMethod = getHandlerMethod(controller, requestType, responseType);
		return new ControllerAdapter<I, O>(controller, handlerMethod, requestType, responseType);
	}

	private static <I, O> Method getHandlerMethod(LambdaController<I, O> controller, Class<I> requestType,
			Class<O> responseType) {
		final List<Method> handlerMethods = Arrays.stream(controller.getClass().getMethods())
				.filter(method -> method.getAnnotation(RequestHandlerMethod.class) != null).collect(toList());
		if (handlerMethods.isEmpty() || handlerMethods.size() > 1) {
			throw new ConfigurationErrorException("Class " + controller.getClass().getName()
					+ " must have exactly one method annotated with " + RequestHandlerMethod.class.getName());
		}
		final Method method = handlerMethods.get(0);
		verifyReturnType(responseType, method);
		return method;
	}

	private static <O> void verifyReturnType(Class<O> responseType, final Method method) {
		if (!responseType.isAssignableFrom(method.getReturnType())) {
			throw new ConfigurationErrorException("Handler method return type " + method.getReturnType().getName()
					+ " is not compatible with response type " + responseType.getName());
		}
	}

	public O handleRequest(ApiGatewayRequest request, I body, Context context) {
		try {
			final Object result = handlerMethod.invoke(controller, getHandlerArguments(request, body, context));
			return responseType.cast(result);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			throw new InternalServerErrorException("Error invoking handler method", e);
		}
	}

	private Object[] getHandlerArguments(ApiGatewayRequest request, I body, Context context) {
		return Arrays.stream(handlerMethod.getParameters())
				.map(param -> getArgumentValue(param, request, body, context)).toArray();
	}

	private Object getArgumentValue(Parameter param, ApiGatewayRequest request, I body, Context context) {
		if (param.getAnnotation(RequestBody.class) != null) {
			if (!param.getType().isAssignableFrom(requestType)) {
				throw new ConfigurationErrorException("Body argument of handler method " + param.getType().getName()
						+ " is not compatible with request type " + requestType.getName());
			}
			return body;
		}
		if (param.getType().isAssignableFrom(Context.class)) {
			return context;
		}
		if (param.getType().isAssignableFrom(ApiGatewayRequest.class)) {
			return request;
		}

		throw new ConfigurationErrorException(
				"Could not convert parameter " + param + " of handler method " + handlerMethod);
	}
}
