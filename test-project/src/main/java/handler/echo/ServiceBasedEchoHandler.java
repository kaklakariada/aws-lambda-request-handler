package handler.echo;

import java.util.function.Supplier;

import javax.inject.Inject;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.github.kaklakariada.aws.lambda.LambdaRequestHandler;
import com.github.kaklakariada.aws.lambda.controller.LambdaController;
import com.github.kaklakariada.aws.lambda.controller.RequestHandlerMethod;
import com.github.kaklakariada.aws.lambda.model.request.ApiGatewayRequest;

import handler.service.EchoServiceFactory;
import handler.service.StageBasedEchoService;

public class ServiceBasedEchoHandler extends LambdaRequestHandler {

	private static final Logger LOG = LogManager.getLogger(ServiceBasedEchoHandler.class);

	public ServiceBasedEchoHandler() {
		super(new ServiceBasedEchoController(), new EchoServiceFactory());
	}

	public static class ServiceBasedEchoController implements LambdaController {
		@Inject
		private Supplier<StageBasedEchoService> echoService;

		@RequestHandlerMethod
		public String echo(ApiGatewayRequest request) {
			LOG.info("Code uri: {}", echoService.get().getCodeUri());
			return echoService.get().echo(request.getPath());
		}
	}
}
