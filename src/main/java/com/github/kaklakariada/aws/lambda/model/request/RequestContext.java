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

public class RequestContext {
	private String path;
	private String accountId;
	private String resourceId;
	private String stage;
	private String requestId;
	private Identity identity;
	private String resourcePath;
	private String httpMethod;
	private String apiId;

	public String getPath() {
		return path;
	}

	public String getAccountId() {
		return accountId;
	}

	public String getResourceId() {
		return resourceId;
	}

	public String getStage() {
		return stage;
	}

	public String getRequestId() {
		return requestId;
	}

	public Identity getIdentity() {
		return identity;
	}

	public String getResourcePath() {
		return resourcePath;
	}

	public String getHttpMethod() {
		return httpMethod;
	}

	public String getApiId() {
		return apiId;
	}

	@Override
	public String toString() {
		return "RequestContext [path=" + path + ", accountId=" + accountId + ", resourceId=" + resourceId + ", stage="
				+ stage + ", requestId=" + requestId + ", identity=" + identity + ", resourcePath=" + resourcePath
				+ ", httpMethod=" + httpMethod + ", apiId=" + apiId + "]";
	}
}
