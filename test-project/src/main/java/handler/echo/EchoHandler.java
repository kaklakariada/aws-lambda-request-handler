package handler.echo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.services.lambda.runtime.Context;
import com.github.kaklakariada.aws.lambda.LambdaRequestHandler;
import com.github.kaklakariada.aws.lambda.controller.LambdaController;
import com.github.kaklakariada.aws.lambda.controller.RequestBody;
import com.github.kaklakariada.aws.lambda.controller.RequestHandlerMethod;
import com.github.kaklakariada.aws.lambda.model.request.ApiGatewayRequest;

public class EchoHandler extends LambdaRequestHandler {

    private static final Logger LOG = LoggerFactory.getLogger(EchoHandler.class);

    public EchoHandler() {
        super(new EchoController());
    }

    public static class EchoController implements LambdaController {
        @RequestHandlerMethod
        public EchoResponse handleRequest(@RequestBody EchoRequest body, Context context, ApiGatewayRequest request) {
            LOG.info("Request body: {}", body);
            LOG.info("Context     : {}", context);
            LOG.info("Request     : {}", request);
            return new EchoResponse("empty", body, request);
        }
    }
}
