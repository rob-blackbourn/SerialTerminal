package net.jetblack.serialterminal.ui.io;

import java.io.IOException;

public class SerialException extends IOException {

	private static final long serialVersionUID = 6053896529234192795L;

	public SerialException() {
		super();
	}

	public SerialException(String message) {
		super(message);
	}

	public SerialException(String message, Throwable cause) {
		super(message, cause);
	}

	public SerialException(Throwable cause) {
		super(cause);
	}
}
