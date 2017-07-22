package com.github.kaklakariada.aws.lambda.listener;

import com.amazonaws.services.lambda.runtime.Context;
import com.github.kaklakariada.aws.lambda.model.request.ApiGatewayRequest;
import com.github.kaklakariada.aws.lambda.model.response.ApiGatewayResponse;

public interface RequestProcessingListener {

	void beforeRequest(ApiGatewayRequest request, Context context);

	void afterRequest(ApiGatewayRequest request, ApiGatewayResponse response, Context context);

}
