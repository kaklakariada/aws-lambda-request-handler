package handler.echo;

import com.github.kaklakariada.aws.lambda.LambdaRequestHandler;
import com.github.kaklakariada.aws.lambda.controller.LambdaController;

public class EchoHandler extends LambdaRequestHandler<EchoRequest,EchoResponse>{

	protected EchoHandler() {
		super(new EchoController(), EchoRequest.class,EchoResponse.class);
	}
	
	public static class EchoController implements LambdaController<EchoRequest, EchoResponse>{
		
	}
}
