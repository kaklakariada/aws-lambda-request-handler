package handler.service;

import com.github.kaklakariada.aws.lambda.service.ServiceParams;

public class EchoServiceParam implements ServiceParams {
	private final String stage;
	private final String codeUri;

	public EchoServiceParam(String stage, String codeUri) {
		this.stage = stage;
		this.codeUri = codeUri;
	}

	public String getStage() {
		return stage;
	}

	public String getCodeUri() {
		return codeUri;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((codeUri == null) ? 0 : codeUri.hashCode());
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
		final EchoServiceParam other = (EchoServiceParam) obj;
		if (codeUri == null) {
			if (other.codeUri != null) {
				return false;
			}
		} else if (!codeUri.equals(other.codeUri)) {
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