package net.jetblack.serialterminal.ui.preferences.editors;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

public class IntComboFieldEditor extends FieldEditor {
	
	private final String[] fEntryNames;
	private final int[] fEntryValues;
	private final int fInvalidValue;

	private Combo fCombo;
	private int fValue;

	public IntComboFieldEditor(String name, String labelText, String[] entryNames, int[] entryValues, int invalidValue, Composite parent) {
		init(name, labelText);
		Assert.isTrue(checkArray(entryNames, entryValues));
		fEntryNames = entryNames;
		fEntryValues = entryValues;
		fInvalidValue = invalidValue;
		createControl(parent);		
	}

	private boolean checkArray(String[] names, int[] values) {
		if (names == null || values == null) {
			return false;
		}
		for (int i = 0; i < names.length; i++) {
			String name = names[i];
			if (name == null) {
				return false;
			}
		}
		return true;
	}

	protected void adjustForNumColumns(int numColumns) {
		if (numColumns > 1) {
			Control control = getLabelControl();
			int left = numColumns;
			if (control != null) {
				((GridData)control.getLayoutData()).horizontalSpan = 1;
				left = left - 1;
			}
			((GridData)fCombo.getLayoutData()).horizontalSpan = left;
		} else {
			Control control = getLabelControl();
			if (control != null) {
				((GridData)control.getLayoutData()).horizontalSpan = 1;
			}
			((GridData)fCombo.getLayoutData()).horizontalSpan = 1;			
		}
	}

	protected void doFillIntoGrid(Composite parent, int numColumns) {
		int comboC = 1;
		if (numColumns > 1) {
			comboC = numColumns - 1;
		}
		Control control = getLabelControl(parent);
		GridData gd = new GridData();
		gd.horizontalSpan = 1;
		control.setLayoutData(gd);
		control = getComboBoxControl(parent);
		gd = new GridData();
		gd.horizontalSpan = comboC;
		gd.horizontalAlignment = GridData.FILL;
		control.setLayoutData(gd);
		control.setFont(parent.getFont());
	}

	protected void doLoad() {
		updateComboForValue(getPreferenceStore().getInt(getPreferenceName()));
	}

	protected void doLoadDefault() {
		updateComboForValue(getPreferenceStore().getDefaultInt(getPreferenceName()));
	}

	protected void doStore() {
		if (fValue == fInvalidValue) {
			getPreferenceStore().setToDefault(getPreferenceName());
			return;
		}
		getPreferenceStore().setValue(getPreferenceName(), fValue);
	}

	public int getNumberOfControls() {
		return 2;
	}

	private Combo getComboBoxControl(Composite parent) {
		if (fCombo == null) {
			fCombo = new Combo(parent, SWT.READ_ONLY);
			fCombo.setFont(parent.getFont());
			for (int i = 0; i < fEntryNames.length; ++i) {
				fCombo.add(fEntryNames[i], i);
			}
			
			fCombo.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent evt) {
					int oldValue = fValue;
					String name = fCombo.getText();
					fValue = getValueForName(name);
					setPresentsDefaultValue(false);
					fireValueChanged(VALUE, oldValue, fValue);					
				}
			});
		}
		return fCombo;
	}
	
	private int getValueForName(String name) {
		for (int i = 0; i < fEntryNames.length; ++i) {
			if (name.equals(fEntryNames[i])) {
				return fEntryValues[i];
			}
		}
		return fEntryValues[0];
	}
	
	private void updateComboForValue(int value) {
		fValue = value;
		for (int i = 0; i < fEntryValues.length; ++i) {
			if (value == fEntryValues[i]) {
				fCombo.setText(fEntryNames[i]);
				return;
			}
		}
		if (fEntryNames.length > 0) {
			fValue = fEntryValues[0];
			fCombo.setText(fEntryNames[0]);
		}
	}

	public void setEnabled(boolean enabled, Composite parent) {
		super.setEnabled(enabled, parent);
		getComboBoxControl(parent).setEnabled(enabled);
	}
}
