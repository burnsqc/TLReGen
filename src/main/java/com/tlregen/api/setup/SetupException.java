package com.tlregen.api.setup;

class SetupException extends Exception {
	@java.io.Serial
	private static final long serialVersionUID = -4984685859977485579L;

	public SetupException() {
		super();
	}

	public SetupException(String message) {
		super(message);
	}

	public SetupException(Throwable cause) {
		super(cause);
	}

	public SetupException(String message, Throwable cause) {
		super(message, cause);
	}
}
