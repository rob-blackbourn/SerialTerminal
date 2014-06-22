package net.jetblack.serialterminal.ui.io;

import jssc.SerialPort;
import jssc.SerialPortList;
import net.jetblack.serialterminal.ui.preferences.SerialTerminalPreferenceConstants;

import org.eclipse.jface.preference.IPreferenceStore;

public class SerialParameters implements SerialTerminalPreferenceConstants {

	public static final int INVALID = -1;

	private final IPreferenceStore preferenceStore;
	
	public SerialParameters(IPreferenceStore preferenceStore) {
		this.preferenceStore = preferenceStore;
	}

	public boolean isValid() {
		return isPortNameValid() && !(getBaudRate() == INVALID || getParity() == INVALID
				|| getDataBits() == INVALID || getStopBits() == INVALID || getLineEnding() == null);
	}

	public boolean isPortNameValid() {
		if (getPortName() == null || "".equals(getPortName())) {
			return false;
		}
		
		String[] portNames = SerialPortList.getPortNames();
		for (int i = 0; i < portNames.length; ++i) {
			if (getPortName().equals(portNames[i])) {
				return true;
			}
		}
		return false;
	}
	
	public String getPortName() {
		return
			preferenceStore.contains(SERIAL_PORT)
				? preferenceStore.getString(SERIAL_PORT)
				: getDefaultSerialPort();
	}

	public void setPortName(String portName) {
		preferenceStore.setValue(SERIAL_PORT, portName);
	}

	public int getBaudRate() {
		return
			preferenceStore.contains(BAUDRATE)
				? preferenceStore.getInt(BAUDRATE)
				: getDefaultBaudRate();
	}

	public void setBaudRate(int baudRate) {
		preferenceStore.setValue(BAUDRATE, baudRate);
	}

	public int getParity() {
		return
			preferenceStore.contains(PARITY)
				? preferenceStore.getInt(PARITY)
				: getDefaultParity();
	}

	public void setParity(int parity) {
		preferenceStore.setValue(PARITY, parity);
	}

	public int getDataBits() {
		return
			preferenceStore.contains(DATABITS)
				? preferenceStore.getInt(DATABITS)
				: getDefaultDataBits();
	}

	public void setDataBits(int dataBits) {
		preferenceStore.setValue(DATABITS, dataBits);
	}

	public int getStopBits() {
		return
			preferenceStore.contains(STOPBITS)
				? preferenceStore.getInt(STOPBITS)
				: getDefaultStopBits();
	}

	public void setStopBits(int stopBits) {
		preferenceStore.setValue(STOPBITS, stopBits);
	}

	public String getLineEnding() {
		return
			preferenceStore.contains(LINE_ENDING)
				? preferenceStore.getString(LINE_ENDING)
				: getDefaultLineEnding();
	}

	public void setLineEnding(String lineEnding) {
		preferenceStore.setValue(LINE_ENDING, lineEnding);
	}
	
	public int getInt(String propertyName) {
		return preferenceStore.getInt(propertyName);
	}
	
	public String getString(String propertyName) {
		return preferenceStore.getString(propertyName);
	}
	
	public void setValue(String name, int value) {
		preferenceStore.setValue(name, value);
	}
	
	public void setValue(String name, String value) {
		preferenceStore.setValue(name,  value);
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
		return SerialUtils.getSummary(getDataBits(), getParity(), getStopBits());
	}
	
	@Override
	public String toString() {
		return
				"PortName=\"" + getPortName()
				+ "\", BaudRate=" + SerialUtils.getBaudRateName(getBaudRate())
				+ ", Parity=" + SerialUtils.getParityName(getParity())
				+ ", DataBits=" + SerialUtils.getDataBitsName(getDataBits())
				+ ", StopBits=" + SerialUtils.getStopBitsName(getStopBits())
				+ ", LineEnding=" + SerialUtils.getLineEndingName(getLineEnding());
	}
}
