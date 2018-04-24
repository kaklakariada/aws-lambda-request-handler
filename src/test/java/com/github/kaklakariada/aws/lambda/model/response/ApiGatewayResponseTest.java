package com.github.kaklakariada.aws.lambda.model.response;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNull.nullValue;

import org.junit.jupiter.api.Test;

class ApiGatewayResponseTest {

	@Test
	void testConstructorWithBody() {
		final ApiGatewayResponse response = new ApiGatewayResponse("body");
		assertThat(response.getBody(), equalTo("body"));
		assertThat(response.getStatusCode(), equalTo(200));
		assertThat(response.getHeaders().size(), equalTo(0));
	}

	@Test
	void testNullBody() {
		assertThat(new ApiGatewayResponse(null).getBody(), nullValue());
	}
}
