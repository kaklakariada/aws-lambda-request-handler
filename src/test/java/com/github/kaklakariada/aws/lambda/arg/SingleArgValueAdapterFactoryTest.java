package com.github.kaklakariada.aws.lambda.arg;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.amazonaws.services.lambda.runtime.Context;
import com.github.kaklakariada.aws.lambda.controller.RequestBody;
import com.github.kaklakariada.aws.lambda.exception.ConfigurationErrorException;
import com.github.kaklakariada.aws.lambda.request.ApiGatewayRequest;

@RunWith(JUnitPlatform.class)
public class SingleArgValueAdapterFactoryTest {

	private SingleArgValueAdapterFactory factory;

	@Mock
	private ApiGatewayRequest apiGatewayRequestMock;
	@Mock
	private Object requestBodyMock;
	@Mock
	private Context contextMock;

	@BeforeEach
	public void setup() {
		MockitoAnnotations.initMocks(this);
		factory = new SingleArgValueAdapterFactory(TestRequest.class);
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
		final Parameter param = getParam("methodWithStringParam", String.class);
		assertConfigurationError(param, "Could not find adapter for parameter " + param + " of handler method");
	}

	void methodWithStringParam(String value) {

	}

	@Test
	public void testBodyParamCorrectType() {
		assertSame(requestBodyMock, runAdapter(getParam("methodWithBodyTypeParam", TestRequest.class)));
	}

	void methodWithBodyTypeParam(@RequestBody TestRequest body) {

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
		return factory.getAdapter(param).getArgumentValue(apiGatewayRequestMock, requestBodyMock, contextMock);
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
