package net.jetblack.serialterminal.ui.swt.layout;

import org.eclipse.swt.SWT;

/**
 * Instances of this class provide layout data for the StripData class.
 */
public final class StripData {

	/** Indicates if the control should fill the space in the horizontal dimension. */
	public final boolean fillWidth;
	
	/** Indicates if the control should fill the space in the vertical dimension. */
	public final boolean fillHeight;
	
	/** The desired size. */
	public final Size size;
	
	/** The margin. */
	public final Margin margin;
	
	/** Whether the control should be excluded from the layout. */
	public boolean exclude = false;
	
	/**
	 * Instantiates a new strip data object with no fill.
	 */
	public StripData () {
		this(false, false);
	}

	/**
	 * Instantiates a new strip data with the given fill and the desire size of SWT.DEFAULT.
	 *
	 * @param fillWidth whether to fill horizontally
	 * @param fillHeight whether to fill vertically
	 */
	public StripData (boolean fillWidth, boolean fillHeight) {
		this(fillWidth, fillHeight, SWT.DEFAULT, SWT.DEFAULT, 0);
	}

	/**
	 * Instantiates a new strip data with the given fill and desired size.
	 *
	 * @param fillWidth whether to fill horizontally
	 * @param fillHeight whether to fill vertically
	 * @param width the width
	 * @param height the height
	 */
	public StripData (boolean fillWidth, boolean fillHeight, int width, int height) {
		this(fillWidth, fillHeight, width, height, 0);
	}

	/**
	 * Instantiates a new strip data.
	 *
	 * @param fillWidth whether to fill horizontally
	 * @param fillHeight whether to fill vertically
	 * @param width the width
	 * @param height the height
	 * @param margin the margin
	 */
	public StripData (boolean fillWidth, boolean fillHeight, int width, int height, int margin) {
		this(fillWidth, fillHeight, width, height, margin, margin, margin, margin);
	}
	
	/**
	 * Instantiates a new strip data.
	 *
	 * @param fillWidth whether to fill horizontally
	 * @param fillHeight whether to fill vertically
	 * @param width the width
	 * @param height the height
	 * @param left the left
	 * @param top the top
	 * @param right the right
	 * @param bottom the bottom
	 */
	public StripData (boolean fillWidth, boolean fillHeight, int width, int height, int left, int top, int right, int bottom) {
		this(fillWidth, fillHeight, new Size(width, height), new Margin(left, top, right, bottom));
	}

	/**
	 * Instantiates a new strip data.
	 *
	 * @param fillWidth whether to fill horizontally
	 * @param fillHeight whether to fill vertically
	 * @param size the size
	 */
	public StripData (boolean fillWidth, boolean fillHeight, Size size) {
		this(fillWidth, fillHeight, size, new Margin(0));
	}
	
	/**
	 * Instantiates a new strip data.
	 *
	 * @param fillWidth whether to fill horizontally
	 * @param fillHeight whether to fill vertically
	 * @param margin the margin
	 */
	public StripData (boolean fillWidth, boolean fillHeight, Margin margin) {
		this(fillWidth, fillHeight, new Size(SWT.DEFAULT, SWT.DEFAULT), margin);
	}
	
	/**
	 * Instantiates a new strip data.
	 *
	 * @param fillWidth whether to fill horizontally
	 * @param fillHeight whether to fill vertically
	 * @param size the size
	 * @param margin the margin
	 */
	public StripData (boolean fillWidth, boolean fillHeight, Size size, Margin margin) {
		this.fillWidth = fillWidth;
		this.fillHeight = fillHeight;
		this.size = size;
		this.margin = margin;
	}
	
	/**
	 * Gets the name.
	 *
	 * @return the name
	 */
	private String getName () {
		String string = getClass().getName();
		int index = string.lastIndexOf ('.');
		if (index == -1) return string;
		return string.substring (index + 1, string.length ());
	}

	public String toString () {
		String string = getName ()+" {";
		if (fillWidth) string += "fillWidth="+fillWidth+" ";
		if (fillHeight) string += "fillHeight="+fillHeight+" ";
		if (size.width != SWT.DEFAULT) string += "width="+size.width+" ";
		if (size.height != SWT.DEFAULT) string += "height="+size.height+" ";
		if (exclude) string += "exclude="+exclude+" ";
		string = string.trim();
		string += "}";
		return string;
	}
}
