package net.jetblack.serialterminal.ui.widgets;

public interface SendWidgetListener {
	public void send(byte[] buf);
	public void error(String message, Exception exception);
}
