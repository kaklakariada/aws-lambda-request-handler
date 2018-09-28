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

public class ApiGatewayRequest {

	private String resource;
	private String path;
	private HttpMethod httpMethod;
	private Map<String, String> headers;
	private Map<String, String> queryStringParameters;
	private Map<String, String> pathParameters;
	private Map<String, String> stageVariables;
	private RequestContext requestContext;
	private String body;
	private boolean isBase64Encoded;

	public String getResource() {
		return resource;
	}

	public String getPath() {
		return path;
	}

	public HttpMethod getHttpMethod() {
		return httpMethod;
	}

	public Map<String, String> getHeaders() {
		return headers != null ? headers : emptyMap();
	}

	public String getHeader(String header) {
		return getHeaders().get(header);
	}

	public Map<String, String> getQueryStringParameters() {
		return queryStringParameters != null ? queryStringParameters : emptyMap();
	}

	public String getQueryStringParameter(String parameter) {
		return getQueryStringParameters().get(parameter);
	}

	public Map<String, String> getPathParameters() {
		return pathParameters != null ? pathParameters : emptyMap();
	}

	public String getPathParameter(String parameter) {
		return getPathParameters().get(parameter);
	}

	public Map<String, String> getStageVariables() {
		return stageVariables != null ? stageVariables : emptyMap();
	}

	public String getStageVariable(String variable) {
		return getStageVariables().get(variable);
	}

	public RequestContext getRequestContext() {
		return requestContext;
	}

	public String getBody() {
		return body;
	}

	public boolean getIsBase64Encoded() {
		return isBase64Encoded;
	}

	public void setResource(String resource) {
		this.resource = resource;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public void setHttpMethod(HttpMethod httpMethod) {
		this.httpMethod = httpMethod;
	}

	public void setHeaders(Map<String, String> headers) {
		this.headers = headers;
	}

	public void setQueryStringParameters(Map<String, String> queryStringParameters) {
		this.queryStringParameters = queryStringParameters;
	}

	public void setPathParameters(Map<String, String> pathParameters) {
		this.pathParameters = pathParameters;
	}

	public void setStageVariables(Map<String, String> stageVariables) {
		this.stageVariables = stageVariables;
	}

	public void setRequestContext(RequestContext requestContext) {
		this.requestContext = requestContext;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public void setBase64Encoded(boolean isBase64Encoded) {
		this.isBase64Encoded = isBase64Encoded;
	}

	@Override
	public String toString() {
		return "ApiGatewayRequest [resource=" + resource + ", path=" + path + ", httpMethod=" + httpMethod
				+ ", headers=" + headers + ", queryStringParameters=" + queryStringParameters + ", stageVariables="
				+ stageVariables + ", requestContext=" + requestContext + ", body=" + body + ", isBase64Encoded="
				+ isBase64Encoded + "]";
	}
}
