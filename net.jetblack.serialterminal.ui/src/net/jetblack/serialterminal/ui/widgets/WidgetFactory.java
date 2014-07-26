package net.jetblack.serialterminal.ui.widgets;

import net.jetblack.serialterminal.ui.io.SerialParameters;
import net.jetblack.serialterminal.ui.io.SerialUtils;
import net.jetblack.serialterminal.ui.utils.BoolParameterSelectionListener;
import net.jetblack.serialterminal.ui.utils.IntParameterSelectionListener;
import net.jetblack.serialterminal.ui.utils.StringParameterSelectionListener;
import net.jetblack.serialterminal.ui.utils.TextParameterSelectionListener;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

public class WidgetFactory {
	
	public static MenuItem createCheckMenuItem(Menu parentMenu, String title, SerialParameters serialParameters, String propertyName) {
		MenuItem menuItem = new MenuItem(parentMenu, SWT.CHECK);
		menuItem.setText(title);
		menuItem.setSelection(serialParameters.getBoolean(propertyName));
		menuItem.addSelectionListener(new BoolParameterSelectionListener(serialParameters, propertyName));
		return menuItem;
	}
	
	public static Menu createRadioMenu(Menu parentMenu, SerialParameters serialParameters, String title, String propertyName, String[] names, int[] values, String defaultName) {
		SelectionListener listener = new IntParameterSelectionListener(serialParameters, propertyName, names, values);
		return WidgetFactory.createRadioMenu(parentMenu, title, names, defaultName, listener);
	}

	public static Menu createRadioMenu(Menu menu, SerialParameters serialParameters, String title, String propertyName, String[][] namesAndValues, String defaultValue) {
		SelectionListener selectionListener = new StringParameterSelectionListener(serialParameters, propertyName, namesAndValues);
		return createRadioMenu(menu, title, SerialUtils.getNames(namesAndValues), serialParameters.getLineEnding(), selectionListener);
	}

	public static Menu createRadioMenu(Menu menu, SerialParameters serialParameters, String title, String propertyName, String[] names, String defaultValue) {
		SelectionListener selectionListener = new TextParameterSelectionListener(serialParameters, propertyName);
		return createRadioMenu(menu, title, names, serialParameters.getLineEnding(), selectionListener);
	}

	public static Menu createRadioMenu(Menu parentMenu, String title, String[] names, String defaultName, SelectionListener selectionListener) {
		
		MenuItem rootMenuItem = new MenuItem(parentMenu, SWT.CASCADE);
		rootMenuItem.setText(title);
		
		Menu menu = new Menu(parentMenu);
		rootMenuItem.setMenu(menu);
		
		for (int i = 0; i < names.length; ++i) {
			createRadioMenuItem(menu, names[i], names[i].equals(defaultName), selectionListener);
		}

		return menu;
	}
	
	public static MenuItem createRadioMenuItem(Menu menu, String text, boolean isSelected, SelectionListener selectionListener) {
		MenuItem menuItem = new MenuItem(menu, SWT.RADIO);
		menuItem.setText(text);
		
		if (isSelected) {
			menuItem.setSelection(true);
		}
		
		if (selectionListener != null) {
			menuItem.addSelectionListener(selectionListener);
		}
		
		return menuItem;
	}
	
	public static Combo createCombo(Composite parent, String title, String[] names, String defaultName, SerialParameters serialParameters, String propertyName) {
		SelectionListener selectionListener = new TextParameterSelectionListener(serialParameters, propertyName);
		return createCombo(parent, names, defaultName, title, selectionListener);
	}
	
	public static Combo createCombo(Composite parent, String[] names, String defaultName, String toolTipText, SelectionListener selectionListener) {
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
		if (selectionListener != null) {
			combo.addSelectionListener(selectionListener);
		}
		return combo;
	}

	public static Combo createCombo(Composite parent, String[][] namesAndValues, String defaultValue, String toolTipText, SelectionListener selectionListener) {
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
		if (selectionListener != null) {
			combo.addSelectionListener(selectionListener);
		}

		return combo;
	}
	
	public static Button createButton(Composite parent, String text, Image image, String toolTipText, SelectionListener selectionListener) {
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
		if (selectionListener != null) {
			button.addSelectionListener(selectionListener);
		}
		return button;
	}
}
