package com.github.kaklakariada.aws.lambda.model.request;

import static java.util.Collections.singletonMap;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNull.nullValue;
import static org.hamcrest.core.IsSame.sameInstance;

import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ApiGatewayRequestTest {

	private static final String KEY = "key";
	private static final String VALUE = "value";
	private ApiGatewayRequest apiGatewayRequest;
	private Map<String, String> map;

	@BeforeEach
	public void setup() {
		apiGatewayRequest = new ApiGatewayRequest();
		map = singletonMap(KEY, VALUE);
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
}
