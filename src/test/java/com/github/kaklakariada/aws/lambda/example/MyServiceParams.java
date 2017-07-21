package com.github.kaklakariada.aws.lambda.example;

import com.github.kaklakariada.aws.lambda.service.ServiceParams;

public class MyServiceParams implements ServiceParams {
	private final String stage;
	private final String configValue;

	public MyServiceParams(String stage, String configValue) {
		this.stage = stage;
		this.configValue = configValue;
	}

	public String getStage() {
		return stage;
	}

	public String getConfigValue() {
		return configValue;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((configValue == null) ? 0 : configValue.hashCode());
		result = prime * result + ((stage == null) ? 0 : stage.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final MyServiceParams other = (MyServiceParams) obj;
		if (configValue == null) {
			if (other.configValue != null) {
				return false;
			}
		} else if (!configValue.equals(other.configValue)) {
			return false;
		}
		if (stage == null) {
			if (other.stage != null) {
				return false;
			}
		} else if (!stage.equals(other.stage)) {
			return false;
		}
		return true;
	}
}