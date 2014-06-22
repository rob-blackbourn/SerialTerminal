package net.jetblack.serialterminal.ui.utils;

import org.eclipse.jface.viewers.LabelProvider;

public class NamedIntValueLabelProvider extends LabelProvider {

	private static NamedIntValueLabelProvider instance;
	
	public static NamedIntValueLabelProvider getInstance() {
		if (instance == null) {
			instance = new NamedIntValueLabelProvider();
		}
		return instance;
	}
	
	@Override
	public String getText(Object element) {
		if (element instanceof NamedIntValue) {
			return ((NamedIntValue)element).getName();
		}
		return super.getText(element);
	}
}
