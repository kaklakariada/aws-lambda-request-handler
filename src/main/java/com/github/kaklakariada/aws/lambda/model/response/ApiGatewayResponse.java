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

import java.util.Base64;
import java.util.Collections;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ApiGatewayResponse {
	private final int statusCode;
	private final Map<String, String> headers;
	private final String body;
	private final boolean base64Encoded;

	public ApiGatewayResponse(int statusCode, Map<String, String> headers, String body) {
		this(statusCode, headers, body, false);
	}

	private ApiGatewayResponse(int statusCode, Map<String, String> headers, String body, boolean base64Encoded) {
		this.statusCode = statusCode;
		this.headers = headers;
		this.body = body;
		this.base64Encoded = base64Encoded;
	}

	public static ApiGatewayResponse ok(String body) {
		return new ApiGatewayResponse(200, Collections.emptyMap(), body);
	}

	public static ApiGatewayResponse ok(byte[] body) {
		return new ApiGatewayResponse(200, Collections.emptyMap(), base64Encode(body), true);
	}

	private static String base64Encode(byte[] body) {
		return Base64.getEncoder().encodeToString(body);
	}

	public int getStatusCode() {
		return statusCode;
	}

	public Map<String, String> getHeaders() {
		return headers;
	}

	public String getBody() {
		return body;
	}

	@JsonProperty("isBase64Encoded")
	public boolean isBase64Encoded() {
		return base64Encoded;
	}
}
