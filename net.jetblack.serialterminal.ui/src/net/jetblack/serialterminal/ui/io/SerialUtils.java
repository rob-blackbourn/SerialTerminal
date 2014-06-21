package net.jetblack.serialterminal.ui.io;

import jssc.SerialPort;
import jssc.SerialPortList;

public class SerialUtils {


	public static final String[] BAUDRATE_NAMES = new String[] { "110", "300",
			"600", "1,200", "4,800", "9,600", "14,400", "19,200", "38,400",
			"57,600", "115,200", "128,000", "256,000" };
	public static final int[] BAUDRATE_VALUES = new int[] {
			SerialPort.BAUDRATE_110, SerialPort.BAUDRATE_300,
			SerialPort.BAUDRATE_600, SerialPort.BAUDRATE_1200,
			SerialPort.BAUDRATE_4800, SerialPort.BAUDRATE_9600,
			SerialPort.BAUDRATE_14400, SerialPort.BAUDRATE_19200,
			SerialPort.BAUDRATE_38400, SerialPort.BAUDRATE_57600,
			SerialPort.BAUDRATE_115200, SerialPort.BAUDRATE_128000,
			SerialPort.BAUDRATE_256000 };

	public static final String[] PARITY_NAMES = new String[] { "None", "Odd",
			"Even", "Mark", "Space" };
	public static final int[] PARITY_VALUES = new int[] {
			SerialPort.PARITY_NONE, SerialPort.PARITY_ODD,
			SerialPort.PARITY_EVEN, SerialPort.PARITY_MARK,
			SerialPort.PARITY_SPACE };

	public static final String[] DATABITS_NAMES = new String[] { "5", "6", "7",
			"8" };
	public static final int[] DATABITS_VALUES = new int[] {
			SerialPort.DATABITS_5, SerialPort.DATABITS_6,
			SerialPort.DATABITS_7, SerialPort.DATABITS_8 };

	public static final String[] STOPBITS_NAMES = new String[] { "1", "1 1/2",
			"2" };
	public static final int[] STOPBITS_VALUES = new int[] {
			SerialPort.STOPBITS_1, SerialPort.STOPBITS_1_5,
			SerialPort.STOPBITS_2 };

	public static final String[][] LINE_ENDING_NAMES_AND_VALUES = new String[][] {
			{ "None", "" }, { "Cr", "\r" }, { "Lf", "\n" }, { "CrLf", "\r\n" } };

	public static String getSummary(int dataBits, int parity, int stopBits) {
		StringBuilder s = new StringBuilder();
		
		switch (dataBits) {
		case SerialPort.DATABITS_5:
			s.append('5');
			break;
		case SerialPort.DATABITS_6:
			s.append('6');
			break;
		case SerialPort.DATABITS_7:
			s.append('7');
			break;
		case SerialPort.DATABITS_8:
			s.append('8');
			break;
		}
		
		switch (parity) {
		case SerialPort.PARITY_NONE:
			s.append('N');
			break;
		case SerialPort.PARITY_EVEN:
			s.append('E');
			break;
		case SerialPort.PARITY_ODD:
			s.append('O');
			break;
		case SerialPort.PARITY_MARK:
			s.append('M');
			break;
		case SerialPort.PARITY_SPACE:
			s.append('S');
			break;
		}
		
		switch (stopBits) {
		case SerialPort.STOPBITS_1:
			s.append('1');
			break;
		case SerialPort.STOPBITS_1_5:
			s.append("1.5");
			break;
		case SerialPort.STOPBITS_2:
			s.append('2');
			break;
		}
		return s.toString();
	}

	public static String getBaudRateName(int value) {
		return getNameFromValue(BAUDRATE_NAMES, BAUDRATE_VALUES, value);
	}

	public static String getParityName(int value) {
		return getNameFromValue(PARITY_NAMES, PARITY_VALUES, value);
	}

	public static String getDataBitsName(int value) {
		return getNameFromValue(DATABITS_NAMES, DATABITS_VALUES, value);
	}

	public static String getStopBitsName(int value) {
		return getNameFromValue(STOPBITS_NAMES, STOPBITS_VALUES, value);
	}

	public static String getLineEndingName(String lineEnding) {
		for (int i = 0; i < LINE_ENDING_NAMES_AND_VALUES.length; ++i) {
			if (LINE_ENDING_NAMES_AND_VALUES[i][1].equals(lineEnding)) {
				return LINE_ENDING_NAMES_AND_VALUES[i][0];
			}
		}
		return null;
	}
	
	private static String getNameFromValue(String[] names, int[] values, int value) {
		for (int i = 0; i < values.length; ++i) {
			if (values[i] == value) {
				return names[i];
			}
		}
		return null;
	}
	
	public static String getLongetPortName(String defaultName) {
		String[] serialPortNames = SerialPortList.getPortNames();
		String longestSerialPortName = serialPortNames == null || serialPortNames.length == 0 ? defaultName : serialPortNames[0];
		for (int i = 1; i < serialPortNames.length; ++i) {
			if (serialPortNames[i] != null && longestSerialPortName.length() < serialPortNames[i].length()) {
				longestSerialPortName = serialPortNames[i];
			}
		}
		return longestSerialPortName;
	}
	
	public static String[] getNames(String[][] namesAndValues) {
		return getNamesOrValues(namesAndValues, true);
	}
	
	public static String[] getValues(String[][] namesAndValues) {
		return getNamesOrValues(namesAndValues, false);
	}
	
	public static String[] getNamesOrValues(String[][] namesAndValues, boolean isNames) {
		String[] array = new String[namesAndValues.length];
		for (int i = 0; i < namesAndValues.length; ++i) {
			array[i] = namesAndValues[i][isNames ? 0 : 1];
		}
		return array;
	}
}
