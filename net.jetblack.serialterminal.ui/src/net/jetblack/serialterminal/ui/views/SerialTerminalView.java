package net.jetblack.serialterminal.ui.views;

import java.io.IOException;

import jssc.SerialPortList;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.part.*;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.SWT;

import net.jetblack.serialterminal.ui.Activator;
import net.jetblack.serialterminal.ui.io.SerialConnection;
import net.jetblack.serialterminal.ui.io.SerialException;
import net.jetblack.serialterminal.ui.io.SerialListener;
import net.jetblack.serialterminal.ui.io.SerialParameters;
import net.jetblack.serialterminal.ui.io.SerialUtils;
import net.jetblack.serialterminal.ui.preferences.SerialTerminalPreferenceConstants;
import net.jetblack.serialterminal.ui.swt.layout.Margin;
import net.jetblack.serialterminal.ui.swt.layout.StripData;
import net.jetblack.serialterminal.ui.swt.layout.StripLayout;
import net.jetblack.serialterminal.ui.utils.IntParameterListener;
import net.jetblack.serialterminal.ui.utils.IntParameterSelectionListener;
import net.jetblack.serialterminal.ui.utils.StringParameterListener;

public class SerialTerminalView
	extends ViewPart
	implements SerialTerminalPreferenceConstants,
		SerialListener,
		IntParameterListener,
		StringParameterListener,
		IPropertyChangeListener,
		SelectionListener {

	public static final String ID = "net.jetblack.serialterminal.ui.views.SerialTerminalView";

	private final SerialParameters serialParameters;

	private Text sendText;
	private Button sendButton, refreshSerialPortsButton, reconnectButton;
	private Combo serialPortCombo, baudRateCombo, stopBitsCombo, dataBitsCombo, parityCombo;
	private Composite statusRow;
	private StyledText textOutput;
	private boolean isAutoScrolling = true;
	
	private SerialConnection serialConnection;

	public SerialTerminalView() {
		IPreferenceStore preferenceStore = Activator.getDefault().getPreferenceStore();
		serialParameters = new SerialParameters(preferenceStore);
		preferenceStore.addPropertyChangeListener(this);
	}

	public void createPartControl(Composite parent) {
		createView(parent);
		openConnection();
	}

	private void createView(Composite parent) {
		parent.setLayout(new StripLayout(false));

		// 1st Row
		Composite sendRow = new Composite(parent, SWT.NO_TRIM);
		sendRow.setLayoutData(new StripData(true, false, new Margin(3, 3, 3, 0)));
		sendRow.setLayout(new StripLayout(true));
		
		sendButton = new Button(sendRow, SWT.PUSH);
		sendButton.setText("Send");
		sendButton.addSelectionListener(this);

		sendText = new Text(sendRow, SWT.SINGLE | SWT.BORDER);
		sendText.setLayoutData(new StripData(true, false, new Margin(3, 0, 3, 0)));

		Combo lineEndingCombo = new Combo(sendRow, SWT.READ_ONLY);
		int selectedLineEnding = -1;
		for (int i = 0; i < SerialUtils.LINE_ENDING_NAMES_AND_VALUES.length; ++i) {
			lineEndingCombo.add(SerialUtils.LINE_ENDING_NAMES_AND_VALUES[i][0]);
			if (SerialUtils.LINE_ENDING_NAMES_AND_VALUES[i][1].equals(serialParameters.getLineEnding())) {
				selectedLineEnding = i;
			}
		}
		if (selectedLineEnding != -1) {
			lineEndingCombo.select(selectedLineEnding);
		}

		// 2nd row
		textOutput = new StyledText(parent, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		textOutput.setLayoutData(new StripData(true, true, new Margin(3)));
		textOutput.setEditable(false);
		
		// 3rd row
		statusRow = new Composite(parent, SWT.NO_TRIM);
		statusRow.setLayoutData(new StripData(true, false, new Margin(3, 0, 3, 3)));
		statusRow.setLayout(new StripLayout(true));
		
		reconnectButton = new Button(statusRow, SWT.DEFAULT);
		reconnectButton.setText("Reconnect");
		reconnectButton.setLayoutData(new StripData(false, false, new Margin(0, 0, 3, 0)));
		reconnectButton.addSelectionListener(this);
		
		serialPortCombo = new Combo(statusRow, SWT.READ_ONLY);
		serialPortCombo.setLayoutData(new StripData(true, false, new Margin(0, 0, 3, 0)));
		serialPortCombo.setItems(SerialPortList.getPortNames());
		if (serialPortCombo.getItemCount() > 0) {
			int serialPortIndex = serialPortCombo.indexOf(serialParameters.getPortName());
			if (serialPortIndex != -1) {
				serialPortCombo.select(serialPortIndex);
			}
		}
		serialPortCombo.addSelectionListener(this);
		
		refreshSerialPortsButton = new Button(statusRow, SWT.DEFAULT);
		refreshSerialPortsButton.setLayoutData(new StripData(false, false, new Margin(0, 0, 3, 0)));
		refreshSerialPortsButton.setText("Refresh");
		refreshSerialPortsButton.addSelectionListener(this);
		
		baudRateCombo = new Combo(statusRow, SWT.READ_ONLY | SWT.RIGHT);
		baudRateCombo.setLayoutData(new StripData(false, false, new Margin(0, 0, 3, 0)));
		baudRateCombo.setItems(SerialUtils.BAUDRATE_NAMES);
		baudRateCombo.select(baudRateCombo.indexOf(SerialUtils.getBaudRateName(serialParameters.getBaudRate())));
		baudRateCombo.addSelectionListener(new IntParameterSelectionListener(serialParameters, BAUDRATE, SerialUtils.BAUDRATE_NAMES, SerialUtils.BAUDRATE_VALUES));
		
		dataBitsCombo = new Combo(statusRow, SWT.READ_ONLY | SWT.RIGHT);
		dataBitsCombo.setLayoutData(new StripData(false, false, new Margin(0, 0, 3, 0)));
		dataBitsCombo.setItems(SerialUtils.DATABITS_NAMES);
		dataBitsCombo.select(dataBitsCombo.indexOf(SerialUtils.getDataBitsName(serialParameters.getDataBits())));
		dataBitsCombo.addSelectionListener(new IntParameterSelectionListener(serialParameters, DATABITS, SerialUtils.DATABITS_NAMES, SerialUtils.DATABITS_VALUES));
		
		parityCombo = new Combo(statusRow, SWT.READ_ONLY | SWT.RIGHT);
		parityCombo.setLayoutData(new StripData(false, false, new Margin(0, 0, 3, 0)));
		parityCombo.setItems(SerialUtils.PARITY_NAMES);
		parityCombo.select(parityCombo.indexOf(SerialUtils.getParityName(serialParameters.getParity())));
		parityCombo.addSelectionListener(new IntParameterSelectionListener(serialParameters, PARITY, SerialUtils.PARITY_NAMES, SerialUtils.PARITY_VALUES));
		
		stopBitsCombo = new Combo(statusRow, SWT.READ_ONLY | SWT.RIGHT);
		stopBitsCombo.setLayoutData(new StripData(false, false, new Margin(0, 0, 3, 0)));
		stopBitsCombo.setItems(SerialUtils.STOPBITS_NAMES);
		stopBitsCombo.select(stopBitsCombo.indexOf(SerialUtils.getStopBitsName(serialParameters.getStopBits())));
		stopBitsCombo.addSelectionListener(new IntParameterSelectionListener(serialParameters, STOPBITS, SerialUtils.STOPBITS_NAMES, SerialUtils.STOPBITS_VALUES));
		
		//closeConnection();
		//openConnection();
	}
	
	@Override
	public void widgetSelected(SelectionEvent e) {
		if (serialPortCombo == e.getSource()) {
			onSerialPortChanged();
		} else if (refreshSerialPortsButton == e.getSource()) {
			onRefreshSerialPorts();
		} else if (sendButton == e.getSource()) {
			onSendSelected();
		} else if (reconnectButton == e.getSource()) {
			onReconnectClicked();
		}
	}
	
	@Override
	public void widgetDefaultSelected(SelectionEvent e) {
	}

	private void onSerialPortChanged() {
		int index = serialPortCombo.getSelectionIndex();
		if (index != -1) {
			String portName = serialPortCombo.getText();
			serialParameters.setPortName(portName);
		}
	}
		
	private void onRefreshSerialPorts() {
		String[] serialPorts = SerialPortList.getPortNames();
		if (serialPorts.length > 0) {
			serialPortCombo.setItems(serialPorts);
			statusRow.layout(true, true);
		}
	}
	
	private void onSendSelected() {
		String text = sendText.getText() + serialParameters.getLineEnding();
		byte[] buf = text.getBytes();
		serialConnection.write(buf);
	}
	
	private void onReconnectClicked() {
		closeConnection();
		openConnection();
	}
	
	public void setFocus() {
		if (sendText != null) {
			sendText.setFocus();
		}
	}

	private void openConnection() {
		if (!serialParameters.isValid()) {
			return;
		}

		displayTextOnUiThread("Connecting: " + serialParameters + "\n");
		
		try {
			serialConnection = new SerialConnection(serialParameters);
			serialConnection.addSerialListener(this);
		} catch (SerialException e) {
			displayTextOnUiThread("Failed to open connection: " + e.getMessage() + "\n");
		}
	}

	private void closeConnection() {
		if (serialConnection == null) {
			return;
		}
		
		displayTextOnUiThread("Disconnecting\n");

		try {
			serialConnection.removeSerialListener(this);
			serialConnection.dispose();
			serialConnection = null;
		} catch (IOException e) {
			displayTextOnUiThread("Failed to close connection: " + e.getMessage() + "\n");
		}
	}

	@Override
	public void bytesReceived(byte[] received) {
		displayTextOnUiThread(new String(received));
	}
	
	private void displayTextOnUiThread(final String text) {
		if (!(textOutput == null || textOutput.isDisposed())) {
			textOutput.getDisplay().asyncExec(new Runnable() {
				@Override
				public void run() {
					if (!(textOutput == null || textOutput.isDisposed())) {
						displayText(text);
					}
				}
			});
		}
	}
	
	private void displayText(String text) {

		int StartPoint = textOutput.getCharCount();
		textOutput.append(text);
		StyleRange styleRange = new StyleRange();
		styleRange.start = StartPoint;
		styleRange.length = text.length();
		styleRange.fontStyle = SWT.NORMAL;
		textOutput.setStyleRange(styleRange);
		if (isAutoScrolling) {
		    textOutput.setSelection(textOutput.getCharCount());
		}
	}

	@Override
	public void parameterChanged(String name, String text, int value) {
		
		boolean isChanged = true;
		if (BAUDRATE.equals(name)) {
			baudRateCombo.setText(text);
		} else if (PARITY.equals(name)) {
			parityCombo.setText(text);
		} else if (STOPBITS.equals(name)) {
			stopBitsCombo.setText(text);
		} else if (DATABITS.equals(name)) {
			dataBitsCombo.setText(text);
		} else {
			isChanged = false;
		}
		
		if (isChanged) {
			closeConnection();
			openConnection();
		}
	}

	@Override
	public void defaultParameterChanged(String name, String text, int value) {
	}

	@Override
	public void parameterChanged(String name, String text) {
		
		boolean isChanged = true;
		
		if (SERIAL_PORT.equals(name)) {
			serialPortCombo.setText(text);
		} else {
			isChanged = false;
		}
		
		if (isChanged) {
			closeConnection();
			openConnection();
		}
	}

	@Override
	public void defaultParameterChanged(String name, String text) {
	}

	@Override
	public void propertyChange(PropertyChangeEvent event) {
		
		if (SERIAL_PORT.equals(event.getProperty())) {
			parameterChanged(SERIAL_PORT, (String)event.getNewValue());
		} else if (BAUDRATE.equals(event.getProperty())) {
			parameterChanged(BAUDRATE, SerialUtils.getBaudRateName((int)event.getNewValue()), (int)event.getNewValue());
		} else if (PARITY.equals(event.getProperty())) {
			parameterChanged(PARITY, SerialUtils.getParityName((int)event.getNewValue()), (int)event.getNewValue());
		} else if (DATABITS.equals(event.getProperty())) {
			parameterChanged(DATABITS, SerialUtils.getDataBitsName((int)event.getNewValue()), (int)event.getNewValue());
		} else if (STOPBITS.equals(event.getProperty())) {
			parameterChanged(STOPBITS, SerialUtils.getStopBitsName((int)event.getNewValue()), (int)event.getNewValue());
		}
	}
}