package parrot.ara;

import java.util.Date;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.SWT;
import org.eclipse.wb.swt.SWTResourceManager;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.custom.CLabel;

import parrot.client.data.objects.Message;

public class MessageView extends Composite {
	private Label userNameLabel;
	private Label dateTimeLabel;
	private CLabel textLabel;

	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public MessageView(Composite parent, Listener paintOverListener, int style) {
		super(parent, style);
		setLayout(new GridLayout(2, false));
		
		userNameLabel = new Label(this, SWT.NONE | SWT.NO_BACKGROUND);
		userNameLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		userNameLabel.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.BOLD));
		userNameLabel.setText("User Name");
		
		dateTimeLabel = new Label(this, SWT.NONE | SWT.NO_BACKGROUND);
		dateTimeLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		dateTimeLabel.setAlignment(SWT.RIGHT);
		dateTimeLabel.setText("00.00.00 12:34");
		
		textLabel = new CLabel(this, SWT.NONE);
		textLabel.addListener(SWT.Paint, paintOverListener);
		textLabel.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, true, 1, 1));
		textLabel.setText("New Label");
		//new Label(this, SWT.NONE);
	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}
	
	void setMessage(Message message) {
		userNameLabel.setText(((Long)(message.getUserId())).toString());
		dateTimeLabel.setText(new Date(message.getTimeMillis()).toString());
		textLabel.setText(message.getText());
	}

}
