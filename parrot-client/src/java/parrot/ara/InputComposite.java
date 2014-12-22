package parrot.ara;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;

public class InputComposite extends Composite {

	private boolean recursiveLock = false;

	private float maxHeightPart = 0.4f;
	private Text textInputWidget;

	public InputComposite(Composite arg0, int arg1) {
		super(arg0, arg1);
		setLayout(new FillLayout(SWT.HORIZONTAL));
		textInputWidget = new Text(this, SWT.V_SCROLL | SWT.WRAP | SWT.MULTI | SWT.DOUBLE_BUFFERED);
		
		/*Listener scrollBarListener = new Listener () {
			private boolean recursiveLock = false;
			private float maxHeightPart = 0.4f;
			
		    @Override
		    public void handleEvent(Event event) {
		    	if (!recursiveLock) {
		    		int maxHeight = (int)(getShell().getClientArea().height * maxHeightPart);
			        Text t = textInputWidget;
			        // use r1.x as wHint instead of SWT.DEFAULT
			        char[] chars = t.getTextChars();
			        int caretPos = t.getCaretPosition(); 
			        boolean spaceWorkaround = false;
			        if (chars.length == 0 || chars[chars.length - 1] == 10) {
			        	recursiveLock = true;
			        	t.append(" ");
			        	recursiveLock = false;
			        	spaceWorkaround = true;
			        }
			        
			        Rectangle clientArea = t.getClientArea();
			        //Rectangle trim = t.t(clientArea.x, clientArea.y, clientArea.width, clientArea.height);
			        Point size = t.computeSize(clientArea.x, SWT.DEFAULT, false);
			        if (size.y > maxHeight) {
			        	size = computeSize(clientArea.x, maxHeight, true);
			        	t.getVerticalBar().setVisible(true);
			        } else {
			        	size = computeSize(clientArea.x, SWT.DEFAULT, true);
			        	getShell().layout(true);
			        	t.showSelection();
			        	t.getVerticalBar().setVisible(false);
			        }
			        
			        if (spaceWorkaround) {
			        	recursiveLock = true;
			        	t.setTextChars(chars);
			        	t.setSelection(caretPos);
			        	recursiveLock = false;
			        }
			        

		    	}
		    }
		};*/
		
		//addListener(SWT.Resize, scrollBarListener);
		//textInputWidget.addListener(SWT.Modify, scrollBarListener);
		
		
		textInputWidget.addListener(SWT.Modify, new Listener() {
			
			@Override
			public void handleEvent(Event arg0) {
				if (!recursiveLock) {
					getShell().layout();
				}
			}
		});
	}
	
	@Override
	public Point computeSize(int wHint, int hHint, boolean changed) {
			int maxHeight = (int)(getShell().getClientArea().height * maxHeightPart);
	        
			char[] chars = textInputWidget.getTextChars();
	        int caretPos = textInputWidget.getCaretPosition(); 
	        boolean spaceWorkaround = false;
	        if (chars.length == 0 || chars[chars.length - 1] == 10) {
	        	recursiveLock = true;
	        	textInputWidget.append(" ");
	        	recursiveLock = false;
	        	spaceWorkaround = true;
	        }
		
			Point inputWidgetSize = textInputWidget.computeSize(wHint, SWT.DEFAULT, false);
	        if (inputWidgetSize.y > maxHeight) {
	        	inputWidgetSize = textInputWidget.computeSize(wHint, maxHeight, true);
	        	textInputWidget.getVerticalBar().setVisible(true);
	        } else {
	        	inputWidgetSize = textInputWidget.computeSize(wHint, SWT.DEFAULT, true);
	        	textInputWidget.getVerticalBar().setVisible(false);
	        }
	        
	        if (spaceWorkaround) {
	        	recursiveLock = true;
	        	textInputWidget.setTextChars(chars);
	        	textInputWidget.setSelection(caretPos);
	        	recursiveLock = false;
	        }
        	textInputWidget.showSelection();
	
	        inputWidgetSize.y += textInputWidget.getLineHeight();
	
	        return inputWidgetSize;

	}
	
	public String getText() {
		return textInputWidget.getText();
	}
	
	public void setText(String text) {
		this.textInputWidget.setText(text);
	}
}
