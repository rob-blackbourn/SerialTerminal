package net.jetblack.serialterminal.ui.io;

import java.util.ArrayList;
import java.util.List;

import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;

public class SerialConnection implements SerialPortEventListener {

	private SerialPort port;

	private final List<SerialListener> listeners = new ArrayList<SerialListener>();
	
	public SerialConnection(String portName, int baudRate, int dataBits, int stopBits, int parity) throws SerialPortException {
		port = new SerialPort(portName);
		port.openPort();
		port.setParams(baudRate, dataBits, stopBits, parity, true, true);
		port.addEventListener(this);
	}

	public void close() throws SerialPortException {
		if (port != null) {
			try {
				if (port.isOpened()) {
					port.closePort();
				}
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
	
	private void notifyBytesReceived(byte[] received) throws SerialPortException {
		for (SerialListener listener : listeners) {
			listener.bytesReceived(received);
		}
	}
	
	private void notifyReadError(SerialPortException exception) {
		for (SerialListener listener : listeners) {
			listener.readError(exception);
		}
	}
	
	public synchronized void serialEvent(SerialPortEvent serialEvent) {
		if (serialEvent.isRXCHAR()) {
			try {
				byte[] buf = port.readBytes(serialEvent.getEventValue());
				if (buf.length > 0) {
					notifyBytesReceived(buf);
				}
			} catch (SerialPortException e) {
				notifyReadError(e);
			}
		}
	}

	public void write(byte bytes[]) throws SerialPortException {
		port.writeBytes(bytes);
	}

	public void setDTR(boolean state) throws SerialPortException {
		port.setDTR(state);
	}

	public void setRTS(boolean state) throws SerialPortException {
		port.setRTS(state);
	}
}
