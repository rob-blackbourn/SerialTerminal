package net.jetblack.serialterminal.ui.utils;

import org.eclipse.jface.viewers.LabelProvider;

public class StringOfStringsLabelProvider extends LabelProvider {

	private static StringOfStringsLabelProvider instance;
	
	public StringOfStringsLabelProvider getInstance() {
		if (instance == null) {
			instance = new StringOfStringsLabelProvider();
		}
		return instance;
	}
	
	@Override
	public String getText(Object element) {
		if (element instanceof String[]) {
			return ((String[])element)[0];
		}
		return super.getText(element);
	}
}
