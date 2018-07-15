/**
 * aws-lambda-request-handler - Request handler for AWS Lambda Proxy model
 * Copyright (C) 2017 Christoph Pirkl <christoph at users.sourceforge.net>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
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
		if (supportedArgType != null) {
			return argTypeSupported(param, supportedArgType) && annotationSupported;
		}
		return annotationSupported;
	}

	private boolean argTypeSupported(Parameter param, final Class<?> supportedArgType) {
		return supportedArgType.isAssignableFrom(param.getType());
	}

	public abstract SingleArgValueAdapter createAdapter(Parameter param);
}
