package net.jetblack.serialterminal.ui.preferences;

import jssc.SerialPort;

public interface SerialTerminalPreferenceConstants {

	public static final String SERIAL_PORT = "serialPortPreference";
	public static final String BAUDRATE = "baudRatePreference";
	public static final String PARITY = "parityPreference";
	public static final String STOPBITS = "stopBitsPreference";
	public static final String DATABITS = "dataBitsPreference";
	public static final String LINE_ENDING = "lineEndingPreference";

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
	

}
