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
package com.github.kaklakariada.aws.lambda.model.response;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNull.nullValue;

import java.util.Base64;

import org.junit.jupiter.api.Test;

class ApiGatewayResponseTest {

	@Test
	void testConstructorWithStringBody() {
		final ApiGatewayResponse response = ApiGatewayResponse.ok("body");
		assertThat(response.getBody(), equalTo("body"));
		assertThat(response.getStatusCode(), equalTo(200));
		assertThat(response.getHeaders().size(), equalTo(0));
		assertThat(response.isBase64Encoded(), equalTo(false));
	}

	@Test
	void testNullStringBody() {
		assertThat(ApiGatewayResponse.ok((String) null).getBody(), nullValue());
	}

	@Test
	void testConstructorWithBinaryBody() {
		final byte[] body = "body".getBytes();
		final ApiGatewayResponse response = ApiGatewayResponse.ok(body);
		assertThat(response.getBody(), equalTo("Ym9keQ=="));
		assertThat(Base64.getDecoder().decode(response.getBody()), equalTo(body));
		assertThat(response.getStatusCode(), equalTo(200));
		assertThat(response.getHeaders().size(), equalTo(0));
		assertThat(response.isBase64Encoded(), equalTo(true));
	}
}
