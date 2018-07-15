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

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;

import java.lang.reflect.Parameter;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.kaklakariada.aws.lambda.arg.adapter.ApiGatewayRequestAdapaterFactory;
import com.github.kaklakariada.aws.lambda.arg.adapter.ArgAdapterFactory;
import com.github.kaklakariada.aws.lambda.arg.adapter.BodyArgAdapterFactory;
import com.github.kaklakariada.aws.lambda.arg.adapter.ContextArgAdapaterFactory;
import com.github.kaklakariada.aws.lambda.arg.adapter.HeaderValueArgAdapaterFactory;
import com.github.kaklakariada.aws.lambda.arg.adapter.PathParameterArgAdapaterFactory;
import com.github.kaklakariada.aws.lambda.arg.adapter.QueryStringArgAdapaterFactory;
import com.github.kaklakariada.aws.lambda.exception.ConfigurationErrorException;

class SingleArgValueAdapterFactory {

	private final List<ArgAdapterFactory> factories;

	SingleArgValueAdapterFactory(List<ArgAdapterFactory> factories) {
		this.factories = factories;
	}

	SingleArgValueAdapterFactory(ObjectMapper objectMapper) {
		this(asList(new BodyArgAdapterFactory(objectMapper), //
				new ApiGatewayRequestAdapaterFactory(), //
				new ContextArgAdapaterFactory(), //
				new PathParameterArgAdapaterFactory(), //
				new QueryStringArgAdapaterFactory(), //
				new HeaderValueArgAdapaterFactory()));
	}

	SingleArgValueAdapter getAdapter(Parameter param) {
		final List<ArgAdapterFactory> supportedFactories = factories.stream() //
				.filter(factory -> factory.supports(param)) //
				.collect(toList());
		if (supportedFactories.isEmpty()) {
			throw new ConfigurationErrorException(
					"None of the arg adapter factories supports parameter '" + param + "'");
		}
		if (supportedFactories.size() > 1) {
			throw new ConfigurationErrorException(
					"More than one arg adapter factory supports parameter '" + param + "': " + supportedFactories);
		}
		return supportedFactories.get(0).createAdapter(param);
	}
}
