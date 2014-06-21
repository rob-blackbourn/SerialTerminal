package net.jetblack.serialterminal.ui.io;

import java.util.ArrayList;
import java.util.List;

import jssc.SerialPort;
import jssc.SerialPortList;
import net.jetblack.serialterminal.ui.preferences.SerialTerminalPreferenceConstants;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;

public class SerialParameters implements SerialTerminalPreferenceConstants, IPropertyChangeListener {

	public static final int INVALID = -1;

	private String portName;
	private int baudRate;
	private int parity;
	private int dataBits;
	private int stopBits;
	private String lineEnding;
	
	private List<SerialParametersListener> listeners = new ArrayList<SerialParametersListener>();

	public SerialParameters(IPreferenceStore preferenceStore) {
		portName =
				preferenceStore.contains(SERIAL_PORT)
					? preferenceStore.getString(SERIAL_PORT)
					: getDefaultSerialPort();

		baudRate =
				preferenceStore.contains(BAUDRATE)
					? preferenceStore.getInt(BAUDRATE)
					: getDefaultBaudRate();

		dataBits =
				preferenceStore.contains(DATABITS)
					? preferenceStore.getInt(DATABITS)
					: getDefaultDataBits();

		stopBits =
				preferenceStore.contains(STOPBITS)
					? preferenceStore.getInt(STOPBITS)
					: getDefaultStopBits();

		parity =
				preferenceStore.contains(PARITY)
					? preferenceStore.getInt(PARITY)
					: getDefaultParity();

		lineEnding =
				preferenceStore.contains(LINE_ENDING)
					? preferenceStore.getString(LINE_ENDING)
					: getDefaultLineEnding();

		preferenceStore.addPropertyChangeListener(this);
	}

	public boolean isValid() {
		return isPortNameValid() && !(baudRate == INVALID || parity == INVALID
				|| dataBits == INVALID || stopBits == INVALID || lineEnding == null);
	}

	public boolean isPortNameValid() {
		if (portName == null || "".equals(portName)) {
			return false;
		}
		
		String[] portNames = SerialPortList.getPortNames();
		for (int i = 0; i < portNames.length; ++i) {
			if (portName.equals(portNames[i])) {
				return true;
			}
		}
		return false;
	}
	
	public String getPortName() {
		return portName;
	}

	public void setPortName(String portName) {
		this.portName = portName;
	}

	public int getBaudRate() {
		return baudRate;
	}

	public void setBaudRate(int baudRate) {
		this.baudRate = baudRate;
	}

	public int getParity() {
		return parity;
	}

	public void setParity(int parity) {
		this.parity = parity;
	}

	public int getDataBits() {
		return dataBits;
	}

	public void setDataBits(int dataBits) {
		this.dataBits = dataBits;
	}

	public int getStopBits() {
		return stopBits;
	}

	public void setStopBits(int stopBits) {
		this.stopBits = stopBits;
	}

	public String getLineEnding() {
		return lineEnding;
	}

	public void setLineEnding(String lineEnding) {
		this.lineEnding = lineEnding;
	}

	public void addListener(SerialParametersListener listener) {
		listeners.add(listener);
	}
	
	public void removeListener(SerialParametersListener listener) {
		listeners.remove(listener);
	}
	
	private void notifyListeners(Object parameter, Object oldValue, Object newValue) {
		for (SerialParametersListener listener : listeners) {
			listener.onChanged(this, parameter, oldValue, newValue);
		}
	}
	
	@Override
	public void propertyChange(PropertyChangeEvent event) {
		Object property = event.getProperty();
		
		boolean isChanged = true;
		if (SERIAL_PORT.equals(property)) {
			portName = (String)event.getNewValue();
		} else if (BAUDRATE.equals(property)) {
			baudRate = (int)event.getNewValue();
		} else if (PARITY.equals(property)) {
			parity = (int)event.getNewValue();
		} else if (DATABITS.equals(property)) {
			dataBits = (int)event.getNewValue();
		} else if (STOPBITS.equals(property)) {
			stopBits = (int)event.getNewValue();
		} else if (LINE_ENDING.equals(property)) {
			lineEnding = (String)event.getNewValue();
		} else {
			isChanged = false;
		}
		
		if (isChanged) {
			notifyListeners(property, event.getOldValue(), event.getNewValue());
		}
	}
	
	public static String getDefaultSerialPort() {
		String[] portNames = SerialPortList.getPortNames();
		return portNames.length == 0 ? "" : portNames[0];
	}
	
	public static int getDefaultBaudRate() {
		return SerialPort.BAUDRATE_9600;
	}
	
	public static int getDefaultParity() {
		return SerialPort.PARITY_NONE;
	}
	
	public static int getDefaultStopBits() {
		return SerialPort.STOPBITS_1;
	}
	
	public static int getDefaultDataBits() {
		return SerialPort.DATABITS_8;
	}
	
	public static String getDefaultLineEnding() {
		return "\r";
	}

	public String getSummary() {
		return SerialUtils.getSummary(dataBits, parity, stopBits);
	}
	
	@Override
	public String toString() {
		return
				"PortName=\"" + portName
				+ "\", BaudRate=" + SerialUtils.getBaudRateName(baudRate)
				+ ", Parity=" + SerialUtils.getParityName(parity)
				+ ", DataBits=" + SerialUtils.getDataBitsName(dataBits)
				+ ", StopBits=" + SerialUtils.getStopBitsName(stopBits)
				+ ", LineEnding=" + SerialUtils.getLineEndingName(lineEnding);
	}
}
