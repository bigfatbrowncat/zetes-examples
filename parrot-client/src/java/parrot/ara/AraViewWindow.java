package parrot.ara;

import java.util.Date;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wb.swt.SWTResourceManager;

import parrot.client.APIClient;
import parrot.client.ClientConnectionProblemException;
import parrot.client.Session;
import parrot.client.data.objects.Message;
import zetes.wings.base.ViewWindowBase;

import org.eclipse.swt.widgets.Label;

public class AraViewWindow extends ViewWindowBase<AraDocument>
{
	private APIClient apiClient;
	private Session session;
	
	private Text text;
	private StyledText styledText;
	
	private SelectionAdapter sendSelectionAdapter = new SelectionAdapter() {
		@Override
		public void widgetSelected(SelectionEvent arg0) {
			try {
				session.sendMessage(text.getText());
				text.setText("");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	};
	
	private SelectionAdapter updateSelectionAdapter = new SelectionAdapter() {
		@Override
		public void widgetSelected(SelectionEvent arg0) {
			try {
				Message[] newMessages = session.getLatestMessages();
				for (int i = 0; i < newMessages.length; i++) {
					styledText.append("[" + new Date(newMessages[i].getTimeMillis()) + "] " + newMessages[i].getText() + "\n");
				}
				text.setText("");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	};

	
	private void openSession() {
		try {
			session = new Session(apiClient, "il", "1234");
		} catch (ClientConnectionProblemException e) {
			e.printStackTrace();
		}
	}
	
	public AraViewWindow(APIClient apiClient) {
		this.apiClient = apiClient;
		openSession();
	}
	
	/**
	 * Create contents of the window.
	 * 
	 * @wbp.parser.entryPoint
	 */
	@Override
	protected Shell constructShell()
	{
		Shell shell = new Shell(SWT.TITLE | SWT.CLOSE | SWT.MIN | SWT.MAX | SWT.RESIZE | SWT.BORDER | SWT.DOUBLE_BUFFERED);
		shell.setSize(401, 361);

		shell.setMinimumSize(new Point(250, 200));

		shell.setImages(new Image[] { 
				SWTResourceManager.getImage(AraViewWindow.class, "/flyer/flyer512.png"),		// Necessary in OS X
				SWTResourceManager.getImage(AraViewWindow.class, "/flyer/flyer64.png"),		// Necessary in Windows (for Alt-Tab)
				SWTResourceManager.getImage(AraViewWindow.class, "/flyer/flyer16.png")		// Necessary in Windows (for taskbar)
		});
		shell.setLayout(new GridLayout(2, false));
		
		styledText = new StyledText(shell, SWT.BORDER);
		styledText.setEditable(false);
		styledText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
		
		text = new Text(shell, SWT.BORDER);
		GridData gd_text = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 2);
		gd_text.heightHint = 67;
		text.setLayoutData(gd_text);
		
		Button sendButton = new Button(shell, SWT.NONE);
		sendButton.addSelectionListener(sendSelectionAdapter);
		sendButton.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1));
		sendButton.setText("Send");
		
		Button btnNewButton = new Button(shell, SWT.NONE);
		btnNewButton.addSelectionListener(updateSelectionAdapter);
		btnNewButton.setText("Update");
	
			
		return shell;
	}

	@Override
	public void setDocument(AraDocument document)
	{
		super.setDocument(document);
		getShell().forceActive();
	}
	
	@Override
	public boolean supportsFullscreen()
	{
		return true;
	}

	@Override
	public boolean supportsMaximizing()
	{
		return true;
	}
}
