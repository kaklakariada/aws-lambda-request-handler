package handler.service;

import java.util.Map;

import com.github.kaklakariada.aws.lambda.model.request.ApiGatewayRequest;
import com.github.kaklakariada.aws.lambda.service.ServiceFactory;
import com.github.kaklakariada.aws.lambda.service.ServiceRegistry;

public class EchoServiceFactory implements ServiceFactory<EchoServiceParam> {

	@Override
	public EchoServiceParam extractServiceParams(ApiGatewayRequest request, Map<String, String> env) {
		return new EchoServiceParam(request.getStageVariable("stage"), env.get("CODE_URI"));
	}

	@Override
	public void registerServices(ServiceRegistry registry, EchoServiceParam params) {
		registry.addService(StageBasedEchoService.class,
				() -> new StageBasedEchoService(params.getStage(), params.getCodeUri()));
	}
}