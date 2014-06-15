package net.jetblack.serialterminal.ui.io;

public interface SerialParametersListener {
	public void onChanged(SerialParameters serialParameters, Object parameter, Object oldValue, Object newValue);
}
