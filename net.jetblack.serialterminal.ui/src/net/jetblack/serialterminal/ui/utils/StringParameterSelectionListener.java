package net.jetblack.serialterminal.ui.utils;

import net.jetblack.serialterminal.ui.io.SerialParameters;
import net.jetblack.serialterminal.ui.io.SerialUtils;

import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Widget;

public class StringParameterSelectionListener extends ParameterSelectionListener {
	
	private final String[] names;
	private final String[] values;
	
	public StringParameterSelectionListener(SerialParameters serialParameters, String propertyName, String[][] namesAndValues) {
		this(serialParameters, propertyName, SerialUtils.getNames(namesAndValues), SerialUtils.getValues(namesAndValues));
	}
	
	public StringParameterSelectionListener(SerialParameters serialParameters, String propertyName, String[] names, String[] values) {
		super(serialParameters, propertyName);
		this.names = names;
		this.values = values;
	}
	
	@Override
	protected void handleSlectionEvent(boolean isDefault, SelectionEvent e) {
		int index = getSelectionIndex((Widget)e.getSource());
		if (index != -1) {
			String newValue = values[index];
			String oldValue = serialParameters.getString(propertyName);
			if (newValue != oldValue) {
				serialParameters.setValue(propertyName, newValue);
				notifyListeners(isDefault, names[index], oldValue, newValue);
			}
		}
	}
}
