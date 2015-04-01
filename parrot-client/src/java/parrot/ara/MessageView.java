package parrot.ara;

import java.util.Date;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import parrot.client.data.objects.Message;

import org.eclipse.wb.swt.SWTResourceManager;

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

		// User Name font
		FontData userNameFontData = getDisplay().getSystemFont().getFontData()[0];
		userNameFontData.setHeight(userNameFontData.getHeight() + 2);
		userNameFontData.setStyle(SWT.BOLD);
		Font userNameFont = new Font(getDisplay(), userNameFontData);
		
		// Date time font
		FontData dateTimeFontData = getDisplay().getSystemFont().getFontData()[0];
		dateTimeFontData.setHeight((int)(dateTimeFontData.getHeight() * 0.5));

		dateTimeFontData.setStyle(SWT.ITALIC);
		Font dateTimeFont = new Font(getDisplay(), userNameFontData);
		
		userNameLabel = new Label(this, SWT.DOUBLE_BUFFERED);
		userNameLabel.setFont(userNameFont);
		userNameLabel.setBackground(base);
		userNameLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		userNameLabel.setText("User Name");
		
		dateTimeLabel = new Label(this, SWT.DOUBLE_BUFFERED);
		userNameLabel.setFont(dateTimeFont);
		dateTimeLabel.setBackground(base);
		dateTimeLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		dateTimeLabel.setAlignment(SWT.RIGHT);
		dateTimeLabel.setText("00.00.00 12:34");
		
		styledText = new StyledText(this, SWT.DOUBLE_BUFFERED);
		styledText.setWordWrap(true);
		styledText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
		
	}
	
	void setMessage(Message message) {
		userNameLabel.setText(((Long)(message.getUserId())).toString());
		dateTimeLabel.setText(new Date(message.getTimeMillis()).toString());
		styledText.setText(message.getText());
	}
	
	

}
