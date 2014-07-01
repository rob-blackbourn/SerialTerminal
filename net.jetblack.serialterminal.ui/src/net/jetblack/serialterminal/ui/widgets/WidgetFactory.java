package net.jetblack.serialterminal.ui.widgets;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;

public class WidgetFactory {
	
	public static Combo createCombo(Composite parent, String[] names, String defaultName, String toolTipText) {
		Combo combo = new Combo(parent, SWT.READ_ONLY);
		combo.setItems(names);
		if (combo.getItemCount() > 0) {
			int index = combo.indexOf(defaultName);
			if (index != -1) {
				combo.select(index);
			}
		}
		if (toolTipText != null) {
			combo.setToolTipText(toolTipText);
		}
		return combo;
	}

	public static Combo createCombo(Composite parent, String[][] namesAndValues, String defaultValue, String toolTipText) {
		Combo combo = new Combo(parent, SWT.READ_ONLY);
		int selectedValue = -1;
		for (int i = 0; i < namesAndValues.length; ++i) {
			combo.add(namesAndValues[i][0]);
			if (namesAndValues[i][1].equals(defaultValue)) {
				selectedValue = i;
			}
		}
		if (selectedValue != -1) {
			combo.select(selectedValue);
		}
		if (toolTipText != null) {
			combo.setToolTipText(toolTipText);
		}

		return combo;
	}
	
	public static Button createButton(Composite parent, String text, Image image, String toolTipText) {
		Button button = new Button(parent, SWT.DEFAULT);
		if (image != null) {
			button.setImage(image);
			button.addDisposeListener(new DisposeListener() {
				@Override
				public void widgetDisposed(DisposeEvent e) {
					Button button = (Button)e.widget;
					button.getImage().dispose();
				}
			});
		}
		if (text != null) {
			button.setText(text);
		}
		if (toolTipText != null) {
			button.setToolTipText(toolTipText);
		}
		return button;
	}
}
