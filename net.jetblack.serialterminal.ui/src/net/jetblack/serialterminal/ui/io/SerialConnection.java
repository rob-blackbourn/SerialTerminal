package net.jetblack.serialterminal.ui.io;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;

public class SerialConnection implements SerialPortEventListener {

	private SerialPort port;

	private final List<SerialListener> listeners = new ArrayList<SerialListener>();
	
	private byte buffer[] = new byte[32768];
	private int bufferIndex;
	private int bufferLast;

	public SerialConnection(SerialParameters serialParameters) throws SerialException {
		try {
			port = new SerialPort(serialParameters.getPortName());
			port.openPort();
			port.setParams(serialParameters.getBaudRate(), serialParameters.getDataBits(), serialParameters.getStopBits(), serialParameters.getParity(), true, true);
			port.addEventListener(this);
		} catch (Exception e) {
			throw new SerialException("Error opening serial port: " + serialParameters.getPortName(), e);
		}

		if (port == null) {
			throw new SerialPortNotFoundException("Serial port not found + iname");
		}
	}

	public void dispose() throws IOException {
		if (port != null) {
			try {
				if (port.isOpened()) {
					port.closePort();
				}
			} catch (SerialPortException e) {
				throw new IOException(e);
			} finally {
				port = null;
			}
		}
	}

	public void addSerialListener(SerialListener listener) {
		listeners.add(listener);
	}
	
	public void removeSerialListener(SerialListener listener) {
		listeners.remove(listener);
	}
	
	private void notifyListeners(byte[] received) {
		for (SerialListener listener : listeners) {
			listener.bytesReceived(received);
		}
	}
	
