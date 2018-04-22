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
package com.github.kaklakariada.aws.lambda.example;

import java.util.Map;

import com.github.kaklakariada.aws.lambda.model.request.ApiGatewayRequest;
import com.github.kaklakariada.aws.lambda.service.ServiceFactory;
import com.github.kaklakariada.aws.lambda.service.ServiceInitializer;
import com.github.kaklakariada.aws.lambda.service.ServiceRegistry;

public class MyServiceFactory implements ServiceFactory<MyServiceParams> {

	public static final String STAGE_ENV_VARIABLE = "stage";
	public static final String STAGE_VAR_CONFIG_VALUE_NAME = "configValue";

	@Override
	public MyServiceParams extractServiceParams(ApiGatewayRequest request, Map<String, String> env) {
		return new MyServiceParams(env.get(STAGE_ENV_VARIABLE),
				request.getStageVariables().get(STAGE_VAR_CONFIG_VALUE_NAME));
	}

	@Override
	public void registerServices(ServiceRegistry registry, MyServiceParams params) {
		final ServiceInitializer<MyServiceA> serviceA = registry.init(() -> new MyServiceA(params.getStage()));
		registry.addService(MyServiceA.class, serviceA);
		registry.addService(MyServiceB.class, () -> new MyServiceB(serviceA.get(), params.getConfigValue()));
	}
}