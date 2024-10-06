package com.tlregen.api.registration;

public class RegistrationException extends Exception {
	@java.io.Serial
	private static final long serialVersionUID = -4222015375092547202L;

	public RegistrationException() {
		super();
	}

	public RegistrationException(String message) {
		super(message);
	}

	public RegistrationException(Throwable cause) {
		super(cause);
	}

	public RegistrationException(String message, Throwable cause) {
		super(message, cause);
	}
}
