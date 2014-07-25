package net.jetblack.serialterminal.ui.utils;

public interface ParameterListener {
	public void parameterChanged(String propertyName, String displayName);
	public void defaultParameterChanged(String propertyName, String displayName);
}
