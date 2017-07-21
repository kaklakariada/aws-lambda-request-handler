package com.github.kaklakariada.aws.lambda.example;

import java.util.Map;

import com.github.kaklakariada.aws.lambda.model.request.ApiGatewayRequest;
import com.github.kaklakariada.aws.lambda.service.ServiceFactory;
import com.github.kaklakariada.aws.lambda.service.ServiceInitializer;
import com.github.kaklakariada.aws.lambda.service.ServiceRegistry;

public class MyServiceFactory implements ServiceFactory<MyServiceParams> {

	public static final String STAGE_ENV_VARIABLE = "stage";
	public static final String STAGE_VAR_CONFIG_VALUE_NAME = "configValue";

	@Override
	public MyServiceParams extractServiceParams(ApiGatewayRequest request, Map<String, String> env) {
		return new MyServiceParams(env.get(STAGE_ENV_VARIABLE),
				request.getStageVariables().get(STAGE_VAR_CONFIG_VALUE_NAME));
	}

	@Override
	public void registerServices(ServiceRegistry registry, MyServiceParams params) {
		final ServiceInitializer<MyServiceA> serviceA = registry.init(() -> new MyServiceA(params.getStage()));
		registry.addService(MyServiceA.class, serviceA);
		registry.addService(MyServiceB.class, () -> new MyServiceB(serviceA.get(), params.getConfigValue()));
	}
}