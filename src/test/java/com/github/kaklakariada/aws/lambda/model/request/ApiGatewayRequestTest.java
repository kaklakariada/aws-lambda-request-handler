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
package com.github.kaklakariada.aws.lambda.model.request;

import static java.util.Collections.singletonMap;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNull.nullValue;
import static org.hamcrest.core.IsSame.sameInstance;

import java.io.IOException;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

class ApiGatewayRequestTest {

	private static final String KEY = "key";
	private static final String VALUE = "value";
	private ApiGatewayRequest apiGatewayRequest;
	private Map<String, String> map;
	private ObjectMapper objectMapper;

	@BeforeEach
	public void setup() {
		apiGatewayRequest = new ApiGatewayRequest();
		map = singletonMap(KEY, VALUE);
		objectMapper = new ObjectMapper();
	}

	@Test
	void testNullHeadersReturnsEmptyMap() {
		apiGatewayRequest.setHeaders(null);
		assertThat(apiGatewayRequest.getHeaders().size(), equalTo(0));
		assertThat(apiGatewayRequest.getHeader(KEY), nullValue());
	}

	@Test
	void testNotNullHeaders() {
		apiGatewayRequest.setHeaders(map);
		assertThat(apiGatewayRequest.getHeaders(), sameInstance(map));
		assertThat(apiGatewayRequest.getHeader(KEY), equalTo(VALUE));
	}

	@Test
	void testNullQueryStringParametersReturnsEmptyMap() {
		apiGatewayRequest.setQueryStringParameters(null);
		assertThat(apiGatewayRequest.getQueryStringParameters().size(), equalTo(0));
		assertThat(apiGatewayRequest.getQueryStringParameter(KEY), nullValue());
	}

	@Test
	void testNotNullQueryStringParameters() {
		apiGatewayRequest.setQueryStringParameters(map);
		assertThat(apiGatewayRequest.getQueryStringParameters(), sameInstance(map));
		assertThat(apiGatewayRequest.getQueryStringParameter(KEY), equalTo(VALUE));
	}

	@Test
	void testNullPathParametersReturnsEmptyMap() {
		apiGatewayRequest.setPathParameters(null);
		assertThat(apiGatewayRequest.getPathParameters().size(), equalTo(0));
		assertThat(apiGatewayRequest.getPathParameter(KEY), nullValue());
	}

	@Test
	void testNotNullPathParameters() {
		apiGatewayRequest.setPathParameters(map);
		assertThat(apiGatewayRequest.getPathParameters(), sameInstance(map));
		assertThat(apiGatewayRequest.getPathParameter(KEY), equalTo(VALUE));
	}

	@Test
	void testNullStageVariablesReturnsEmptyMap() {
		apiGatewayRequest.setStageVariables(null);
		assertThat(apiGatewayRequest.getStageVariables().size(), equalTo(0));
		assertThat(apiGatewayRequest.getStageVariable(KEY), nullValue());
	}

	@Test
	void testNotNullStageVariables() {
		apiGatewayRequest.setStageVariables(map);
		assertThat(apiGatewayRequest.getStageVariables(), sameInstance(map));
		assertThat(apiGatewayRequest.getStageVariable(KEY), equalTo(VALUE));
	}

	@Test
	void testParseRequest() {
		final ApiGatewayRequest request = parse(
				"{ \"resource\": \"/trace\", \"path\": \"/trace\", \"httpMethod\": \"POST\", "
						+ "\"headers\": { \"accept\": \"*/*\", \"Host\": \"testHost\", \"User-Agent\": \"testUserAgent\", \"X-Amzn-Trace-Id\": \"Root=1-Trace-Id\", \"X-Forwarded-For\": \"testXForwardedFor\", \"X-Forwarded-Port\": \"testXForwardedForPort\", \"X-Forwarded-Proto\": \"https\" }, "
						+ "\"multiValueHeaders\": { \"accept\": [ \"*/*\" ], \"Host\": [ \"testHostMulti\" ], \"User-Agent\": [ \"testUserAgentMulti\" ], \"X-Amzn-Trace-Id\": [ \"Root=1-Trace-Id\" ], \"X-Forwarded-For\": [ \"testXForwardedForMulti\" ], \"X-Forwarded-Port\": [ \"testXForwardedForPortMulti\" ], \"X-Forwarded-Proto\": [ \"https\" ] }, "
						+ "\"queryStringParameters\": null, \"multiValueQueryStringParameters\": null, \"pathParameters\": null, \"stageVariables\": { \"stage\": \"testStageVariableStage\" }, "
						+ "\"requestContext\": { \"resourceId\": \"testResourceId\", \"resourcePath\": \"/testResourcePath\", \"httpMethod\": \"POST\", \"extendedRequestId\": \"extendedRequestId\", \"requestTime\": \"06/Jan/2019:11:56:29 +0000\", \"path\": \"/test/path\", \"accountId\": \"testAccountId\", \"protocol\": \"HTTP/1.1\", \"stage\": \"testStage\", \"domainPrefix\": \"testDomainPrefix\", \"requestTimeEpoch\": 1546775789335, \"requestId\": \"testRequestId\", \"identity\": { \"cognitoIdentityPoolId\": null, \"accountId\": null, \"cognitoIdentityId\": null, \"caller\": null, \"sourceIp\": \"sourceIp\", \"accessKey\": null, \"cognitoAuthenticationType\": null, \"cognitoAuthenticationProvider\": null, \"userArn\": null, \"userAgent\": \"testUserAgent\", \"user\": null }, \"domainName\": \"apiId.execute-api.eu-west-1.amazonaws.com\", \"apiId\": \"testApiId\" }, \"body\": \"testBody\", \"isBase64Encoded\": false }");

		assertThat(request.getBody(), equalTo("testBody"));
	}

	private ApiGatewayRequest parse(String json) {
		try {
			return objectMapper.readValue(json, ApiGatewayRequest.class);
		} catch (final IOException e) {
			throw new AssertionError("Parsing json failed: " + e.getMessage(), e);
		}
	}
}
