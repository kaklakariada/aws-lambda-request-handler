package handler.service;

public class StageBasedEchoService {
	private final String stage;
	private final String codeUri;

	public StageBasedEchoService(String stage, String codeUri) {
		this.stage = stage;
		this.codeUri = codeUri;
	}

	public String echo(String message) {
		return "Stage " + stage + ": " + message;
	}

	public String getCodeUri() {
		return codeUri;
	}
}
