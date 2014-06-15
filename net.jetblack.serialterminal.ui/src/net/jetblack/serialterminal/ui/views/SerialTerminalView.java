package net.jetblack.serialterminal.ui.views;

import java.io.IOException;

import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.part.*;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.SWT;

import net.jetblack.serialterminal.ui.Activator;
import net.jetblack.serialterminal.ui.io.SerialConnection;
import net.jetblack.serialterminal.ui.io.SerialException;
import net.jetblack.serialterminal.ui.io.SerialListener;
import net.jetblack.serialterminal.ui.io.SerialParameters;
import net.jetblack.serialterminal.ui.io.SerialParametersListener;
import net.jetblack.serialterminal.ui.preferences.SerialTerminalPreferenceConstants;

public class SerialTerminalView extends ViewPart implements SerialTerminalPreferenceConstants, SerialListener, SerialParametersListener {

	public static final String ID = "net.jetblack.serialterminal.ui.views.SerialTerminalView";

	private final SerialParameters serialParameters;

	private Text sendText;
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
		GridLayout layout = new GridLayout(3, false);
		parent.setLayout(layout);

		// 1st Row
		Button sendButton = new Button(parent, SWT.PUSH);
		sendButton.setText("Send");
		sendButton.setLayoutData(new GridData(GridData.CENTER));
		sendButton.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				onSendSelected();
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});

		sendText = new Text(parent, SWT.SINGLE | SWT.BORDER);
		sendText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		Combo lineEndingCombo = new Combo(parent, SWT.READ_ONLY);
		int selectedLineEnding = -1;
		for (int i = 0; i < LINE_ENDING_NAMES_AND_VALUES.length; ++i) {
			lineEndingCombo.add(LINE_ENDING_NAMES_AND_VALUES[i][0]);
			if (LINE_ENDING_NAMES_AND_VALUES[i][1].equals(serialParameters.getLineEnding())) {
				selectedLineEnding = i;
			}
		}
		if (selectedLineEnding != -1) {
			lineEndingCombo.select(selectedLineEnding);
		}

		// 2nd row
		//textOutput = new StyledText(parent, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		textOutput = new StyledText(parent, SWT.H_SCROLL | SWT.V_SCROLL);
		textOutput.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 3, 1));
		textOutput.setEditable(false);
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
		closeConnection();
		openConnection();
	}

}