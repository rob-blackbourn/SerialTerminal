package net.jetblack.serialterminal.ui.swt.layout;

/**
 * Instances of this class provide information on the margins surrounding a control.
 */
public class Margin {
	
	/** The space to the left of a control. */
	public final int left;
	
	/** The space above a control. */
	public final int top;
	
	/** The space to the right of a control. */
	public final int right;
	
	/** The space below a control. */
	public final int bottom;

	/**
	 * Instantiates a new margin.
	 *
	 * @param left the left margin
	 * @param top the top margin
	 * @param right the right margin
	 * @param bottom the bottom margin
	 */
	public Margin(int left, int top, int right, int bottom) {
		this.left = left;
		this.top = top;
		this.right = right;
		this.bottom = bottom;
	}
	
	/**
	 * Instantiates a new margin.
	 *
	 * @param margin the margin for all sides
	 */
	public Margin(int margin) {
		this(margin, margin, margin, margin);
	}
	
	/**
	 * Instantiates a new margin.
	 *
	 * @param width the left and right margins
	 * @param height the top and bottom margins
	 */
	public Margin(int width, int height) {
		this(width, height, width, height);
	}
	
	/**
	 * Gets the total width of the left margin and the right margin.
	 * 
	 * @return the left margin plus the right margin
	 */
	public int getWidth() {
		return left + right;
	}
	
	/**
	 * Gets the total height of the top margin and the bottom margin.
	 * 
	 * @return the top margin plus the bottom margin
	 */
	public int getHeight() {
		return top + bottom;
	}
}
