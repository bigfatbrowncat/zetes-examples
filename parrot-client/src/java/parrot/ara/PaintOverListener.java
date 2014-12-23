package parrot.ara;

import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

public abstract class PaintOverListener implements Listener {

	private final Composite root;
	
	public PaintOverListener(Composite root) {
		this.root = root;
	}
	
	@Override
	public void handleEvent(Event event) {
		Rectangle msgListRect = root.getBounds();
		
		Control parent = ((Control)event.widget);
		Rectangle thisRect = ((Control)event.widget).getBounds();
		while (parent != root) {
			parent = parent.getParent();
			thisRect.x += parent.getBounds().x;
			thisRect.y += parent.getBounds().y;
		}
		
		Rectangle rect = new Rectangle(
				msgListRect.x - thisRect.x, 
				msgListRect.y - thisRect.y, 
				msgListRect.width, 
				msgListRect.height);
		
		handleEvent(event, rect);
	}

	public abstract void handleEvent(Event event, Rectangle thisRect);
}
