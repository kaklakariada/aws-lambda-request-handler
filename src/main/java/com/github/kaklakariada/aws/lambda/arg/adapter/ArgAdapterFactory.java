package com.github.kaklakariada.aws.lambda.arg.adapter;

import java.lang.annotation.Annotation;
import java.lang.reflect.Parameter;

import com.github.kaklakariada.aws.lambda.arg.SingleArgValueAdapter;

public interface ArgAdapterFactory {
	default Class<?> getSupportedArgType() {
		return null;
	}

	default Class<? extends Annotation> getSupportedArgAnnotation() {
		return null;
	}

	SingleArgValueAdapter createAdapter(Parameter param);
}
