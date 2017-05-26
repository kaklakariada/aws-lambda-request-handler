package com.github.kaklakariada.aws.lambda.arg;

import com.amazonaws.services.lambda.runtime.Context;
import com.github.kaklakariada.aws.lambda.request.ApiGatewayRequest;

@FunctionalInterface
public interface AllArgValueAdapter {
	Object[] getArgumentValue(ApiGatewayRequest request, Object body, Context context);
}