	public static boolean touchPort(String portName, int irate) throws SerialException {

		SerialPort serialPort = new SerialPort(portName);

		try {
			serialPort.openPort();
			serialPort.setParams(irate, 8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
			serialPort.closePort();
			return true;
		} catch (SerialPortException e) {
			throw new SerialException("Error touching serial port " + portName, e);
		} finally {
			if (serialPort.isOpened()) {
				try {
					serialPort.closePort();
				} catch (SerialPortException e) {
					// Ignore
				}
			}
		}
	}

	public synchronized void serialEvent(SerialPortEvent serialEvent) {
		if (serialEvent.isRXCHAR()) {
			try {
				byte[] buf = port.readBytes(serialEvent.getEventValue());
				if (buf.length > 0) {
					if (bufferLast == buffer.length) {
						byte temp[] = new byte[bufferLast << 1];
						System.arraycopy(buffer, 0, temp, 0, bufferLast);
						buffer = temp;
					}

					notifyListeners(buf);
				}
			} catch (SerialPortException e) {
				errorMessage("serialEvent", e);
			}
		}
	}

	/**
	 * Returns the number of bytes that have been read from serial and are
	 * waiting to be dealt with by the user.
	 */
	public synchronized int available() {
		return (bufferLast - bufferIndex);
	}

	/**
	 * Ignore all the bytes read so far and empty the buffer.
	 */
	public synchronized void clear() {
		bufferLast = 0;
		bufferIndex = 0;
	}

	/**
	 * Returns a number between 0 and 255 for the next byte that's waiting in
	 * the buffer. Returns -1 if there was no byte (although the user should
	 * first check available() to see if things are ready to avoid this)
	 */
	public synchronized int read() {
		if (bufferIndex == bufferLast)
			return -1;

		int outgoing = buffer[bufferIndex++] & 0xff;
		if (bufferIndex == bufferLast) { // rewind
			bufferIndex = 0;
			bufferLast = 0;
		}
		return outgoing;
	}

	/**
	 * Returns the next byte in the buffer as a char. Returns -1, or 0xffff, if
	 * nothing is there.
	 */
	public synchronized char readChar() {
		if (bufferIndex == bufferLast)
			return (char) (-1);
		return (char) read();
	}

	/**
	 * Return a byte array of anything that's in the serial buffer. Not
	 * particularly memory/speed efficient, because it creates a byte array on
	 * each read, but it's easier to use than readBytes(byte b[]) (see below).
	 */
	public synchronized byte[] readBytes() {
		if (bufferIndex == bufferLast)
			return null;

		int length = bufferLast - bufferIndex;
		byte outgoing[] = new byte[length];
		System.arraycopy(buffer, bufferIndex, outgoing, 0, length);

		bufferIndex = 0; // rewind
		bufferLast = 0;
		return outgoing;
	}

	/**
	 * Grab whatever is in the serial buffer, and stuff it into a byte buffer
	 * passed in by the user. This is more memory/time efficient than
	 * readBytes() returning a byte[] array.
	 * <p/>
	 * Returns an int for how many bytes were read. If more bytes are available
	 * than can fit into the byte array, only those that will fit are read.
	 */
	public synchronized int readBytes(byte outgoing[]) {
		if (bufferIndex == bufferLast)
			return 0;

		int length = bufferLast - bufferIndex;
		if (length > outgoing.length)
			length = outgoing.length;
		System.arraycopy(buffer, bufferIndex, outgoing, 0, length);

		bufferIndex += length;
		if (bufferIndex == bufferLast) {
			bufferIndex = 0; // rewind
			bufferLast = 0;
		}
		return length;
	}

	/**
	 * Reads from the serial port into a buffer of bytes up to and including a
	 * particular character. If the character isn't in the serial buffer, then
	 * 'null' is returned.
	 */
	public synchronized byte[] readBytesUntil(int interesting) {
		if (bufferIndex == bufferLast)
			return null;
		byte what = (byte) interesting;

		int found = -1;
		for (int k = bufferIndex; k < bufferLast; k++) {
			if (buffer[k] == what) {
				found = k;
				break;
			}
		}
		if (found == -1)
			return null;

		int length = found - bufferIndex + 1;
		byte outgoing[] = new byte[length];
		System.arraycopy(buffer, bufferIndex, outgoing, 0, length);

		bufferIndex = 0; // rewind
		bufferLast = 0;
		return outgoing;
	}

	/**
	 * Reads from the serial port into a buffer of bytes until a particular
	 * character. If the character isn't in the serial buffer, then 'null' is
	 * returned.
	 * <p/>
	 * If outgoing[] is not big enough, then -1 is returned, and an error
	 * message is printed on the console. If nothing is in the buffer, zero is
	 * returned. If 'interesting' byte is not in the buffer, then 0 is returned.
	 */
	public synchronized int readBytesUntil(int interesting, byte outgoing[]) {
		if (bufferIndex == bufferLast)
			return 0;
		byte what = (byte) interesting;

		int found = -1;
		for (int k = bufferIndex; k < bufferLast; k++) {
			if (buffer[k] == what) {
				found = k;
				break;
			}
		}
		if (found == -1)
			return 0;

		int length = found - bufferIndex + 1;
		if (length > outgoing.length) {
			System.err.println("readBytesUntil() byte buffer is too small for the " + length + " bytes up to and including char " + interesting);
			return -1;
		}
		// byte outgoing[] = new byte[length];
		System.arraycopy(buffer, bufferIndex, outgoing, 0, length);

		bufferIndex += length;
		if (bufferIndex == bufferLast) {
			bufferIndex = 0; // rewind
			bufferLast = 0;
		}
		return length;
	}

	/**
	 * Return whatever has been read from the serial port so far as a String. It
	 * assumes that the incoming characters are ASCII.
	 * <p/>
	 * If you want to move Unicode data, you can first convert the String to a
	 * byte stream in the representation of your choice (i.e. UTF8 or two-byte
	 * Unicode data), and send it as a byte array.
	 */
	public synchronized String readString() {
		if (bufferIndex == bufferLast)
			return null;
		return new String(readBytes());
	}

	/**
	 * Combination of readBytesUntil and readString. See caveats in each
	 * function. Returns null if it still hasn't found what you're looking for.
	 * <p/>
	 * If you want to move Unicode data, you can first convert the String to a
	 * byte stream in the representation of your choice (i.e. UTF8 or two-byte
	 * Unicode data), and send it as a byte array.
	 */
	public synchronized String readStringUntil(int interesting) {
		byte b[] = readBytesUntil(interesting);
		if (b == null)
			return null;
		return new String(b);
	}

	/**
	 * This will handle both ints, bytes and chars transparently.
	 */
	public void write(int what) { // will also cover char
		try {
			port.writeInt(what & 0xff);
		} catch (SerialPortException e) {
			errorMessage("write", e);
		}
	}

	public void write(byte bytes[]) {
		try {
			port.writeBytes(bytes);
		} catch (SerialPortException e) {
			errorMessage("write", e);
		}
	}

	/**
	 * Write a String to the output. Note that this doesn't account for Unicode
	 * (two bytes per char), nor will it send UTF8 characters.. It assumes that
	 * you mean to send a byte buffer (most often the case for networking and
	 * serial i/o) and will only use the bottom 8 bits of each char in the
	 * string. (Meaning that internally it uses String.getBytes)
	 * <p/>
	 * If you want to move Unicode data, you can first convert the String to a
	 * byte stream in the representation of your choice (i.e. UTF8 or two-byte
	 * Unicode data), and send it as a byte array.
	 */
	public void write(String what) {
		write(what.getBytes());
	}

	public void setDTR(boolean state) {
		try {
			port.setDTR(state);
		} catch (SerialPortException e) {
			errorMessage("setDTR", e);
		}
	}

	public void setRTS(boolean state) {
		try {
			port.setRTS(state);
		} catch (SerialPortException e) {
			errorMessage("setRTS", e);
		}
	}

	static public void errorMessage(String where, Throwable e) {
		System.err.println("Error inside Serial: " + where);
		e.printStackTrace();
	}
}
