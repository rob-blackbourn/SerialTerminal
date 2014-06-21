package net.jetblack.serialterminal.ui.views;

import java.io.IOException;
import jssc.SerialPort;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.part.*;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.SWT;

import net.jetblack.serialterminal.ui.Activator;
import net.jetblack.serialterminal.ui.io.SerialConnection;
import net.jetblack.serialterminal.ui.io.SerialException;
import net.jetblack.serialterminal.ui.io.SerialListener;
import net.jetblack.serialterminal.ui.io.SerialParameters;
import net.jetblack.serialterminal.ui.io.SerialParametersListener;
import net.jetblack.serialterminal.ui.io.SerialUtils;
import net.jetblack.serialterminal.ui.preferences.SerialTerminalPreferenceConstants;
import net.jetblack.serialterminal.ui.swt.layout.Margin;
import net.jetblack.serialterminal.ui.swt.layout.Size;
import net.jetblack.serialterminal.ui.swt.layout.StripData;
import net.jetblack.serialterminal.ui.swt.layout.StripLayout;

public class SerialTerminalView extends ViewPart implements SerialTerminalPreferenceConstants, SerialListener, SerialParametersListener {

	public static final String ID = "net.jetblack.serialterminal.ui.views.SerialTerminalView";

	private final SerialParameters serialParameters;

	private Text sendText, statusText, serialPortText, baudRateText, parametersText;
	private StyledText textOutput;
	private boolean isAutoScrolling = true;
	
	private SerialConnection serialConnection;

	public SerialTerminalView() {
		serialParameters = new SerialParameters(Activator.getDefault().getPreferenceStore());
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
		
		Button sendButton = new Button(sendRow, SWT.PUSH);
		sendButton.setText("Send");
		sendButton.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				onSendSelected();
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});

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
		Composite statusRow = new Composite(parent, SWT.NO_TRIM);
		statusRow.setLayoutData(new StripData(true, false, new Margin(3, 0, 3, 3)));
		statusRow.setLayout(new StripLayout(true));
		
		statusText = new Text(statusRow, SWT.READ_ONLY | SWT.BORDER);
		statusText.setLayoutData(new StripData(true, false, new Margin(0, 0, 3, 0)));
		
		serialPortText = new Text(statusRow, SWT.READ_ONLY | SWT.BORDER);
		String longestSerialPortName = SerialUtils.getLongetPortName("/dev/tty1");
		int maxSerialPortWidth = measureText(serialPortText, longestSerialPortName);
		serialPortText.setLayoutData(new StripData(false, false, new Size(maxSerialPortWidth, SWT.DEFAULT), new Margin(0, 0, 3, 0)));
		
		baudRateText = new Text(statusRow, SWT.READ_ONLY | SWT.BORDER | SWT.RIGHT);
		String longestBaudRateName = SerialUtils.getBaudRateName(SerialPort.BAUDRATE_256000);
		int maxBaudRateWidth = measureText(baudRateText, longestBaudRateName);
		baudRateText.setLayoutData(new StripData(false, false, new Size(maxBaudRateWidth, SWT.DEFAULT), new Margin(0, 0, 3, 0)));
		baudRateText.setText(SerialUtils.getBaudRateName(serialParameters.getBaudRate()));
		
		parametersText = new Text(statusRow, SWT.READ_ONLY | SWT.BORDER | SWT.CENTER);
		String longestParameterName = "8N1/2";
		int maxParameterWidth = measureText(parametersText, longestParameterName);
		parametersText.setLayoutData(new StripData(false, false, new Size(maxParameterWidth, SWT.DEFAULT)));
		
		serialParameters.addListener(this);
	}

	private void onSendSelected() {
		String text = sendText.getText() + serialParameters.getLineEnding();
		byte[] buf = text.getBytes();
		serialConnection.write(buf);
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
			displayTextOnUiThread("Failed to open connection" + e.getMessage());
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
			displayTextOnUiThread("Failed to close connection" + e.getMessage());
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
	public void onChanged(SerialParameters serialParameters, Object parameter, Object oldValue, Object newValue) {
		if (SerialParameters.BAUDRATE.equals(parameter)) {
			baudRateText.setText(SerialUtils.getBaudRateName(serialParameters.getBaudRate()));
		}
		
		closeConnection();
		openConnection();
	}

	private int measureText(Control control, String text) {
		
		if (text == null) return SWT.DEFAULT;
		
		GC gc = new GC(control);
		int width = gc.getCharWidth(' ');
		for (int i = 0; i < text.length(); ++i) {
			width += gc.getCharWidth(text.charAt(i));
		}
		gc.dispose();
		
		return width;
	}
}