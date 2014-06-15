package net.jetblack.serialterminal.ui.preferences.editors;

import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

public class StringComboFieldEditor extends FieldEditor {

	private Combo fCombo;
    private Button fRefreshButton;
	private final StringSource fStringSource;
	private String fValue;
	private String[] fEntries;

	public StringComboFieldEditor(String name, String labelText, StringSource stringSource, Composite parent) {
		init(name, labelText);
		fStringSource = stringSource;
		createControl(parent);		
	}

	protected void adjustForNumColumns(int numColumns) {
		if (numColumns > 1) {
			int left = numColumns;
			Label label = getLabelControl();
			if (label != null) {
				((GridData)label.getLayoutData()).horizontalSpan = 1;
				left = left - 1;
			}
			if (fRefreshButton != null) {
				((GridData)fRefreshButton.getLayoutData()).horizontalSpan = 1;
				left -= 1;
			}
			((GridData)fCombo.getLayoutData()).horizontalSpan = left;
		} else {
			Label label = getLabelControl();
			if (label != null) {
				((GridData)label.getLayoutData()).horizontalSpan = 1;
			}
			if (fRefreshButton != null) {
				((GridData)fRefreshButton.getLayoutData()).horizontalSpan = 1;
			}
			((GridData)fCombo.getLayoutData()).horizontalSpan = 1;			
		}
	}

	protected void doFillIntoGrid(Composite parent, int numColumns) {
		int comboC = 1;
		if (numColumns > 1) {
			comboC = numColumns - 2;
		}
		
		Label label = getLabelControl(parent);
		GridData labelGridData = new GridData();
		labelGridData.horizontalSpan = 1;
		label.setLayoutData(labelGridData);
		
		Combo combo = getComboBoxControl(parent);
		GridData comboGridData = new GridData();
		comboGridData.horizontalSpan = comboC;
		comboGridData.horizontalAlignment = GridData.FILL;
		combo.setLayoutData(comboGridData);
		combo.setFont(parent.getFont());
		
		Button button = getButtonControl(parent);
		GridData buttonGridData = new GridData();
		buttonGridData.horizontalSpan = 1;
		button.setLayoutData(buttonGridData);
	}

	protected void doLoad() {
		updateComboForValue(getPreferenceStore().getString(getPreferenceName()));
	}

	protected void doLoadDefault() {
		updateComboForValue(getPreferenceStore().getDefaultString(getPreferenceName()));
	}

	protected void doStore() {
		if (fValue == null) {
			getPreferenceStore().setToDefault(getPreferenceName());
			return;
		}
		getPreferenceStore().setValue(getPreferenceName(), fValue);
	}

	public int getNumberOfControls() {
		return 3;
	}

    protected Button getButtonControl(final Composite parent) {
        if (fRefreshButton == null) {
            fRefreshButton = new Button(parent, SWT.PUSH);
            fRefreshButton.setText("Refresh");
            fRefreshButton.setFont(parent.getFont());
            fRefreshButton.addSelectionListener(new SelectionAdapter() {
            	@Override
                public void widgetSelected(SelectionEvent evt) {
        			fEntries = fStringSource.getStrings();
            		fCombo.setItems(fEntries);
            		if (fEntries.length > 0) {
            			fCombo.select(0);
            		}
            		parent.layout();
                }
            });
            fRefreshButton.addDisposeListener(new DisposeListener() {
                public void widgetDisposed(DisposeEvent event) {
                    fRefreshButton = null;
                }
            });
        } else {
            checkParent(fRefreshButton, parent);
        }
        return fRefreshButton;
    }

	private Combo getComboBoxControl(Composite parent) {
		if (fCombo == null) {
			fEntries = fStringSource.getStrings();
			fCombo = new Combo(parent, SWT.READ_ONLY);
			fCombo.setFont(parent.getFont());
			for (int i = 0; i < fEntries.length; ++i) {
				fCombo.add(fEntries[i], i);
			}
			
			fCombo.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent evt) {
					String oldValue = fValue;
					fValue = fCombo.getText();
					setPresentsDefaultValue(false);
					fireValueChanged(VALUE, oldValue, fValue);					
				}
			});
		}
		return fCombo;
	}
	
	private void updateComboForValue(String value) {
		fValue = value;
		for (int i = 0; i < fEntries.length; ++i) {
			if (value.equals(fEntries[i])) {
				fCombo.setText(value);
				return;
			}
		}
		if (fEntries.length > 0) {
			fValue = fEntries[0];
			fCombo.setText(fEntries[0]);
		}
	}

	public void setEnabled(boolean enabled, Composite parent) {
		super.setEnabled(enabled, parent);
		getComboBoxControl(parent).setEnabled(enabled);
	}
}
