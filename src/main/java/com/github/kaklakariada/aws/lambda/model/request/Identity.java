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
package com.github.kaklakariada.aws.lambda.model.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Identity {
	private String cognitoIdentityPoolId;
	private String accountId;
	private String cognitoIdentityId;
	private String caller;
	private String apiKey;
	private String sourceIp;
	private String cognitoAuthenticationType;
	private String cognitoAuthenticationProvider;
	private String userArn;
	private String userAgent;
	private String user;
	private String accessKey;
	private String apiKeyId;

	public String getCognitoIdentityPoolId() {
		return cognitoIdentityPoolId;
	}

	public String getAccountId() {
		return accountId;
	}

	public String getCognitoIdentityId() {
		return cognitoIdentityId;
	}

	public String getCaller() {
		return caller;
	}

	public String getApiKey() {
		return apiKey;
	}

	public String getSourceIp() {
		return sourceIp;
	}

	public String getCognitoAuthenticationType() {
		return cognitoAuthenticationType;
	}

	public String getCognitoAuthenticationProvider() {
		return cognitoAuthenticationProvider;
	}

	public String getUserArn() {
		return userArn;
	}

	public String getUserAgent() {
		return userAgent;
	}

	public String getUser() {
		return user;
	}

	public String getAccessKey() {
		return accessKey;
	}

	public String getApiKeyId() {
		return apiKeyId;
	}

	@Override
	public String toString() {
		return "Identity [cognitoIdentityPoolId=" + cognitoIdentityPoolId + ", accountId=" + accountId
				+ ", cognitoIdentityId=" + cognitoIdentityId + ", caller=" + caller + ", apiKey=" + apiKey
				+ ", sourceIp=" + sourceIp + ", cognitoAuthenticationType=" + cognitoAuthenticationType
				+ ", cognitoAuthenticationProvider=" + cognitoAuthenticationProvider + ", userArn=" + userArn
				+ ", userAgent=" + userAgent + ", user=" + user + ", accessKey=" + accessKey + ", apiKeyId=" + apiKeyId
				+ "]";
	}
}
