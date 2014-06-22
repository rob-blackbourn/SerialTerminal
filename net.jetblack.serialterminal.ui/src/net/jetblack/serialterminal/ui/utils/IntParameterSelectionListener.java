package net.jetblack.serialterminal.ui.utils;

import java.util.ArrayList;
import java.util.List;

import net.jetblack.serialterminal.ui.io.SerialParameters;

import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Combo;

public class IntParameterSelectionListener implements SelectionListener {

	private final SerialParameters serialParameters;
	private final String name;
	private final String[] texts;
	private final int[] values;
	
	private final List<IntParameterListener> listeners = new ArrayList<IntParameterListener>();
	
	public IntParameterSelectionListener(SerialParameters serialParameters, String name, String[] texts, int[] values) {
		this.serialParameters = serialParameters;
		this.name = name;
		this.texts = texts;
		this.values = values;
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
			int newValue = values[index];
			int oldValue = serialParameters.getInt(name);
			if (newValue != oldValue) {
				serialParameters.setValue(name, newValue);
				notifyListeners(isDefault, name, texts[index], newValue);
			}
		}
	}
	
	public void addListener(IntParameterListener listener) {
		listeners.add(listener);
	}
	
	public void removeListener(IntParameterListener listener) {
		listeners.remove(listener);
	}

	private void notifyListeners(boolean isDefault, String name, String text, int newValue) {
		for (IntParameterListener listener : listeners) {
			if (isDefault) {
				listener.defaultParameterChanged(name, text, newValue);
			} else {
				listener.parameterChanged(name, text, newValue);
			}
		}
	}
}
