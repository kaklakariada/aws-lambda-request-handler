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
package com.github.kaklakariada.aws.lambda.arg;

import java.lang.annotation.Annotation;
import java.lang.reflect.Parameter;

import com.amazonaws.services.lambda.runtime.Context;
import com.github.kaklakariada.aws.lambda.arg.adapter.BodyArgAdapter;
import com.github.kaklakariada.aws.lambda.controller.PathParameter;
import com.github.kaklakariada.aws.lambda.controller.QueryStringParameter;
import com.github.kaklakariada.aws.lambda.controller.RequestBody;
import com.github.kaklakariada.aws.lambda.exception.ConfigurationErrorException;
import com.github.kaklakariada.aws.lambda.request.ApiGatewayRequest;

class SingleArgValueAdapterFactory {

	SingleArgValueAdapterFactory() {
	}

	SingleArgValueAdapter getAdapter(Parameter param) {
		if (param.getAnnotation(RequestBody.class) != null) {
			return new BodyArgAdapter().createAdapter(param);
		}
		if (param.getType().isAssignableFrom(Context.class)) {
			return (ApiGatewayRequest request, Context context) -> context;
		}
		if (param.getType().isAssignableFrom(ApiGatewayRequest.class)) {
			return (ApiGatewayRequest request, Context context) -> request;
		}
		final QueryStringParameter queryString = param.getAnnotation(QueryStringParameter.class);
		if (queryString != null) {
			assertParamType(param, String.class, QueryStringParameter.class);
			return (ApiGatewayRequest request, Context context) -> request.getQueryStringParameters()
					.get(queryString.value());
		}

		final PathParameter pathParameter = param.getAnnotation(PathParameter.class);
		if (pathParameter != null) {
			assertParamType(param, String.class, PathParameter.class);
			return (ApiGatewayRequest request, Context context) -> request.getPathParameters()
					.get(pathParameter.value());
		}
		throw new ConfigurationErrorException("Could not find adapter for parameter " + param + " of handler method");
	}

	private void assertParamType(Parameter param, final Class<?> expectedType, Class<? extends Annotation> annotation) {
		if (!param.getType().isAssignableFrom(expectedType)) {
			throw new ConfigurationErrorException(
					"Argument of handler method " + param.getType().getName() + " annotated with "
							+ annotation.getName() + " is not compatible with request type " + expectedType.getName());
		}
	}
}
