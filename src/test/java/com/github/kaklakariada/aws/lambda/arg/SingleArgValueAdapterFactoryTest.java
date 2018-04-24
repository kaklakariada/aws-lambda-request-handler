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

import static java.util.Collections.singletonMap;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

import org.hamcrest.Matcher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.amazonaws.services.lambda.runtime.Context;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.kaklakariada.aws.lambda.controller.PathParameter;
import com.github.kaklakariada.aws.lambda.controller.QueryStringParameter;
import com.github.kaklakariada.aws.lambda.controller.RequestBody;
import com.github.kaklakariada.aws.lambda.exception.ConfigurationErrorException;
import com.github.kaklakariada.aws.lambda.model.request.ApiGatewayRequest;

public class SingleArgValueAdapterFactoryTest {

	private static final String QUERY_STRING_PARAM_VALUE = "queryStringParamValue";
	private static final String QUERY_STRING_PARAM_NAME = "queryStringParamName";
	private static final String PATH_PARAM_NAME = "pathParamName";
	private static final String PATH_PARAM_VALUE = "pathParamValue";
	private static final String BODY_CONTENT = "request body content";

	private SingleArgValueAdapterFactory factory;

	@Mock
	private ApiGatewayRequest apiGatewayRequestMock;
	@Mock
	private Context contextMock;
	@Mock
	private ObjectMapper objectMapperMock;
	@Mock
	private TestRequest requestBodyMock;

	@BeforeEach
	public void setup() throws JsonParseException, JsonMappingException, IOException {
		MockitoAnnotations.initMocks(this);
		factory = new SingleArgValueAdapterFactory(objectMapperMock);
		when(apiGatewayRequestMock.getQueryStringParameters())
				.thenReturn(singletonMap(QUERY_STRING_PARAM_NAME, QUERY_STRING_PARAM_VALUE));
		when(apiGatewayRequestMock.getPathParameters()).thenReturn(singletonMap(PATH_PARAM_NAME, PATH_PARAM_VALUE));
		when(apiGatewayRequestMock.getBody()).thenReturn(BODY_CONTENT);
		when(objectMapperMock.readValue(BODY_CONTENT, TestRequest.class)).thenReturn(requestBodyMock);
	}

	@Test
	public void testApiGatewayRequestParam() {
		assertSame(apiGatewayRequestMock,
				runAdapter(getParam("methodWithApiGatewayRequestParam", ApiGatewayRequest.class)));
	}

	void methodWithApiGatewayRequestParam(ApiGatewayRequest request) {

	}

	@Test
	public void testContextParam() {
		assertSame(contextMock, runAdapter(getParam("methodWithContextParam", Context.class)));
	}

	void methodWithContextParam(Context context) {

	}

	@Test
	public void testUnknownParamType() {
		assertConfigurationError(getParam("methodWithStringParam", String.class),
				startsWith("None of the arg adapter factories supports parameter"));
	}

	void methodWithStringParam(String value) {

	}

	@Test
	public void testBodyParamCorrectType() {
		assertSame(requestBodyMock, runAdapter(getParam("methodWithBodyTypeParam", TestRequest.class)));
	}

	void methodWithBodyTypeParam(@RequestBody TestRequest body) {

	}

	@Test
	public void testQueryStringParameter() {
		assertEquals(QUERY_STRING_PARAM_VALUE, runAdapter(getParam("methodWithQueryStringParam", String.class)));
	}

	void methodWithQueryStringParam(@QueryStringParameter(QUERY_STRING_PARAM_NAME) String value) {

	}

	@Test
	public void testQueryStringParameterWrongParamType() {
		assertConfigurationError(getParam("methodWithQueryStringParamWithWrongType", Integer.class),
				startsWith("None of the arg adapter factories supports parameter"));
	}

	void methodWithQueryStringParamWithWrongType(@QueryStringParameter(QUERY_STRING_PARAM_NAME) Integer value) {

	}

	@Test
	public void testPathParameterParam() {
		assertEquals(PATH_PARAM_VALUE, runAdapter(getParam("methodWithPathParam", String.class)));
	}

	void methodWithPathParam(@PathParameter(PATH_PARAM_NAME) String value) {

	}

	@Test
	public void testPathParameterWrongParamType() {
		assertConfigurationError(getParam("methodWithPathParamWithWrongType", Integer.class),
				startsWith("None of the arg adapter factories supports parameter"));
	}

	void methodWithPathParamWithWrongType(@PathParameter(PATH_PARAM_NAME) Integer value) {

	}

	private Parameter getParam(String methodName, Class<?> parameterType) {
		try {
			final Method method = this.getClass().getDeclaredMethod(methodName, parameterType);
			return method.getParameters()[0];
		} catch (SecurityException | NoSuchMethodException e) {
			throw new AssertionError(e);
		}
	}

	private Object runAdapter(Parameter param) {
		return factory.getAdapter(param).getArgumentValue(apiGatewayRequestMock, contextMock);
	}

	private void assertConfigurationError(Parameter param, Matcher<String> expectedErrorMessage) {
		final ConfigurationErrorException exception = assertConfigurationError(param);
		assertThat(exception.getErrorMessage(), expectedErrorMessage);
	}

	private ConfigurationErrorException assertConfigurationError(Parameter param) {
		final ConfigurationErrorException exception = assertThrows(ConfigurationErrorException.class, () -> {
			runAdapter(param);
		});
		return exception;
	}

	private static class TestRequest {
	}
}
