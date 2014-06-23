package net.jetblack.serialterminal.ui.utils;

import java.util.ArrayList;
import java.util.List;

import net.jetblack.serialterminal.ui.io.SerialParameters;

import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Combo;

public class StringParameterSelectionListener implements SelectionListener {
	
	private final SerialParameters serialParameters;
	private final String name;
	
	private final List<ParameterListener> listeners = new ArrayList<ParameterListener>();
	
	public StringParameterSelectionListener(SerialParameters serialParameters, String name) {
		this.serialParameters = serialParameters;
		this.name = name;
	}
	
	@Override
	public void widgetSelected(SelectionEvent e) {
		handleSlectionEvent(false, e);
	}

	@Override
	public void widgetDefaultSelected(SelectionEvent e) {
		handleSlectionEvent(true, e);
	}
	
	private void handleSlectionEvent(boolean isDefault, SelectionEvent e) {
		Combo combo = (Combo)e.getSource();
		int index = combo.getSelectionIndex();
		if (index != -1) {
			String oldValue = serialParameters.getString(name);
			String newValue = combo.getText();
			if ((newValue == null && oldValue == null) || (newValue != null && !newValue.equals(oldValue))) {
				serialParameters.setValue(name, newValue);
				notifyListeners(isDefault, name, newValue);
			}
		}
	}
	
	public void addListener(ParameterListener listener) {
		listeners.add(listener);
	}
	
	public void removeListener(ParameterListener listener) {
		listeners.remove(listener);
	}

	private void notifyListeners(boolean isDefault, String name, String text) {
		for (ParameterListener listener : listeners) {
			if (isDefault) {
				listener.defaultParameterChanged(name, text);
			} else {
				listener.parameterChanged(name, text);
			}
		}
	}
}
