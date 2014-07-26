package net.jetblack.serialterminal.ui.views;

import jssc.SerialPortException;
import jssc.SerialPortList;

import org.eclipse.jface.action.ContributionItem;
import org.eclipse.jface.action.ControlContribution;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.part.*;
import org.eclipse.swt.SWT;

import net.jetblack.serialterminal.ui.Activator;
import net.jetblack.serialterminal.ui.io.SerialConnection;
import net.jetblack.serialterminal.ui.io.SerialParameters;
import net.jetblack.serialterminal.ui.io.SerialUtils;
import net.jetblack.serialterminal.ui.preferences.SerialTerminalPreferenceConstants;
import net.jetblack.serialterminal.ui.swt.layout.Margin;
import net.jetblack.serialterminal.ui.swt.layout.StripData;
import net.jetblack.serialterminal.ui.swt.layout.StripLayout;
import net.jetblack.serialterminal.ui.utils.TextParameterSelectionListener;
import net.jetblack.serialterminal.ui.widgets.OutputWidget;
import net.jetblack.serialterminal.ui.widgets.SendWidget;
import net.jetblack.serialterminal.ui.widgets.SendWidgetListener;
import net.jetblack.serialterminal.ui.widgets.WidgetFactory;

public class SerialTerminalView
	extends ViewPart
	implements SerialTerminalPreferenceConstants,
		SendWidgetListener {

	public static final String ID = "net.jetblack.serialterminal.ui.views.SerialTerminalView";

	private final SerialParameters serialParameters;
	private String[] _serialPorts = null;
	
	private Composite sendRow;
	private OutputWidget outputWidget;
	
	private SerialConnection serialConnection;

	public SerialTerminalView() {
		IPreferenceStore preferenceStore = Activator.getDefault().getPreferenceStore();
		serialParameters = new SerialParameters(preferenceStore);
	}

	public void createPartControl(Composite parent) {
		createView(parent);
		openConnection();
	}

	private void createView(Composite parent) {

		IPreferenceStore preferenceStore = Activator.getDefault().getPreferenceStore();

		parent.setLayout(new StripLayout(false));

		// 1st Row
		sendRow = new Composite(parent, SWT.NO_TRIM);
		sendRow.setLayoutData(new StripData(true, false, new Margin(3, 3, 3, 0)));
		sendRow.setLayout(new StripLayout(true));
		SendWidget sendWidget = new SendWidget(sendRow, preferenceStore, serialParameters);
		sendWidget.addLIstener(this);
		
		// 2nd row
		StyledText textOutput = new StyledText(parent, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		textOutput.setLayoutData(new StripData(true, true, new Margin(3)));
		textOutput.setEditable(false);
		outputWidget = new OutputWidget(textOutput, serialParameters);
		
		contributeToActionBars();
	}
	
	public void setFocus() {
		if (sendRow != null) {
			sendRow.setFocus();
		}
	}

	private void contributeToActionBars() {
		IActionBars actionBars = getViewSite().getActionBars();
		fillLocalToolBar(actionBars.getToolBarManager());
		fillLocalPullDown(actionBars.getMenuManager());
	}
	
	private void fillLocalToolBar(IToolBarManager manager) {

		
		manager.add(new ControlContribution("reconnectButton") {

			@Override
			protected Control createControl(Composite parent) {
				return WidgetFactory.createButton(
						parent,
						null,
						new Image(parent.getDisplay(), Activator.class.getResourceAsStream("/icons/connect.png")),
						"Reconnect",
						new SelectionListener() {
							@Override
							public void widgetSelected(SelectionEvent e) {
								reconnect();
							}
							
							@Override
							public void widgetDefaultSelected(SelectionEvent e) {
								reconnect();
							}
						});
			}
			
		});

		manager.add(new ControlContribution("serialPortCombo") {

			@Override
			protected Control createControl(Composite parent) {
				
				Composite composite = new Composite(parent, SWT.NO_TRIM);
				StripLayout layout = new StripLayout();
				composite.setLayout(layout);
				
				_serialPorts = SerialPortList.getPortNames();
				Combo combo = WidgetFactory.createCombo(
						composite,
						_serialPorts,
						serialParameters.getPortName(),
						"Serial port",
						new TextParameterSelectionListener(serialParameters, SERIAL_PORT));

				combo.pack();
				Point size = combo.getSize();
				int width = Math.max(size.x, 100);
				combo.setLayoutData(new StripData(true, true, width, SWT.DEFAULT));

				return composite;
			}
			
			@Override
			public boolean isDynamic() {
				return true;
			}
			
			@Override
			public boolean isDirty() {
				return _serialPorts == null;
			}
		});
		
		manager.add(new ControlContribution("refreshButton") {

			@Override
			protected Control createControl(Composite parent) {
				return WidgetFactory.createButton(
						parent,
						null,
						new Image(parent.getDisplay(), Activator.class.getResourceAsStream("/icons/refresh.png")),
						"Refresh",
						new SelectionListener() {
							@Override
							public void widgetSelected(SelectionEvent e) {
								refreshSerialPorts();
							}
							
							@Override
							public void widgetDefaultSelected(SelectionEvent e) {
								refreshSerialPorts();
							}
						});
			}
			
		});
	}
	
	private void fillLocalPullDown(IMenuManager manager) {
		manager.add(new ContributionItem("Configuration") {
			@Override
			public void fill(Menu menu, int index) {
				
				WidgetFactory.createCheckMenuItem(menu, "Text", serialParameters, SHOW_TEXT);
				WidgetFactory.createCheckMenuItem(menu, "Wrap", serialParameters, WRAP);
				
				WidgetFactory.createRadioMenu(menu, serialParameters, "Baud Rate", BAUDRATE, SerialUtils.BAUDRATE_NAMES, SerialUtils.BAUDRATE_VALUES, SerialUtils.getBaudRateName(serialParameters.getBaudRate()));
				WidgetFactory.createRadioMenu(menu, serialParameters, "Parity", PARITY, SerialUtils.PARITY_NAMES, SerialUtils.PARITY_VALUES, SerialUtils.getParityName(serialParameters.getParity()));
				WidgetFactory.createRadioMenu(menu, serialParameters, "Data Bits", DATABITS, SerialUtils.DATABITS_NAMES, SerialUtils.DATABITS_VALUES, SerialUtils.getDataBitsName(serialParameters.getDataBits()));
				WidgetFactory.createRadioMenu(menu, serialParameters, "Stop Bits", STOPBITS, SerialUtils.STOPBITS_NAMES, SerialUtils.STOPBITS_VALUES, SerialUtils.getStopBitsName(serialParameters.getStopBits()));
				WidgetFactory.createRadioMenu(menu, serialParameters, "Line Ending", LINE_ENDING, SerialUtils.LINE_ENDING_NAMES_AND_VALUES, SerialUtils.getLineEndingName(serialParameters.getLineEnding()));
				WidgetFactory.createRadioMenu(menu, serialParameters, "Encoding", ENCODING, SerialUtils.ENCODING_NAMES, serialParameters.getEncoding());
			}
		});
	}
	
	private void refreshSerialPorts() {
		_serialPorts = null;
		getViewSite()
			.getActionBars()
			.getToolBarManager()
			.update(false);
	}
	
	private void openConnection() {
		if (!serialParameters.isValid()) {
			return;
		}

		outputWidget.showStatus("Connecting: " + serialParameters + "\n");
		
		try {
			serialConnection = new SerialConnection(serialParameters.getPortName(), serialParameters.getBaudRate(), serialParameters.getDataBits(), serialParameters.getStopBits(), serialParameters.getParity());
			serialConnection.addSerialListener(outputWidget);
		} catch (SerialPortException e) {
			outputWidget.showError("Failed to open connection", e);
		}
	}

	private void closeConnection() {
		if (serialConnection == null) {
			return;
		}
		
		outputWidget.showStatus("Disconnecting\n");

		try {
			serialConnection.removeSerialListener(outputWidget);
			serialConnection.close();
			serialConnection = null;
		} catch (SerialPortException e) {
			outputWidget.showError("Failed to close connection", e);
		}
	}

	public void reconnect() {
		closeConnection();
		openConnection();
	}

	@Override
	public void send(byte[] buf) {
		if (serialConnection == null) {
			return;
		}
		
		try {
			serialConnection.write(buf);
		} catch (SerialPortException e) {
			outputWidget.showError("write", e);
		}
	}
	
	@Override
	public void error(String message, Exception e) {
		outputWidget.showError(message, e);
	}
}