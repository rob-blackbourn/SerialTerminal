package net.jetblack.serialterminal.ui.io;

import jssc.SerialPortException;

public interface SerialListener {
	public void bytesReceived(byte[] buf);
	public void readError(SerialPortException e);
}
