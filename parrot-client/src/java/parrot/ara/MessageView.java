package parrot.ara;

import java.util.Date;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import parrot.client.data.objects.Message;

public class MessageView extends Composite {
	private Label userNameLabel;
	private Label dateTimeLabel;
	private StyledText styledText;

	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public MessageView(Composite parent, int style) {
		super(parent, style);
		Color base = getDisplay().getSystemColor(SWT.COLOR_LIST_BACKGROUND);
		setBackground(base);
		setLayout(new GridLayout(2, false));
		
		
		userNameLabel = new Label(this, SWT.DOUBLE_BUFFERED);
		userNameLabel.setBackground(base);
		userNameLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
//		userNameLabel.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.BOLD));
		userNameLabel.setText("User Name");
		
		dateTimeLabel = new Label(this, SWT.DOUBLE_BUFFERED);
		dateTimeLabel.setBackground(base);
		dateTimeLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		dateTimeLabel.setAlignment(SWT.RIGHT);
		dateTimeLabel.setText("00.00.00 12:34");
		
		styledText = new StyledText(this, SWT.DOUBLE_BUFFERED);
		styledText.setWordWrap(true);
		styledText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));

		this.addControlListener(new ControlListener() {
			
			@Override
			public void controlResized(ControlEvent arg0) {
				int width = getSize().x;
				Point computed = MessageView.this.computeSize(width, SWT.DEFAULT);
				System.out.println(computed.x + ", " + computed.y);
				
			}
			
			@Override
			public void controlMoved(ControlEvent arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		
	}
	
	void setMessage(Message message) {
		userNameLabel.setText(((Long)(message.getUserId())).toString());
		dateTimeLabel.setText(new Date(message.getTimeMillis()).toString());
		styledText.setText(message.getText());
	}
	
	

}
