package parrot.ara;

import java.util.Date;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.PaintObjectEvent;
import org.eclipse.swt.custom.PaintObjectListener;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wb.swt.SWTResourceManager;

import parrot.client.APIClient;
import parrot.client.ClientConnectionProblemException;
import parrot.client.Session;
import parrot.client.data.objects.Message;
import zetes.wings.base.ViewWindowBase;

import org.eclipse.swt.widgets.Label;
import org.mihalis.opal.flatButton.FlatButton;
import org.mihalis.opal.obutton.OButton;
import org.eclipse.swt.widgets.Composite;

public class AraViewWindow extends ViewWindowBase<AraDocument> {
	private APIClient apiClient;
	private Session session;
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
					styledText.append("["
							+ new Date(newMessages[i].getTimeMillis()) + "] "
							+ newMessages[i].getText() + "\n");
				}
				text.setText("");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	};
	private Text text;

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
	protected Shell constructShell() {
		final Display display = Display.getCurrent();
		Color backColor = display.getSystemColor(
				SWT.COLOR_LIST_BACKGROUND);

		final Shell shell = new Shell(SWT.TITLE | SWT.CLOSE | SWT.MIN | SWT.MAX
				| SWT.RESIZE | SWT.BORDER | SWT.DOUBLE_BUFFERED);
		shell.setSize(401, 404);

		shell.setMinimumSize(new Point(250, 200));

		shell.setImages(new Image[] {
				SWTResourceManager.getImage(AraViewWindow.class,
						"/flyer/flyer512.png"), // Necessary in OS X
				SWTResourceManager.getImage(AraViewWindow.class,
						"/flyer/flyer64.png"), // Necessary in Windows (for
												// Alt-Tab)
				SWTResourceManager.getImage(AraViewWindow.class,
						"/flyer/flyer16.png") // Necessary in Windows (for
												// taskbar)
		});
		shell.setBackground(backColor);
		GridLayout gl_shell = new GridLayout(1, false);
		gl_shell.verticalSpacing = 0;
		gl_shell.marginWidth = 0;
		gl_shell.marginHeight = 0;
		shell.setLayout(gl_shell);

		styledText = new StyledText(shell, SWT.READ_ONLY | SWT.WRAP
				| SWT.V_SCROLL | SWT.NO_BACKGROUND);
		GridData gd_styledText = new GridData(SWT.FILL, SWT.FILL, true, true,
				1, 1);
		gd_styledText.heightHint = 280;
		styledText.setLayoutData(gd_styledText);
		styledText.setEditable(false);
		styledText.setBackground(backColor);
		styledText.setBottomMargin(5);
		styledText.setRightMargin(5);
		styledText.setLeftMargin(5);
		styledText.setTopMargin(5);

		styledText.setForeground(display.getSystemColor(SWT.COLOR_BLUE));
		styledText.addListener(SWT.Resize, new Listener() {
			private Image oldImage = null;
			public void handleEvent(Event event) {
				Rectangle rect = styledText.getClientArea();
				Image newImage = new Image(display, 1, Math.max(1, rect.height));
				GC gc = new GC(newImage);
				gc.setForeground(display.getSystemColor(SWT.COLOR_WHITE));
				gc.setBackground(display.getSystemColor(SWT.COLOR_YELLOW));
				gc.fillGradientRectangle(rect.x, rect.y, 1, rect.height, true);
				gc.dispose();
				styledText.setBackgroundImage(newImage);
				if (oldImage != null)
					oldImage.dispose();
				oldImage = newImage;
			}
		});
		styledText.addListener(SWT.Paint, new Listener() {
		      public void handleEvent(Event event) {
					Rectangle rect = styledText.getClientArea();
					GC gc = event.gc;
					gc.setForeground(display.getSystemColor(SWT.COLOR_RED));
					gc.setBackground(display.getSystemColor(SWT.COLOR_YELLOW));
					gc.fillGradientRectangle(rect.x, rect.y, 1, rect.height, true);
					gc.dispose();
		      }
		});

		final Composite composite = new Composite(shell, SWT.NO_BACKGROUND);
		GridLayout gl_composite = new GridLayout(2, false);
		gl_composite.marginBottom = 5;
		gl_composite.marginLeft = 5;
		gl_composite.marginTop = 5;
		gl_composite.marginWidth = 0;
		gl_composite.marginRight = 5;
		gl_composite.marginHeight = 0;
		composite.setLayout(gl_composite);
		composite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false,
				false, 1, 1));
		composite.addListener(SWT.Resize, new Listener() {
			private Image oldImage = null;
			public void handleEvent(Event event) {
				Rectangle rect = composite.getClientArea();
				Image newImage = new Image(display, 1, Math.max(1, rect.height));
				GC gc = new GC(newImage);
				gc.setForeground(display.getSystemColor(SWT.COLOR_WHITE));
				gc.setBackground(display.getSystemColor(SWT.COLOR_YELLOW));
				gc.fillGradientRectangle(rect.x, rect.y, 1, rect.height, true);
				gc.dispose();
				composite.setBackgroundImage(newImage);
				if (oldImage != null)
					oldImage.dispose();
				oldImage = newImage;
			}
		});

		text = new Text(composite, SWT.BORDER | SWT.WRAP | SWT.V_SCROLL | SWT.MULTI);
		text.setBackground(SWTResourceManager.getColor(255, 255, 255));
		text.addListener(SWT.Resize, new Listener() {
			private Image oldImage = null;
			public void handleEvent(Event event) {
				Rectangle rect = text.getClientArea();
				Image newImage = new Image(display, 1, Math.max(1, rect.height));
				GC gc = new GC(newImage);
				gc.setForeground(display.getSystemColor(SWT.COLOR_WHITE));
				gc.setBackground(display.getSystemColor(SWT.COLOR_GREEN));
				gc.fillGradientRectangle(rect.x, rect.y, 1, rect.height, true);
				gc.dispose();
				text.setBackgroundImage(newImage);
				if (oldImage != null)
					oldImage.dispose();
				oldImage = newImage;
			}
		});
		text.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 2));

		OButton sendButton = new OButton(composite, SWT.NONE);
		GridData gd_button = new GridData(SWT.CENTER, SWT.CENTER, false, false,
				1, 1);
		gd_button.widthHint = 109;
		sendButton.setLayoutData(gd_button);
		sendButton.setText(" ");
		GridLayout gl_button = new GridLayout(2, false);
		gl_button.marginHeight = 1;
		gl_button.horizontalSpacing = 0;
		sendButton.setLayout(gl_button);

		Label label_1 = new Label(sendButton, SWT.NONE);
		label_1.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false,
				1, 1));
		label_1.setText("Send");
		label_1.setForeground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		label_1.setFont(SWTResourceManager.getFont("Lucida Grande", 11,
				SWT.BOLD));
		label_1.setEnabled(false);

		Label label_2 = new Label(sendButton, SWT.NONE);
		label_2.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true, 1,
				1));
		label_2.setText("(Ctrl+Enter)");
		label_2.setForeground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		label_2.setFont(SWTResourceManager.getFont("Lucida Grande", 9,
				SWT.NORMAL));
		label_2.setEnabled(false);
		sendButton.addSelectionListener(sendSelectionAdapter);

		Button updateButton = new Button(composite, SWT.NONE);
		updateButton.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false,
				false, 1, 1));
		updateButton.addSelectionListener(updateSelectionAdapter);
		updateButton.setText("Update");

		return shell;
	}

	@Override
	public void setDocument(AraDocument document) {
		super.setDocument(document);
		getShell().forceActive();
	}

	@Override
	public boolean supportsFullscreen() {
		return true;
	}

	@Override
	public boolean supportsMaximizing() {
		return true;
	}
}
