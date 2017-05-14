package handler.echo;

import com.github.kaklakariada.aws.lambda.LambdaRequestHandler;
import com.github.kaklakariada.aws.lambda.controller.LambdaController;
import com.github.kaklakariada.aws.lambda.controller.RequestHandlerMethod;

public class EchoHandler extends LambdaRequestHandler<EchoRequest, EchoResponse> {

    public EchoHandler() {
        super(new EchoController(), EchoRequest.class, EchoResponse.class);
    }

    public static class EchoController implements LambdaController<EchoRequest, EchoResponse> {
        @RequestHandlerMethod
        public EchoResponse handleRequest() {
            return new EchoResponse("empty");
        }
    }
}
