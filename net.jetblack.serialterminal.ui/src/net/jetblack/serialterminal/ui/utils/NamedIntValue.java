package net.jetblack.serialterminal.ui.utils;

public class NamedIntValue {

	private String name;
	private int value;
	
	public NamedIntValue(String name, int value) {
		this.setName(name);
		this.setValue(value);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}
	
	public static NamedIntValue[] toArray(String[] names, int[] values) {
		if (names == null || values == null || names.length != values.length) {
			throw new IllegalArgumentException("arrays must be non-null and the same length");
		}
		int n = names.length;
		NamedIntValue[] namesAndValues = new NamedIntValue[n];
		for (int i = 0; i < n; ++i) {
			namesAndValues[i] = new NamedIntValue(names[i], values[i]);
		}
		return namesAndValues;
	}
}
