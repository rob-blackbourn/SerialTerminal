package net.jetblack.serialterminal.ui.widgets;

import java.io.UnsupportedEncodingException;

import jssc.SerialPortException;
import net.jetblack.serialterminal.ui.io.SerialListener;
import net.jetblack.serialterminal.ui.io.SerialParameters;
import net.jetblack.serialterminal.ui.preferences.SerialTerminalPreferenceConstants;
import net.jetblack.serialterminal.ui.utils.SWTFontUtils;

import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.custom.StyledTextContent;
import org.eclipse.swt.graphics.Font;

public class OutputWidget implements SerialListener, SerialTerminalPreferenceConstants, IPropertyChangeListener {

	private final StyledText textOutput;
	private final SerialParameters serialParameters;
	private final StyledTextContent defaultContent;
	private final String lineDelimiter;

	private boolean isAutoScrolling = true;

	public OutputWidget(StyledText textOutput, SerialParameters serialParameters) {
		
		this.textOutput = textOutput;
		this.serialParameters = serialParameters;
		
		defaultContent = textOutput.getContent();
		lineDelimiter = textOutput.getLineDelimiter();
		
		Font monospacedFont = SWTFontUtils.getMonospacedFont();
		this.textOutput.setFont(monospacedFont);
	}
	
	public void showError(final String message, Exception exception) {
		displayTextOnUiThread(message + ": " + exception.getMessage());
	}
	
	public void showStatus(final String text) {
		displayTextOnUiThread(text);
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
	
	private void displayOnUiThread(final byte[] received) {
		if (!(textOutput == null || textOutput.isDisposed())) {
			textOutput.getDisplay().asyncExec(new Runnable() {
				@Override
				public void run() {
					if (!(textOutput == null || textOutput.isDisposed())) {
						try {
							String text = bytesToText(received);
							displayText(text);
						} catch (UnsupportedEncodingException e) {
							e.printStackTrace();
						}
					}
				}
			});
		}
	}
	
	private void displayText(final String text) {

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
	
	private String bytesToText(byte[] received) throws UnsupportedEncodingException {
		String text = new String(received, serialParameters.getEncoding());
		String lineEnding = serialParameters.getLineEnding();
		if (!("".equals(lineEnding) || lineEnding.equals(lineDelimiter))) {
			text = text.replace(lineEnding, lineDelimiter);
		}
		return text;
	}
	
	@Override
	public void bytesReceived(byte[] received) {
		displayOnUiThread(received);
	}
	
	public void readError(SerialPortException exception) {
		showError("read: ", exception);
	}

	private void onShowTextChanged(boolean showText) {
		textOutput.setContent(defaultContent);
	}
	
	private void onWrapChanged(boolean wrap) {
		
	}

	@Override
	public void propertyChange(PropertyChangeEvent event) {
		if (SHOW_TEXT.equals(event.getProperty())) {
			onShowTextChanged((boolean)event.getNewValue());
		} else if (WRAP.equals(event.getProperty())) {
			onWrapChanged((boolean)event.getNewValue());
		}
	}
}
