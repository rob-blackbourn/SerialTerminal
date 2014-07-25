package net.jetblack.serialterminal.ui.utils;

import java.util.ArrayList;
import java.util.List;

import net.jetblack.serialterminal.ui.io.SerialParameters;

import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.swt.widgets.Widget;

public abstract class ParameterSelectionListener implements SelectionListener {

	protected final SerialParameters serialParameters;
	protected final String propertyName;

	private final List<ParameterListener> listeners = new ArrayList<ParameterListener>();

	public ParameterSelectionListener(SerialParameters serialParameters, String propertyName) {
		this.serialParameters = serialParameters;
		this.propertyName = propertyName;
	}

	public void addListener(ParameterListener listener) {
		listeners.add(listener);
	}
	
	public void removeListener(ParameterListener listener) {
		listeners.remove(listener);
	}

	protected void notifyListeners(boolean isDefault, String text, Object oldValue, Object newValue) {
		for (ParameterListener listener : listeners) {
			if (isDefault) {
				listener.defaultParameterChanged(propertyName, text);
			} else {
				listener.parameterChanged(propertyName, text);
			}
		}
	}

	protected int getSelectionIndex(Widget widget) {
		if (widget instanceof Combo) {
			return ((Combo)widget).getSelectionIndex();
		} else if (widget instanceof MenuItem) {
			MenuItem menuItem = (MenuItem)widget;
			return menuItem.getParent().indexOf(menuItem);
		} else {
			return -1;
		}
	}

	protected boolean getSelection(Widget widget) {
		if (widget instanceof MenuItem) {
			return ((MenuItem)widget).getSelection();
		} else if (widget instanceof Button) {
			return ((Button)widget).getSelection();
		} else if (widget instanceof ToolItem) {
			return ((ToolItem)widget).getSelection();
		} else {
			return false;
		}
	}
	protected String getText(Widget widget) {
		if (widget instanceof Combo) {
			return ((Combo)widget).getText();
		} else if (widget instanceof MenuItem) {
			return ((MenuItem)widget).getText();
		} else {
			return null;
		}
	}

	@Override
	public void widgetSelected(SelectionEvent e) {
		handleSlectionEvent(false, e);
	}

	@Override
	public void widgetDefaultSelected(SelectionEvent e) {
		handleSlectionEvent(true, e);
	}

	protected abstract void handleSlectionEvent(boolean b, SelectionEvent e);
}
