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
package com.github.kaklakariada.aws.lambda.controller;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import com.amazonaws.services.lambda.runtime.Context;
import com.github.kaklakariada.aws.lambda.model.request.ApiGatewayRequest;

/**
 * Marks a method of a {@link LambdaController} as the request handler.
 * Supported method arguments:
 * <ul>
 * <li>Type {@link ApiGatewayRequest}
 * <li>Type {@link Context}
 * <li>Argument annotation {@link RequestBody}
 * <li>Argument annotation {@link HeaderValue}
 * <li>Argument annotation {@link PathParameter}
 * <li>Argument annotation {@link QueryStringParameter}
 * </ul>
 */
@Retention(RUNTIME)
@Target(METHOD)
public @interface RequestHandlerMethod {

}
