package parrot.ara;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Layout;

public class MessagesListLayout extends Layout {

	@Override
	protected Point computeSize(Composite composite, int widthHint, int heightHint, boolean flushCache) {
		int width = widthHint, height = heightHint;

		if (heightHint == SWT.DEFAULT) {
			height = 0;
		}
		for (int i = 0; i < composite.getChildren().length; i++) {
			Control child = composite.getChildren()[i];
			Point childSize = child.computeSize(widthHint, SWT.DEFAULT, flushCache);
			if (widthHint == SWT.DEFAULT && width < childSize.x) {
				width = childSize.x;
			}
			if (heightHint == SWT.DEFAULT) {
				height += childSize.y;
			}
		}
		
		return new Point(width, height);
	}

	@Override
	protected void layout(Composite composite, boolean flushCache) {
		int width = composite.getClientArea().width, y = 0;
		for (int i = 0; i < composite.getChildren().length; i++) {
			Control child = composite.getChildren()[i];
			Point childSize = child.computeSize(width, SWT.DEFAULT, flushCache);
			child.setBounds(0, y, width, childSize.y);
			y += childSize.y;
		}		
	}

}
