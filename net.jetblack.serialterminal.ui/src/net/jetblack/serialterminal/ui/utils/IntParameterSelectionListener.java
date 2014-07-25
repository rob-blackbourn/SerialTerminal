package net.jetblack.serialterminal.ui.utils;

import net.jetblack.serialterminal.ui.io.SerialParameters;

import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Widget;

public class IntParameterSelectionListener extends ParameterSelectionListener {

	private final String[] texts;
	private final int[] values;
	
	public IntParameterSelectionListener(SerialParameters serialParameters, String propertyName, String[] texts, int[] values) {
		super(serialParameters, propertyName);
		this.texts = texts;
		this.values = values;
	}
	
	@Override
	protected void handleSlectionEvent(boolean isDefault, SelectionEvent e) {
		int index = getSelectionIndex((Widget)e.getSource());
		if (index != -1) {
			int newValue = values[index];
			int oldValue = serialParameters.getInt(propertyName);
			if (newValue != oldValue) {
				serialParameters.setValue(propertyName, newValue);
				notifyListeners(isDefault, texts[index], oldValue, newValue);
			}
		}
	}
}
