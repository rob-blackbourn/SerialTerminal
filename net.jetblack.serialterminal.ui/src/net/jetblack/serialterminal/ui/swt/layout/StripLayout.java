package net.jetblack.serialterminal.ui.swt.layout;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Layout;

/**
 * Instances of this class lays out controls in a horizontal or vertical
 * strip, with the specified fill and margin.
 */
public final class StripLayout extends Layout {

	/** Indicates whether the layout should be horizontal or vertical. */
	public final boolean isHorizontal;

	/**
	 * Instantiates a new strip layout.
	 */
	public StripLayout() {
		this(true);
	}
	
	/**
	 * Instantiates a new strip layout.
	 *
	 * @param isHorizontal specifies whether the layout should be horizontal or vertical
	 */
	public StripLayout(boolean isHorizontal) {
		this.isHorizontal = isHorizontal;
	}
	
	@Override
	protected Point computeSize(Composite composite, int wHint, int hHint, boolean flushCache) {
		Point extent = layoutOrSize(composite, false, flushCache);
		if (wHint != SWT.DEFAULT) extent.x = wHint;
		if (hHint != SWT.DEFAULT) extent.y = hHint;
		return extent;
	}

	@Override
	protected void layout(Composite composite, boolean flushCache) {
		layoutOrSize(composite, true, flushCache);
	}

	@Override
	protected boolean flushCache (Control control) {
		return true;
	}
	
	/**
	 * Layout or size.
	 *
	 * @param composite the control to layout
	 * @param isLayout specifies whether the layout should be applied or just sized.
	 * @param flushCache whether to flush the cache
	 * @return the size of the layed out control
	 */
	private Point layoutOrSize(Composite composite, boolean isLayout, boolean flushCache) {

		Rectangle clientArea = getCroppedClientArea(composite, isLayout);
		
		// find all non-excluded child controls.
		Control [] children = composite.getChildren ();
		int count = 0;
		for (int i=0; i<children.length; ++i) {
			Control child = children [i];
			StripData data = (StripData) child.getLayoutData ();
			
			// ensure all controls have layout data.
			if (data == null) {
				child.setLayoutData(data = new StripData());
			}
			
			if (!data.exclude) {
				children [count++] = children [i];
			} 
		}

		if (count == 0) {
			//return new Point (margin.left + margin.right, margin.top + margin.bottom);
			return new Point (0, 0);
		}
		
		int fillChildCount = 0;
		int maxChild = 0;
		int minStrip = 0;
		Point[] sizes = new Point[count];
		for (int i=0; i<count; ++i) {
			Control child = children [i];
			
			StripData data = (StripData)child.getLayoutData();
			
			if ((data.fillWidth && isHorizontal) || (data.fillHeight && !isHorizontal)) {
				++fillChildCount;
			}

			Point size = child.computeSize(data.size.width, data.size.height, flushCache);
			
			int width = size.x + data.margin.getWidth();
			int height = size.y + data.margin.getHeight();
			
			if (isHorizontal) {
				maxChild = Math.max(maxChild, height);
				minStrip += width;
			} else {
				maxChild = Math.max(maxChild, width);
				minStrip += height;
			}
			
			sizes[i] = size;
		}
				
		int emptySpace = (isHorizontal ? clientArea.width : clientArea.height) - minStrip;
		int childFill = fillChildCount == 0 || emptySpace < fillChildCount ? 0 : emptySpace / fillChildCount;
		
		int x = clientArea.x, y = clientArea.y;
		for (int i = 0; i < count; ++i) {
			
			Control child = children[i];
			StripData data = (StripData)child.getLayoutData();
			Point size = sizes[i];
			
			int childWidth, childHeight, childX, childY;
			if (isHorizontal) {
				childWidth = data.fillWidth ? size.x + childFill : size.x;
				childHeight = data.fillHeight ? (isLayout ? clientArea.height - data.margin.getHeight() : maxChild) : size.y;
				childX = data.margin.left + x;
				childY = data.margin.top + (data.fillHeight ? y : y + (maxChild - size.y) / 2);
			} else {
				childHeight = data.fillHeight ? size.y + childFill : size.y;
				childWidth = data.fillWidth ? (isLayout ? clientArea.width - data.margin.getWidth() : maxChild) : size.x;
				childX = data.margin.left + (data.fillWidth ? x : x + (maxChild - size.x) / 2);
				childY = data.margin.top + y;
			}
			
			if (isLayout) {
				child.setBounds(childX, childY, childWidth, childHeight);
			}
			
			if (isHorizontal) {
				x += childWidth + data.margin.getWidth();
			} else {
				y += childHeight + data.margin.getHeight();
			}
		}
		
		x += maxChild;
		y += maxChild;
		
		return new Point(x, y);
	}
	
	/**
	 * Gets the cropped client area.
	 *
	 * @param composite the composite to investigate
	 * @param isLayout specifies whether the layout should be applied or just sized.
	 * @return the cropped client area
	 */
	private Rectangle getCroppedClientArea(Composite composite, boolean isLayout) {
		if (isLayout) {
			//Rectangle clientArea = composite.getClientArea();
			//return new Rectangle(clientArea.x + margin.left, clientArea.y + margin.top, clientArea.width - (margin.left + margin.right), clientArea.height - (margin.top + margin.bottom));
			return composite.getClientArea();
		} else {
			return new Rectangle(0, 0, 0, 0);
		}
	}
}
