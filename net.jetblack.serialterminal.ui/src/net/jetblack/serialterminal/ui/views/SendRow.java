package net.jetblack.serialterminal.ui.views;

import java.util.ArrayList;
import java.util.List;

import net.jetblack.serialterminal.ui.io.SerialParameters;
import net.jetblack.serialterminal.ui.io.SerialUtils;
import net.jetblack.serialterminal.ui.swt.layout.Margin;
import net.jetblack.serialterminal.ui.swt.layout.StripData;
import net.jetblack.serialterminal.ui.swt.layout.StripLayout;

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

public class SendRow implements SelectionListener, IPropertyChangeListener {

	private final SerialParameters serialParameters;
	private final Composite control;
	private final Button sendButton;
	private final Text sendText;

	private final List<SendListener> listeners = new ArrayList<SendListener>();
	
	public SendRow(Composite parent, IPreferenceStore preferenceStore, SerialParameters serialParameters) {
		
		this.serialParameters = serialParameters;
		
		control = new Composite(parent, SWT.NO_TRIM);
		control.setLayoutData(new StripData(true, false, new Margin(3, 3, 3, 0)));
		control.setLayout(new StripLayout(true));
		
		sendButton = new Button(control, SWT.PUSH);
		sendButton.setText("Send");
		sendButton.addSelectionListener(this);

		sendText = new Text(control, SWT.SINGLE | SWT.BORDER);
		sendText.setLayoutData(new StripData(true, false, new Margin(3, 0, 3, 0)));

		Combo lineEndingCombo = new Combo(control, SWT.READ_ONLY);
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
		
	}
	
	public Composite getControl() {
		return control;
	}

	public void addLIstener(SendListener listener) {
		listeners.add(listener);
	}
	
	public void removeListener(SendListener listener) {
		listeners.remove(listener);
	}
	
	private void notifyListeners(byte[] buf) {
		for (SendListener listener : listeners) {
			listener.send(buf);
		}
	}
	@Override
	public void widgetSelected(SelectionEvent e) {
		String text = sendText.getText() + serialParameters.getLineEnding();
		byte[] buf = text.getBytes();
		notifyListeners(buf);
	}

	@Override
	public void widgetDefaultSelected(SelectionEvent e) {
	}

	@Override
	public void propertyChange(PropertyChangeEvent event) {
	}
}
