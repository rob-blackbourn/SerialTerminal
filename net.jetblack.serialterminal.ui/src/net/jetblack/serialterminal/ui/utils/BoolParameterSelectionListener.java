package net.jetblack.serialterminal.ui.utils;

import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Widget;

import net.jetblack.serialterminal.ui.io.SerialParameters;

public class BoolParameterSelectionListener extends ParameterSelectionListener {

	public BoolParameterSelectionListener(SerialParameters serialParameters,
			String name) {
		super(serialParameters, name);
	}

	@Override
	protected void handleSlectionEvent(boolean isDefault, SelectionEvent e) {
		
		boolean newValue = getSelection((Widget)e.getSource());
		boolean oldValue = serialParameters.getBoolean(propertyName);
		if (newValue != oldValue) {
			serialParameters.setValue(propertyName, newValue);
			notifyListeners(isDefault, null, oldValue, newValue);
		}
	}

}
