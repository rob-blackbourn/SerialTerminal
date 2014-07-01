package net.jetblack.serialterminal.ui.widgets;

import java.util.ArrayList;
import java.util.List;

import net.jetblack.serialterminal.ui.io.SerialParameters;
import net.jetblack.serialterminal.ui.io.SerialUtils;
import net.jetblack.serialterminal.ui.swt.layout.Margin;
import net.jetblack.serialterminal.ui.swt.layout.StripData;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

public class SendWidget implements SelectionListener, IPropertyChangeListener {

	private final SerialParameters serialParameters;
	private final Button sendButton;
	private final Text sendText;
	private final Combo lineEndingCombo;

	private final List<SendWidgetListener> listeners = new ArrayList<SendWidgetListener>();
	
	public SendWidget(Composite parent, IPreferenceStore preferenceStore, SerialParameters serialParameters) {
		
		this.serialParameters = serialParameters;

		sendButton = new Button(parent, SWT.PUSH);
		sendButton.setText("Send");
		sendButton.addSelectionListener(this);

		sendText = new Text(parent, SWT.SINGLE | SWT.BORDER);
		sendText.setLayoutData(new StripData(true, false, new Margin(3, 0, 3, 0)));

		lineEndingCombo = WidgetFactory.createCombo(parent, SerialUtils.LINE_ENDING_NAMES_AND_VALUES, serialParameters.getLineEnding(), "Line ending");
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
	@Override
	public void widgetSelected(SelectionEvent e) {
		if (e.getSource() == sendButton) {
			onSendButtonClicked();
		}
	}

	@Override
	public void widgetDefaultSelected(SelectionEvent e) {
	}

	@Override
	public void propertyChange(PropertyChangeEvent event) {
	}
	
	private void onSendButtonClicked() {
		String text = sendText.getText() + serialParameters.getLineEnding();
		byte[] buf = text.getBytes();
		notifyListeners(buf);
	}
}
