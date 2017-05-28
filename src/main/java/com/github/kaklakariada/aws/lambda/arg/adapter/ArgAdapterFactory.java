package com.github.kaklakariada.aws.lambda.arg.adapter;

import java.lang.annotation.Annotation;
import java.lang.reflect.Parameter;

import com.github.kaklakariada.aws.lambda.arg.SingleArgValueAdapter;

public abstract class ArgAdapterFactory {

	public Class<?> getSupportedArgType() {
		return null;
	}

	public Class<? extends Annotation> getSupportedArgAnnotation() {
		return null;
	}

	public boolean supports(Parameter param) {
		final Class<?> supportedArgType = getSupportedArgType();
		final Class<? extends Annotation> supportedArgAnnotation = getSupportedArgAnnotation();

		if (supportedArgType == null && supportedArgAnnotation == null) {
			throw new IllegalStateException("Neither supportedArgType nor supportedArgAnnotation given");
		}

		if (supportedArgType != null && supportedArgAnnotation == null) {
			return argTypeSupported(param, supportedArgType);
		}
		final boolean annotationSupported = param.getAnnotation(supportedArgAnnotation) != null;
		if (supportedArgType != null && supportedArgAnnotation != null) {
			return argTypeSupported(param, supportedArgType) && annotationSupported;
		}
		return annotationSupported;
	}

	private boolean argTypeSupported(Parameter param, final Class<?> supportedArgType) {
		return supportedArgType.isAssignableFrom(param.getType());
	}

	public abstract SingleArgValueAdapter createAdapter(Parameter param);
}
