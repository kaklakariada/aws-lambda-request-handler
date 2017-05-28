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
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.amazonaws.services.lambda.runtime.Context;
import com.github.kaklakariada.aws.lambda.controller.PathParameter;
import com.github.kaklakariada.aws.lambda.controller.QueryStringParameter;
import com.github.kaklakariada.aws.lambda.controller.RequestBody;
import com.github.kaklakariada.aws.lambda.exception.ConfigurationErrorException;
import com.github.kaklakariada.aws.lambda.request.ApiGatewayRequest;

@RunWith(JUnitPlatform.class)
public class SingleArgValueAdapterFactoryTest {

	private static final String QUERY_STRING_PARAM_VALUE = "queryStringParamValue";
	private static final String QUERY_STRING_PARAM_NAME = "queryStringParamName";
	private static final String PATH_PARAM_NAME = "pathParamName";
	private static final String PATH_PARAM_VALUE = "pathParamValue";

	private SingleArgValueAdapterFactory factory;

	@Mock
	private ApiGatewayRequest apiGatewayRequestMock;
	@Mock
	private Context contextMock;

	@BeforeEach
	public void setup() {
		MockitoAnnotations.initMocks(this);
		factory = new SingleArgValueAdapterFactory();
		when(apiGatewayRequestMock.getQueryStringParameters())
				.thenReturn(singletonMap(QUERY_STRING_PARAM_NAME, QUERY_STRING_PARAM_VALUE));
		when(apiGatewayRequestMock.getPathParameters()).thenReturn(singletonMap(PATH_PARAM_NAME, PATH_PARAM_VALUE));
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
	public void testBodyParamWrongType() {
		assertConfigurationError(getParam("methodWithWrongBodyTypeParam", String.class),
				"Body argument of handler method java.lang.String is not compatible with request type "
						+ TestRequest.class.getName());
	}

	void methodWithWrongBodyTypeParam(@RequestBody String wrongParamType) {

	}

	@Test
	public void testUnknownParamType() {
		assertNoAdapterFound(getParam("methodWithStringParam", String.class));
	}

	void methodWithStringParam(String value) {

	}

	@Test
	public void testBodyParamCorrectType() {
		assertSame("body", runAdapter(getParam("methodWithBodyTypeParam", TestRequest.class)));
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
				"Argument of handler method java.lang.Integer annotated with " + QueryStringParameter.class.getName()
						+ " is not compatible with request type java.lang.String");
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
				"Argument of handler method java.lang.Integer annotated with " + PathParameter.class.getName()
						+ " is not compatible with request type java.lang.String");
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

	private void assertConfigurationError(Parameter param, String expectedErrorMessage) {
		final ConfigurationErrorException exception = assertThrows(ConfigurationErrorException.class, () -> {
			runAdapter(param);
		});
		assertEquals(expectedErrorMessage, exception.getErrorMessage());
	}

	private void assertNoAdapterFound(final Parameter param) {
		assertConfigurationError(param, "Could not find adapter for parameter " + param + " of handler method");
	}

	private static class TestRequest {
	}
}
