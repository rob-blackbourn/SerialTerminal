package net.jetblack.serialterminal.ui.views;

import java.io.IOException;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.*;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.SWT;

import net.jetblack.serialterminal.ui.Activator;
import net.jetblack.serialterminal.ui.io.SerialConnection;
import net.jetblack.serialterminal.ui.io.SerialException;
import net.jetblack.serialterminal.ui.io.SerialParameters;
import net.jetblack.serialterminal.ui.preferences.SerialTerminalPreferenceConstants;
import net.jetblack.serialterminal.ui.swt.layout.Margin;
import net.jetblack.serialterminal.ui.swt.layout.StripData;
import net.jetblack.serialterminal.ui.swt.layout.StripLayout;
import net.jetblack.serialterminal.ui.widgets.OutputWidget;
import net.jetblack.serialterminal.ui.widgets.PreferenceWidget;
import net.jetblack.serialterminal.ui.widgets.PreferenceWidgetListener;
import net.jetblack.serialterminal.ui.widgets.SendWidget;
import net.jetblack.serialterminal.ui.widgets.SendWidgetListener;

public class SerialTerminalView
	extends ViewPart
	implements SerialTerminalPreferenceConstants,
		PreferenceWidgetListener,
		SendWidgetListener {

	public static final String ID = "net.jetblack.serialterminal.ui.views.SerialTerminalView";

	private final SerialParameters serialParameters;

	private Composite sendRow, preferenceRow;
	private OutputWidget outputWidget;
	
	private SerialConnection serialConnection;

	public SerialTerminalView() {
		IPreferenceStore preferenceStore = Activator.getDefault().getPreferenceStore();
		serialParameters = new SerialParameters(preferenceStore);
	}

	public void createPartControl(Composite parent) {
		createView(parent);
		openConnection();
	}

	private void createView(Composite parent) {

		IPreferenceStore preferenceStore = Activator.getDefault().getPreferenceStore();

		parent.setLayout(new StripLayout(false));

		// 1st Row
		sendRow = new Composite(parent, SWT.NO_TRIM);
		sendRow.setLayoutData(new StripData(true, false, new Margin(3, 3, 3, 0)));
		sendRow.setLayout(new StripLayout(true));
		SendWidget sendWidget = new SendWidget(sendRow, preferenceStore, serialParameters);
		sendWidget.addLIstener(this);
		
		// 2nd row
		StyledText textOutput = new StyledText(parent, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		textOutput.setLayoutData(new StripData(true, true, new Margin(3)));
		textOutput.setEditable(false);
		outputWidget = new OutputWidget(textOutput);
		
		// 3rd row
		preferenceRow = new Composite(parent, SWT.NO_TRIM);
		preferenceRow.setLayoutData(new StripData(true, false, new Margin(3, 0, 3, 3)));
		preferenceRow.setLayout(new StripLayout(true));
		PreferenceWidget preferenceWidget = new PreferenceWidget(preferenceRow, preferenceStore, serialParameters);
		preferenceWidget.addListener(this);
	}
	
	public void setFocus() {
		if (sendRow != null) {
			sendRow.setFocus();
		}
	}

	private void openConnection() {
		if (!serialParameters.isValid()) {
			return;
		}

		outputWidget.append("Connecting: " + serialParameters + "\n");
		
		try {
			serialConnection = new SerialConnection(serialParameters.getPortName(), serialParameters.getBaudRate(), serialParameters.getDataBits(), serialParameters.getStopBits(), serialParameters.getParity());
			serialConnection.addSerialListener(outputWidget);
		} catch (SerialException e) {
			outputWidget.append("Failed to open connection: " + e.getMessage() + "\n");
		}
	}

	private void closeConnection() {
		if (serialConnection == null) {
			return;
		}
		
		outputWidget.append("Disconnecting\n");

		try {
			serialConnection.removeSerialListener(outputWidget);
			serialConnection.dispose();
			serialConnection = null;
		} catch (IOException e) {
			outputWidget.append("Failed to close connection: " + e.getMessage() + "\n");
		}
	}

	@Override
	public void onReconnect() {
		closeConnection();
		openConnection();
	}

	@Override
	public void send(byte[] buf) {
		serialConnection.write(buf);
	}
}