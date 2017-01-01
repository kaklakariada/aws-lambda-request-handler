/**
 * aws-lambda-request-handler - Request handler for AWS Lambda Proxy model
 * Copyright (C) 2015 Christoph Pirkl <christoph at users.sourceforge.net>
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
package com.github.kaklakariada.aws.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.github.kaklakariada.aws.lambda.exception.LambdaException;

public class ErrorResponseBody {

	private final int statusCode;
	private final String errorMessage;
	private final String awsRequestId;
	private final String logStreamName;

	private ErrorResponseBody(int statusCode, String errorMessage, String awsRequestId, String logGroupName,
			String logStreamName) {
		this.statusCode = statusCode;
		this.errorMessage = errorMessage;
		this.awsRequestId = awsRequestId;
		this.logStreamName = logStreamName;
	}

	public static ErrorResponseBody create(LambdaException e, Context context) {
		return new ErrorResponseBody(e.getErrorCode(), e.getErrorMessage(), context.getAwsRequestId(),
				context.getLogGroupName(), context.getLogStreamName());
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public int getStatusCode() {
		return statusCode;
	}

	public String getAwsRequestId() {
		return awsRequestId;
	}

	public String getLogStreamName() {
		return logStreamName;
	}
}
