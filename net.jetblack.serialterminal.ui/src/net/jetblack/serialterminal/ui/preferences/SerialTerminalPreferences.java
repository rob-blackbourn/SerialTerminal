package net.jetblack.serialterminal.ui.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

import net.jetblack.serialterminal.ui.Activator;
import net.jetblack.serialterminal.ui.io.SerialParameters;

public class SerialTerminalPreferences extends AbstractPreferenceInitializer implements SerialTerminalPreferenceConstants {

	public void initializeDefaultPreferences() {
		initialiseDefaultPreferences(Activator.getDefault().getPreferenceStore());
	}
	
	private void initialiseDefaultPreferences(IPreferenceStore store) {
		if (store != null) {
			store.setDefault(SERIAL_PORT, SerialParameters.getDefaultSerialPort());
			store.setDefault(BAUDRATE, SerialParameters.getDefaultBaudRate());
			store.setDefault(PARITY, SerialParameters.getDefaultParity());
			store.setDefault(STOPBITS, SerialParameters.getDefaultStopBits());
			store.setDefault(DATABITS, SerialParameters.getDefaultDataBits());
			store.setDefault(LINE_ENDING, SerialParameters.getDefaultLineEnding());
		}
	}
}
