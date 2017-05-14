package com.github.kaklakariada.aws.lambda.exception;

public class ConfigurationErrorException extends InternalServerErrorException {

	private static final long serialVersionUID = 1L;

	public ConfigurationErrorException(String errorMessage) {
		super(errorMessage, null);
	}
}
