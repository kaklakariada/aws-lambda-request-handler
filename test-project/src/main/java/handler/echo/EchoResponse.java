package handler.echo;

import com.github.kaklakariada.aws.lambda.request.ApiGatewayRequest;

public class EchoResponse {
    private final String message;
    private final ApiGatewayRequest request;
    private final EchoRequest body;

    public EchoResponse(String message, EchoRequest body, ApiGatewayRequest request) {
        this.message = message;
        this.body = body;
        this.request = request;
    }

    public String getMessage() {
        return message;
    }

    public EchoRequest getBody() {
        return body;
    }

    public ApiGatewayRequest getRequest() {
        return request;
    }
}