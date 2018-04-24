package com.github.kaklakariada.aws.lambda.model.response;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsNull.nullValue;

import org.junit.jupiter.api.Test;

class ApiGatewayResponseTest {

	@Test
	void testNullBody() {
		assertThat(new ApiGatewayResponse(null).getBody(), nullValue());
	}
}
