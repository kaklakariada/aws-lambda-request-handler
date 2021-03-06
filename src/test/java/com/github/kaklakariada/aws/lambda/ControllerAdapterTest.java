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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.lang.reflect.Parameter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.amazonaws.services.lambda.runtime.Context;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.kaklakariada.aws.lambda.controller.LambdaController;
import com.github.kaklakariada.aws.lambda.controller.RequestHandlerMethod;
import com.github.kaklakariada.aws.lambda.exception.ConfigurationErrorException;
import com.github.kaklakariada.aws.lambda.exception.InternalServerErrorException;
import com.github.kaklakariada.aws.lambda.model.request.ApiGatewayRequest;

public class ControllerAdapterTest {

	@Mock
	private ApiGatewayRequest apiGatewayRequestMock;
	@Mock
	private Context contextMock;
	@Mock
	private TestResponse responseMock;

	private ObjectMapper objectMapper;

	@BeforeEach
	public void setup() {
		MockitoAnnotations.initMocks(this);
		objectMapper = new ObjectMapper();
	}

	@Test
	public void testNoHandlerMethod() {
		assertHandlerMethodConfigError(new LambdaControllerMissingHandlerMethod());
	}

	@Test
	public void testHandlerMissingHandlerMethodAnnotation() {
		assertHandlerMethodConfigError(new LambdaControllerMissingHandlerMethodAnnotation());
	}

	@Test
	public void testTwoHandlerMethods() {
		assertHandlerMethodConfigError(new LambdaControllerTwoHandlerMethods());
	}

	@Test
	public void testHandlerMethodPackagePrivate() {
		assertHandlerMethodConfigError(new LambdaControllerWithPackagePrivateHandlerMethod());
	}

	@Test
	public void testHandlerMethodPrivate() {
		assertHandlerMethodConfigError(new LambdaControllerWithPrivateHandlerMethod());
	}

	@Test
	public void testHandlerMethodProtected() {
		assertHandlerMethodConfigError(new LambdaControllerWithProtectedHandlerMethod());
	}

	@Test
	public void testVoidHandlerMethodReturnsNull() {
		final LambdaControllerVoidHandlerMethod controllerMock = mock(LambdaControllerVoidHandlerMethod.class);
		final Object result = create(controllerMock).handleRequest(apiGatewayRequestMock, contextMock);
		assertNull(result);
		verify(controllerMock).handler1();
	}

	@Test
	public void testHandlerMethodWithInvalidArguments() throws NoSuchMethodException, SecurityException {
		final Parameter param = LambdaControllerInvalidArgumentType.class.getMethod("handler1", String.class)
				.getParameters()[0];
		assertConfigError(new LambdaControllerInvalidArgumentType(),
				"None of the arg adapter factories supports parameter '" + param + "'");
	}

	@Test
	public void testHandlerMethodMatchingReturnReturnType() {
		final LambdaControllerValidReturnType controllerMock = mock(LambdaControllerValidReturnType.class);
		when(controllerMock.handler1()).thenReturn(responseMock);
		assertSame(responseMock, executeHandlerMethod(controllerMock));
		verify(controllerMock).handler1();
	}

	private Object executeHandlerMethod(final LambdaController controllerMock) {
		final ControllerAdapter adapter = create(controllerMock);
		return adapter.handleRequest(apiGatewayRequestMock, contextMock);
	}

	@Test
	public void testHandlerMethodThrowsException() {
		final LambdaControllerValidReturnType controllerMock = mock(LambdaControllerValidReturnType.class);
		final RuntimeException cause = new RuntimeException("expected");
		when(controllerMock.handler1()).thenThrow(cause);

		final ControllerAdapter adapter = create(controllerMock);

		final InternalServerErrorException exception = assertThrows(InternalServerErrorException.class,
				() -> adapter.handleRequest(apiGatewayRequestMock, contextMock));
		assertSame(cause, exception.getCause().getCause());
		assertEquals("Error invoking handler method", exception.getErrorMessage());
	}

	private ControllerAdapter create(final LambdaController controllerMock) {
		return ControllerAdapter.create(objectMapper, controllerMock);
	}

	private void assertHandlerMethodConfigError(final LambdaController controller) {
		final String expectedErrorMessage = "Class " + controller.getClass().getName()
				+ " must have exactly one public method annotated with " + RequestHandlerMethod.class.getName();
		assertConfigError(controller, expectedErrorMessage);
	}

	private void assertConfigError(final LambdaController controller, String expectedErrorMessage) {
		final ConfigurationErrorException exception = assertThrows(ConfigurationErrorException.class,
				() -> create(controller));

		assertEquals(expectedErrorMessage, exception.getErrorMessage());
	}

	private static class LambdaControllerMissingHandlerMethod implements LambdaController {
	}

	private static class LambdaControllerMissingHandlerMethodAnnotation implements LambdaController {
		@SuppressWarnings("unused")
		public void handler() {
		}
	}

	private static class LambdaControllerTwoHandlerMethods implements LambdaController {
		@RequestHandlerMethod
		public void handler1() {
		}

		@RequestHandlerMethod
		public void handler2() {
		}
	}

	private static class LambdaControllerVoidHandlerMethod implements LambdaController {
		@RequestHandlerMethod
		public void handler1() {
		}
	}

	private static class LambdaControllerValidReturnType implements LambdaController {
		@RequestHandlerMethod
		public TestResponse handler1() {
			return null;
		}
	}

	private static class LambdaControllerInvalidArgumentType implements LambdaController {
		@RequestHandlerMethod
		public TestResponse handler1(String arg) {
			return null;
		}
	}

	private static class LambdaControllerWithPrivateHandlerMethod implements LambdaController {
		@RequestHandlerMethod
		private TestResponse handler1() {
			return null;
		}
	}

	private static class LambdaControllerWithPackagePrivateHandlerMethod implements LambdaController {
		@RequestHandlerMethod
		TestResponse handler1() {
			return null;
		}
	}

	private static class LambdaControllerWithProtectedHandlerMethod implements LambdaController {
		@RequestHandlerMethod
		protected TestResponse handler1() {
			return null;
		}
	}

	private static class BaseTestResponse {
	}

	private static class TestResponse extends BaseTestResponse {
	}
}
