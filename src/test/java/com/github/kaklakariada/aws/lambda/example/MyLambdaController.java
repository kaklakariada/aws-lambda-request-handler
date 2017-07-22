package com.github.kaklakariada.aws.lambda.example;

import java.util.function.Supplier;

import javax.inject.Inject;

import com.github.kaklakariada.aws.lambda.controller.LambdaController;
import com.github.kaklakariada.aws.lambda.controller.RequestHandlerMethod;

public class MyLambdaController implements LambdaController {

	@Inject
	private Supplier<MyServiceB> serviceB;

	@RequestHandlerMethod
	public String handleRequest() {
		return "Service B: " + serviceB.get().getValue();
	}
}
