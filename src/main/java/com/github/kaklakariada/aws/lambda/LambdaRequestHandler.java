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

import java.io.InputStream;
import java.io.OutputStream;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import com.github.kaklakariada.aws.lambda.controller.LambdaController;

public abstract class LambdaRequestHandler<I, O> implements RequestStreamHandler {

	private final RequestHandlingService<I, O> requestHandlingService;

	protected LambdaRequestHandler(LambdaController<I, O> controller, Class<I> requestType, Class<O> responseType) {
		requestHandlingService = new RequestHandlingService<>(controller, requestType, responseType);
	}

	@Override
	public final void handleRequest(InputStream input, OutputStream output, Context context) {
		requestHandlingService.handleRequest(input, output, context);
	}
}
