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

import static java.util.Collections.singletonMap;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.github.kaklakariada.aws.lambda.example.MyServiceB;
import com.github.kaklakariada.aws.lambda.example.MyServiceFactory;
import com.github.kaklakariada.aws.lambda.example.MyServiceParams;
import com.github.kaklakariada.aws.lambda.model.request.ApiGatewayRequest;

public class ServiceCacheTest {

	private static final String STAGE1 = "stage1";
	private static final String STAGE2 = "stage2";
	private static final Map<String, String> ENV_STAGE1 = singletonMap(MyServiceFactory.STAGE_ENV_VARIABLE, STAGE1);
	private static final Map<String, String> ENV_STAGE2 = singletonMap(MyServiceFactory.STAGE_ENV_VARIABLE, STAGE2);
	private static final String SETTING_VAL_A = "val1";
	private static final String SETTING_VAL_B = "val2";

	private ServiceCache<MyServiceParams> cache;
	private ApiGatewayRequest requestA;
	private ApiGatewayRequest requestB;

	@BeforeEach
	public void setup() {
		cache = new ServiceCache<>(new MyServiceFactory());
		requestA = new ApiGatewayRequest();
		requestA.setStageVariables(singletonMap(MyServiceFactory.STAGE_VAR_CONFIG_VALUE_NAME, SETTING_VAL_A));
		requestB = new ApiGatewayRequest();
		requestB.setStageVariables(singletonMap(MyServiceFactory.STAGE_VAR_CONFIG_VALUE_NAME, SETTING_VAL_B));
	}

	@Test
	public void testServicesRegistered() {
		final MyServiceParams paramStage1ValA = cache.getParams(requestA, ENV_STAGE1);
		final MyServiceParams paramStage1ValB = cache.getParams(requestB, ENV_STAGE1);
		final MyServiceParams paramStage2ValA = cache.getParams(requestA, ENV_STAGE2);
		final MyServiceParams paramStage2ValB = cache.getParams(requestB, ENV_STAGE2);

		final MyServiceB serviceStage1ValA = cache.getService(MyServiceB.class, paramStage1ValA);
		final MyServiceB serviceStage1ValB = cache.getService(MyServiceB.class, paramStage1ValB);
		final MyServiceB serviceStage2ValA = cache.getService(MyServiceB.class, paramStage2ValA);
		final MyServiceB serviceStage2ValB = cache.getService(MyServiceB.class, paramStage2ValB);

		assertThat(serviceStage1ValA.getValue(), equalTo("Stage: " + STAGE1 + ", config: " + SETTING_VAL_A));
		assertThat(serviceStage1ValB.getValue(), equalTo("Stage: " + STAGE1 + ", config: " + SETTING_VAL_B));
		assertThat(serviceStage2ValA.getValue(), equalTo("Stage: " + STAGE2 + ", config: " + SETTING_VAL_A));
		assertThat(serviceStage2ValB.getValue(), equalTo("Stage: " + STAGE2 + ", config: " + SETTING_VAL_B));
	}
}
