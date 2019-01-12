package handler.echo;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.amazonaws.services.lambda.runtime.Context;
import com.github.kaklakariada.aws.lambda.LambdaRequestHandler;
import com.github.kaklakariada.aws.lambda.controller.HeaderValue;
import com.github.kaklakariada.aws.lambda.controller.LambdaController;
import com.github.kaklakariada.aws.lambda.controller.RequestBody;
import com.github.kaklakariada.aws.lambda.controller.RequestHandlerMethod;
import com.github.kaklakariada.aws.lambda.model.request.ApiGatewayRequest;

public class EchoHandler extends LambdaRequestHandler {

	private static final Logger LOG = LogManager.getLogger(EchoHandler.class);

	public EchoHandler() {
		super(new EchoController());
	}

	public static class EchoController implements LambdaController {

		@RequestHandlerMethod
		public EchoResponse handleRequest(@RequestBody EchoRequest body, Context context, ApiGatewayRequest request,
				@HeaderValue("User-Agent") String userAgent) {
			final EchoResponse response = new EchoResponse("empty", body, request);
			LOG.info("Request body: {}\nContext: {}\nRequest: {}\nResponse: {}", body, context, request, response);
			LOG.info("User agent: {}", userAgent);
			return response;
		}
	}
}
