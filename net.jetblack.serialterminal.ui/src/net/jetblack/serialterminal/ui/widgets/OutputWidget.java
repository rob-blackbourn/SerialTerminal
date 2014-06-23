package net.jetblack.serialterminal.ui.widgets;

import jssc.SerialPortException;
import net.jetblack.serialterminal.ui.io.SerialListener;
import net.jetblack.serialterminal.ui.utils.SWTFontUtils;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.custom.StyledTextContent;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

public class OutputWidget implements SerialListener, SelectionListener {

	private final StyledText textOutput;
	private final StyledTextContent defaultContent;
	private final MenuItem showTextMenuItem, showHexMenuItem;
	private boolean showText = true;
	private int blockSize = 8;

	private boolean isAutoScrolling = true;

	public OutputWidget(StyledText textOutput) {

		this.textOutput = textOutput;
		defaultContent = textOutput.getContent();
		
		Font monospacedFont = SWTFontUtils.getMonospacedFont();
		this.textOutput.setFont(monospacedFont);
		Menu popupMenu = new Menu(textOutput);
		showTextMenuItem = new MenuItem(popupMenu, SWT.NONE);
		showTextMenuItem.setText("Text");
		showTextMenuItem.addSelectionListener(this);
		
		showHexMenuItem = new MenuItem(popupMenu, SWT.NONE);
		showHexMenuItem.setText("Hex");
		
		textOutput.setMenu(popupMenu);
	}
	
	public void showError(final String message, SerialPortException exception) {
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

	@Override
	public void bytesReceived(byte[] received) {
		displayTextOnUiThread(new String(received));
	}
	
	public void readError(SerialPortException exception) {
		showError("read: ", exception);
	}

	@Override
	public void widgetSelected(SelectionEvent e) {
		if (e.getSource() == showTextMenuItem) {
			onShowTextMenuItemClicked();
		} else if (e.getSource() == showHexMenuItem) {
			onShowHexMenuItemClicked();
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
}
