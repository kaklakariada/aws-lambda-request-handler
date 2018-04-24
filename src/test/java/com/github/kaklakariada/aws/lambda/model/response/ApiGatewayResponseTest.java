package com.github.kaklakariada.aws.lambda.model.response;

import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThat;

import org.junit.jupiter.api.Test;

public class ApiGatewayResponseTest {

	@Test
	void testNullBody() {
		assertThat(new ApiGatewayResponse(null).getBody(), nullValue());
	}
}
