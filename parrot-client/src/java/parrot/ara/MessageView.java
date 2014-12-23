package parrot.ara;

import java.util.Date;
import java.util.List;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.SWT;
import org.eclipse.wb.swt.SWTResourceManager;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Rectangle;

import parrot.client.data.objects.Message;
import org.eclipse.swt.custom.StyledText;

public class MessageView extends Composite {
	private Label userNameLabel;
	private Label dateTimeLabel;
	private StyledText styledText;

	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public MessageView(Composite parent, List<PaintOverListener> paintOverListeners, int style) {
		super(parent, style);
		setBackground(SWTResourceManager.getColor(SWT.COLOR_LIST_BACKGROUND));
		setLayout(new GridLayout(2, false));
		
		Color base = getDisplay().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND);
		final Color light = new Color(getDisplay(), 
		        (int)Math.min(base.getRed() * 1.1, 255),
		        (int)Math.min(base.getGreen() * 1.1, 255),
		        (int)Math.min(base.getBlue() * 1.1, 255));
				
		final Color dark = new Color(getDisplay(), 
		        (int)(base.getRed() * 0.9),
		        (int)(base.getGreen() * 0.9),
		        (int)(base.getBlue() * 0.9));
		
		PaintOverListener lightDarkGradientListener = new PaintOverListener(this) {

				@Override
				public void handleEvent(Event event, Rectangle thisRect) {
					GC gc = event.gc;
					gc.setAdvanced(true);

					gc.setAlpha(24);

					gc.setBackground(getDisplay().getSystemColor(SWT.COLOR_BLACK));
					gc.fillRectangle(thisRect.x, thisRect.y, thisRect.width, thisRect.height);
				}

		};
		paintOverListeners.add(0, lightDarkGradientListener);
		
		userNameLabel = new Label(this, SWT.DOUBLE_BUFFERED);
		userNameLabel.setBackground(SWTResourceManager.getColor(SWT.COLOR_LIST_BACKGROUND));
		userNameLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		userNameLabel.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.BOLD));
		userNameLabel.setText("User Name");
		for (PaintOverListener pol : paintOverListeners) {
			userNameLabel.addListener(SWT.Paint, pol);
		}
		
		dateTimeLabel = new Label(this, SWT.DOUBLE_BUFFERED);
		dateTimeLabel.setBackground(SWTResourceManager.getColor(SWT.COLOR_LIST_BACKGROUND));
		dateTimeLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		dateTimeLabel.setAlignment(SWT.RIGHT);
		dateTimeLabel.setText("00.00.00 12:34");
		for (PaintOverListener pol : paintOverListeners) {
			dateTimeLabel.addListener(SWT.Paint, pol);
		}
		
		styledText = new StyledText(this, SWT.DOUBLE_BUFFERED);
		styledText.setBackground(SWTResourceManager.getColor(SWT.COLOR_LIST_BACKGROUND));
		styledText.setEditable(false);
		styledText.setWordWrap(true);
		styledText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
		for (PaintOverListener pol : paintOverListeners) {
			styledText.addListener(SWT.Paint, pol);
		}
		
		for (PaintOverListener pol : paintOverListeners) {
			this.addListener(SWT.Paint, pol);
		}
		
		addListener(SWT.Dispose, new Listener() {
			
			@Override
			public void handleEvent(Event arg0) {
				light.dispose();
				dark.dispose();
			}
		});
	}
	
	void setMessage(Message message) {
		userNameLabel.setText(((Long)(message.getUserId())).toString());
		dateTimeLabel.setText(new Date(message.getTimeMillis()).toString());
		styledText.setText(message.getText());
	}

}
