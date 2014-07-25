package net.jetblack.serialterminal.ui.widgets;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import jssc.SerialPortException;
import net.jetblack.serialterminal.ui.io.SerialListener;
import net.jetblack.serialterminal.ui.io.SerialParameters;
import net.jetblack.serialterminal.ui.io.SerialUtils;
import net.jetblack.serialterminal.ui.preferences.SerialTerminalPreferenceConstants;
import net.jetblack.serialterminal.ui.swt.layout.Margin;
import net.jetblack.serialterminal.ui.swt.layout.StripData;
import net.jetblack.serialterminal.ui.utils.IntParameterSelectionListener;
import net.jetblack.serialterminal.ui.utils.SWTFontUtils;
import net.jetblack.serialterminal.ui.utils.StringParameterSelectionListener;
import net.jetblack.serialterminal.ui.utils.TextParameterSelectionListener;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.custom.StyledTextContent;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

public class OutputWidget implements SerialListener, SelectionListener, SerialTerminalPreferenceConstants {

	private final StyledText textOutput;
	private final SerialParameters serialParameters;
	private final StyledTextContent defaultContent;
	private final MenuItem showTextMenuItem, wrapMenuItem;
	private final String lineDelimiter;
	private final Menu parityMenu, baudRateMenu, dataBitsMenu, stopBitsMenu, lineEndingMenu, encodingMenu;

	private boolean isAutoScrolling = true;

	public OutputWidget(StyledText textOutput, SerialParameters serialParameters) {
		
		this.textOutput = textOutput;
		this.serialParameters = serialParameters;
		
		defaultContent = textOutput.getContent();
		lineDelimiter = textOutput.getLineDelimiter();
		
		Font monospacedFont = SWTFontUtils.getMonospacedFont();
		this.textOutput.setFont(monospacedFont);
		
		Menu popupMenu = new Menu(textOutput);
		showTextMenuItem = new MenuItem(popupMenu, SWT.CHECK);
		showTextMenuItem.setText("Text");
		showTextMenuItem.setSelection(true);
		showTextMenuItem.addSelectionListener(this);
		
		wrapMenuItem = new MenuItem(popupMenu, SWT.CHECK);
		wrapMenuItem.setText("Wrap");
		wrapMenuItem.addSelectionListener(this);
		
		baudRateMenu = WidgetFactory.createRadioMenu(popupMenu, "Baud Rate", SerialUtils.BAUDRATE_NAMES, SerialUtils.getBaudRateName(serialParameters.getBaudRate()), new IntParameterSelectionListener(serialParameters, BAUDRATE, SerialUtils.BAUDRATE_NAMES, SerialUtils.BAUDRATE_VALUES));
		parityMenu = WidgetFactory.createRadioMenu(popupMenu, "Parity", SerialUtils.PARITY_NAMES, SerialUtils.getParityName(serialParameters.getParity()), new IntParameterSelectionListener(serialParameters, PARITY, SerialUtils.PARITY_NAMES, SerialUtils.PARITY_VALUES));
		dataBitsMenu = WidgetFactory.createRadioMenu(popupMenu, "Data Bits", SerialUtils.DATABITS_NAMES, SerialUtils.getDataBitsName(serialParameters.getDataBits()), new IntParameterSelectionListener(serialParameters, DATABITS, SerialUtils.DATABITS_NAMES, SerialUtils.DATABITS_VALUES));
		stopBitsMenu = WidgetFactory.createRadioMenu(popupMenu, "Stop Bits", SerialUtils.STOPBITS_NAMES, SerialUtils.getStopBitsName(serialParameters.getStopBits()), new IntParameterSelectionListener(serialParameters, STOPBITS, SerialUtils.STOPBITS_NAMES, SerialUtils.STOPBITS_VALUES));
		lineEndingMenu = WidgetFactory.createRadioMenu(popupMenu, "Line Ending", SerialUtils.getNames(SerialUtils.LINE_ENDING_NAMES_AND_VALUES), SerialUtils.getLineEndingName(serialParameters.getLineEnding()), new StringParameterSelectionListener(serialParameters, LINE_ENDING, SerialUtils.LINE_ENDING_NAMES_AND_VALUES));
		encodingMenu = WidgetFactory.createRadioMenu(popupMenu, "Encoding", SerialUtils.ENCODING_NAMES, serialParameters.getEncoding(), new TextParameterSelectionListener(serialParameters, LINE_ENDING));
		
		textOutput.setMenu(popupMenu);
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

	@Override
	public void widgetSelected(SelectionEvent e) {
		if (e.getSource() == showTextMenuItem) {
			onShowTextMenuItemClicked();
		} else if (e.getSource() == wrapMenuItem) {
			onWrapMenuItemClicked();
		}
	}

	@Override
	public void widgetDefaultSelected(SelectionEvent e) {
	}

	private void onShowHexMenuItemClicked() {
	}

	private void onShowTextMenuItemClicked() {
		textOutput.setContent(defaultContent);
	}
	
	private void onWrapMenuItemClicked() {
		
	}
}
