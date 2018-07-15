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

import static java.util.Collections.emptyMap;

import java.util.Map;

public class RequestContext {
	private String path;
	private String accountId;
	private String resourceId;
	private String stage;
	private String requestId;
	private String extendedRequestId;
	private Identity identity;
	private String resourcePath;
	private String httpMethod;
	private String apiId;
	// 22/Apr/2018:14:34:13 +0000
	private String requestTime;
	private String protocol;
	private Long requestTimeEpoch;
	private Map<String, String> authorizer;

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

	public String getExtendedRequestId() {
		return extendedRequestId;
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

	public String getRequestTime() {
		return requestTime;
	}

	public String getProtocol() {
		return protocol;
	}

	public Long getRequestTimeEpoch() {
		return requestTimeEpoch;
	}

	public Map<String, String> getAuthorizer() {
		return authorizer != null ? authorizer : emptyMap();
	}

	public String getAuthorizer(String field) {
		return getAuthorizer().get(field);
	}

	public void setPath(String path) {
		this.path = path;
	}

	public void setAccountId(String accountId) {
		this.accountId = accountId;
	}

	public void setResourceId(String resourceId) {
		this.resourceId = resourceId;
	}

	public void setStage(String stage) {
		this.stage = stage;
	}

	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}

	public void setExtendedRequestId(String extendedRequestId) {
		this.extendedRequestId = extendedRequestId;
	}

	public void setIdentity(Identity identity) {
		this.identity = identity;
	}

	public void setResourcePath(String resourcePath) {
		this.resourcePath = resourcePath;
	}

	public void setHttpMethod(String httpMethod) {
		this.httpMethod = httpMethod;
	}

	public void setApiId(String apiId) {
		this.apiId = apiId;
	}

	public void setRequestTime(String requestTime) {
		this.requestTime = requestTime;
	}

	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}

	public void setRequestTimeEpoch(Long requestTimeEpoch) {
		this.requestTimeEpoch = requestTimeEpoch;
	}

	public void setAuthorizer(Map<String, String> authorizer) {
		this.authorizer = authorizer;
	}

	@Override
	public String toString() {
		return "RequestContext [path=" + path + ", accountId=" + accountId + ", resourceId=" + resourceId + ", stage="
				+ stage + ", requestId=" + requestId + ", extendedRequestId=" + extendedRequestId + ", identity="
				+ identity + ", resourcePath=" + resourcePath + ", httpMethod=" + httpMethod + ", apiId=" + apiId
				+ ", requestTime=" + requestTime + ", protocol=" + protocol + ", requestTimeEpoch=" + requestTimeEpoch
				+ ", authorizer=" + authorizer + "]";
	}
}
