package com.github.kaklakariada.aws.lambda.example;

public class MyServiceB {
	private final MyServiceA serviceA;
	private final String configValue;

	public MyServiceB(MyServiceA serviceA, String configValue) {
		this.serviceA = serviceA;
		this.configValue = configValue;
	}

	public String getValue() {
		return "Stage: " + serviceA.getStage() + ", config: " + configValue;
	}
}