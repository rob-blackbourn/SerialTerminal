package net.jetblack.serialterminal.ui.widgets;

import java.util.ArrayList;
import java.util.List;
import jssc.SerialPortList;
import net.jetblack.serialterminal.ui.Activator;
import net.jetblack.serialterminal.ui.io.SerialParameters;
import net.jetblack.serialterminal.ui.io.SerialUtils;
import net.jetblack.serialterminal.ui.preferences.SerialTerminalPreferenceConstants;
import net.jetblack.serialterminal.ui.swt.layout.Margin;
import net.jetblack.serialterminal.ui.swt.layout.StripData;
import net.jetblack.serialterminal.ui.utils.IntParameterSelectionListener;
import net.jetblack.serialterminal.ui.utils.ParameterListener;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;

public class PreferenceWidget implements SerialTerminalPreferenceConstants, SelectionListener, ParameterListener, IPropertyChangeListener {

	private final Composite parent;
	private final SerialParameters serialParameters;
	private final Button reconnectButton, refreshSerialPortsButton;
	private final Combo serialPortCombo, baudRateCombo, dataBitsCombo, stopBitsCombo, parityCombo;
	
	private final List<PreferenceWidgetListener> listeners = new ArrayList<PreferenceWidgetListener>();
	
	public PreferenceWidget(Composite parent, IPreferenceStore preferenceStore, SerialParameters serialParameters) {
		
		this.serialParameters = serialParameters;
		this.parent = parent;
		
		reconnectButton = WidgetFactory.createButton(parent, "Reconnect", null, "Close and open the serial port", this);
		reconnectButton.setLayoutData(new StripData(false, false, new Margin(0, 0, 3, 0)));
		
		serialPortCombo = WidgetFactory.createCombo(parent, SerialPortList.getPortNames(), serialParameters.getPortName(), "Serial port", this);
		serialPortCombo.setLayoutData(new StripData(true, false, new Margin(0, 0, 3, 0)));
		
		refreshSerialPortsButton = WidgetFactory.createButton(parent, null, new Image(parent.getDisplay(), Activator.class.getResourceAsStream("/icons/refresh.png")), "Refresh", this);
		refreshSerialPortsButton.setLayoutData(new StripData(false, false, new Margin(0, 0, 3, 0)));
		
		baudRateCombo = WidgetFactory.createCombo(parent, SerialUtils.BAUDRATE_NAMES, SerialUtils.getBaudRateName(serialParameters.getBaudRate()), "Baud rate", new IntParameterSelectionListener(serialParameters, BAUDRATE, SerialUtils.BAUDRATE_NAMES, SerialUtils.BAUDRATE_VALUES));
		baudRateCombo.setLayoutData(new StripData(false, false, new Margin(0, 0, 3, 0)));

		dataBitsCombo = WidgetFactory.createCombo(parent, SerialUtils.DATABITS_NAMES, SerialUtils.getDataBitsName(serialParameters.getDataBits()), "Data bits", new IntParameterSelectionListener(serialParameters, DATABITS, SerialUtils.DATABITS_NAMES, SerialUtils.DATABITS_VALUES));
		dataBitsCombo.setLayoutData(new StripData(false, false, new Margin(0, 0, 3, 0)));

		parityCombo = WidgetFactory.createCombo(parent, SerialUtils.PARITY_NAMES, SerialUtils.getParityName(serialParameters.getParity()), "Parity", new IntParameterSelectionListener(serialParameters, PARITY, SerialUtils.PARITY_NAMES, SerialUtils.PARITY_VALUES));
		parityCombo.setLayoutData(new StripData(false, false, new Margin(0, 0, 3, 0)));

		stopBitsCombo = WidgetFactory.createCombo(parent, SerialUtils.STOPBITS_NAMES, SerialUtils.getStopBitsName(serialParameters.getStopBits()), "Stop bits", new IntParameterSelectionListener(serialParameters, STOPBITS, SerialUtils.STOPBITS_NAMES, SerialUtils.STOPBITS_VALUES));
		stopBitsCombo.setLayoutData(new StripData(false, false, new Margin(0, 0, 3, 0)));
		
		preferenceStore.addPropertyChangeListener(this);
	}
	

	@Override
	public void propertyChange(PropertyChangeEvent event) {
		if (SERIAL_PORT.equals(event.getProperty())) {
			parameterChanged(SERIAL_PORT, (String)event.getNewValue());
		} else if (BAUDRATE.equals(event.getProperty())) {
			parameterChanged(BAUDRATE, SerialUtils.getBaudRateName((int)event.getNewValue()));
		} else if (PARITY.equals(event.getProperty())) {
			parameterChanged(PARITY, SerialUtils.getParityName((int)event.getNewValue()));
		} else if (DATABITS.equals(event.getProperty())) {
			parameterChanged(DATABITS, SerialUtils.getDataBitsName((int)event.getNewValue()));
		} else if (STOPBITS.equals(event.getProperty())) {
			parameterChanged(STOPBITS, SerialUtils.getStopBitsName((int)event.getNewValue()));
		}
	}

	@Override
	public void parameterChanged(String name, String text) {
		boolean isChanged = true;
		
		if (SERIAL_PORT.equals(name)) {
			serialPortCombo.setText(text);
		} else if (BAUDRATE.equals(name)) {
			baudRateCombo.setText(text);
		} else if (PARITY.equals(name)) {
			parityCombo.setText(text);
		} else if (STOPBITS.equals(name)) {
			stopBitsCombo.setText(text);
		} else if (DATABITS.equals(name)) {
			dataBitsCombo.setText(text);
		} else {
			isChanged = false;
		}
		
		if (isChanged) {
			notifyListeners();
		}
	}

	@Override
	public void defaultParameterChanged(String name, String text) {
	}

	@Override
	public void widgetSelected(SelectionEvent e) {
		if (serialPortCombo == e.getSource()) {
			onSerialPortChanged();
		} else if (refreshSerialPortsButton == e.getSource()) {
			onRefreshSerialPorts();
		} else if (reconnectButton == e.getSource()) {
			onReconnectClicked();
		}
	}

	@Override
	public void widgetDefaultSelected(SelectionEvent e) {
	}
	
	public void addListener(PreferenceWidgetListener listener) {
		listeners.add(listener);
	}
	
	public void removeListener(PreferenceWidgetListener listener) {
		listeners.remove(listener);
	}
	
	private void notifyListeners() {
		for (PreferenceWidgetListener listener : listeners) {
			listener.onReconnect();
		}
	}

	private void onSerialPortChanged() {
		int index = serialPortCombo.getSelectionIndex();
		if (index != -1) {
			String portName = serialPortCombo.getText();
			serialParameters.setPortName(portName);
		}
	}
	
	private void onRefreshSerialPorts() {
		String[] serialPorts = SerialPortList.getPortNames();
		if (serialPorts.length > 0) {
			serialPortCombo.setItems(serialPorts);
			parent.layout(true, true);
		}
	}
	
	private void onReconnectClicked() {
		notifyListeners();
	}
}
