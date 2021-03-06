package net.jetblack.serialterminal.ui.preferences;

import jssc.SerialPortList;

import org.eclipse.jface.preference.*;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.IWorkbench;

import net.jetblack.serialterminal.ui.Activator;
import net.jetblack.serialterminal.ui.io.SerialUtils;
import net.jetblack.serialterminal.ui.preferences.editors.IntComboFieldEditor;
import net.jetblack.serialterminal.ui.preferences.editors.StringComboFieldEditor;
import net.jetblack.serialterminal.ui.preferences.editors.StringSource;

public class SerialTerminalPreferencePage extends FieldEditorPreferencePage
		implements IWorkbenchPreferencePage, SerialTerminalPreferenceConstants {

	public SerialTerminalPreferencePage() {
		super(GRID);
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
		setDescription("Configuration settings for the serial terminal");
	}

	public void createFieldEditors() {
		addField(new StringComboFieldEditor(SERIAL_PORT, "Serial Port", getSerialPortStringSource(), getFieldEditorParent()));
		addField(new IntComboFieldEditor(BAUDRATE, "Baud Rate", SerialUtils.BAUDRATE_NAMES, SerialUtils.BAUDRATE_VALUES, -1, getFieldEditorParent()));
		addField(new IntComboFieldEditor(PARITY, "Parity", SerialUtils.PARITY_NAMES, SerialUtils.PARITY_VALUES, -1, getFieldEditorParent()));
		addField(new IntComboFieldEditor(DATABITS, "Data Bits", SerialUtils.DATABITS_NAMES, SerialUtils.DATABITS_VALUES, -1, getFieldEditorParent()));
		addField(new IntComboFieldEditor(STOPBITS, "Stop Bits", SerialUtils.STOPBITS_NAMES, SerialUtils.STOPBITS_VALUES, -1, getFieldEditorParent()));
		addField(new ComboFieldEditor(LINE_ENDING, "Line Ending", SerialUtils.LINE_ENDING_NAMES_AND_VALUES, getFieldEditorParent()));
		addField(new StringComboFieldEditor(ENCODING, "Encoding", getEncodingStringSource(), getFieldEditorParent()));
		addField(new StringComboFieldEditor(ENCODING, "Encoding", getOutputFormatStringSource(), getFieldEditorParent()));
	}

	public void init(IWorkbench workbench) {
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
	}
	
	private static final StringSource getSerialPortStringSource() {
		return new StringSource() {
			@Override
			public String[] getStrings() {
				return SerialPortList.getPortNames();
			}
		};
	}
	
	private static final StringSource getEncodingStringSource() {
		return new StringSource() {
			
			@Override
			public String[] getStrings() {
				return SerialUtils.ENCODING_NAMES;
			}
		};
	}
	
	private static final StringSource getOutputFormatStringSource() {
		return new StringSource() {
			
			@Override
			public String[] getStrings() {
				return SerialUtils.OUTPUT_FORMAT_NAMES;
			}
		};
	}
}