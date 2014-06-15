package net.jetblack.serialterminal.ui.io;

public class SerialPortNotFoundException extends SerialException {

	private static final long serialVersionUID = -6301473449004307298L;

	public SerialPortNotFoundException() {
		super();
	}

	public SerialPortNotFoundException(String message) {
		super(message);
	}

	public SerialPortNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}

	public SerialPortNotFoundException(Throwable cause) {
		super(cause);
	}
}
