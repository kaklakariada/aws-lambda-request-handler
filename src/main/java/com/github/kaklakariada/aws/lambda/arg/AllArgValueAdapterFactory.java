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

import static java.util.stream.Collectors.toList;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import com.amazonaws.services.lambda.runtime.Context;
import com.github.kaklakariada.aws.lambda.request.ApiGatewayRequest;

public class AllArgValueAdapterFactory {

	private final SingleArgValueAdapterFactory singleArgValueAdapterFactory;

	public AllArgValueAdapterFactory() {
		this(new SingleArgValueAdapterFactory());
	}

	AllArgValueAdapterFactory(SingleArgValueAdapterFactory singleArgValueAdapterFactory) {
		this.singleArgValueAdapterFactory = singleArgValueAdapterFactory;
	}

	public AllArgValueAdapter getAdapter(Method handlerMethod) {
		final List<SingleArgValueAdapter> adapters = Arrays.stream(handlerMethod.getParameters())
				.map(singleArgValueAdapterFactory::getAdapter) //
				.collect(toList());

		return (ApiGatewayRequest request, Context context) -> adapters.stream()
				.map(adapter -> adapter.getArgumentValue(request, context)) //
				.toArray();
	}
}
