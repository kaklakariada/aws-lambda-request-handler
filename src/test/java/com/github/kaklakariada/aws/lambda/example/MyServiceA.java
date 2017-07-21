package com.github.kaklakariada.aws.lambda.example;

public class MyServiceA {
	private final String stage;

	public MyServiceA(String stage) {
		this.stage = stage;
	}

	public String getStage() {
		return stage;
	}
}