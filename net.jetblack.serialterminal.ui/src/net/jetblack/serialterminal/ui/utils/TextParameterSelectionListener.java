package net.jetblack.serialterminal.ui.utils;

import net.jetblack.serialterminal.ui.io.SerialParameters;

import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Widget;

public class TextParameterSelectionListener extends ParameterSelectionListener {
	
	public TextParameterSelectionListener(SerialParameters serialParameters, String propertyName) {
		super(serialParameters, propertyName);
	}
	
	@Override
	protected void handleSlectionEvent(boolean isDefault, SelectionEvent e) {
		String newValue = getText((Widget)e.getSource());
		if (newValue != null) {
			String oldValue = serialParameters.getString(propertyName);
			if (newValue != oldValue) {
				serialParameters.setValue(propertyName, newValue);
				notifyListeners(isDefault, newValue, oldValue, newValue);
			}
		}
	}
}
