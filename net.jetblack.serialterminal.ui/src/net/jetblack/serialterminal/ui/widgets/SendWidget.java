package net.jetblack.serialterminal.ui.widgets;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import net.jetblack.serialterminal.ui.io.SerialParameters;
import net.jetblack.serialterminal.ui.swt.layout.StripData;
import net.jetblack.serialterminal.ui.utils.StringUtils;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

public class SendWidget implements SelectionListener, IPropertyChangeListener {

	private final SerialParameters serialParameters;
	private final Text sendText;

	private final List<SendWidgetListener> listeners = new ArrayList<SendWidgetListener>();
	
	public SendWidget(Text sendText, IPreferenceStore preferenceStore, SerialParameters serialParameters) {
		
		this.serialParameters = serialParameters;
		this.sendText = sendText;
		sendText.addSelectionListener(this);
	}

	public void setFocus() {
		if (sendText != null) {
			sendText.setFocus();
		}
	}

	public void addLIstener(SendWidgetListener listener) {
		listeners.add(listener);
	}
	
	public void removeListener(SendWidgetListener listener) {
		listeners.remove(listener);
	}
	
	private void notifyListeners(byte[] buf) {
		for (SendWidgetListener listener : listeners) {
			listener.send(buf);
		}
	}
	
	private void notifyListeners(String message, Exception e) {
		for (SendWidgetListener listener : listeners) {
			listener.error(message, e);
		}
	}
	
	@Override
	public void widgetSelected(SelectionEvent e) {
	}

	@Override
	public void widgetDefaultSelected(SelectionEvent e) {
		if (e.getSource() == sendText) {
			onReturnPressed();
		}
	}

	@Override
	public void propertyChange(PropertyChangeEvent event) {
	}
	
	private void onReturnPressed() {
		try {
			String text = StringUtils.replaceEscapeSequencies(sendText.getText());
			notifyListeners(text.getBytes(serialParameters.getEncoding()));
		} catch (ParseException e) {
			sendText.setSelection(e.getErrorOffset(), sendText.getCharCount());
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			notifyListeners("Failed to encode message", e);
		}
	}
}
