package parrot.ara;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wb.swt.SWTResourceManager;
import org.mihalis.opal.obutton.OButton;

import parrot.client.APIClient;
import parrot.client.ClientConnectionProblemException;
import parrot.client.Session;
import parrot.client.data.objects.Message;
import zetes.wings.base.ViewWindowBase;

public class AraViewWindow extends ViewWindowBase<AraDocument> {
	private APIClient apiClient;
	private Session session;

	private Text text;
	private ScrolledComposite messagesScrolledComposite;
	
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
					MessageView newMessageView = new MessageView(messagesListComposite, SWT.NONE);
					newMessageView.setLayoutData(new RowData(SWT.DEFAULT, SWT.DEFAULT));
					newMessageView.setMessage(newMessages[i]);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	};
	private Composite messagesListComposite;

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
		shell.setSize(401, 358);

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
		gl_shell.horizontalSpacing = 0;
		shell.setLayout(gl_shell);
		
		messagesScrolledComposite = new ScrolledComposite(shell, SWT.V_SCROLL);
		messagesScrolledComposite.setExpandHorizontal(true);
		GridData gd_messagesScrolledComposite = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		gd_messagesScrolledComposite.heightHint = 205;
		messagesScrolledComposite.setLayoutData(gd_messagesScrolledComposite);
		messagesScrolledComposite.setExpandVertical(true);
		
		messagesListComposite = new Composite(messagesScrolledComposite, SWT.NONE);
		RowLayout rl_messagesListComposite = new RowLayout(SWT.VERTICAL);
		rl_messagesListComposite.wrap = false;
		rl_messagesListComposite.pack = false;
		rl_messagesListComposite.center = true;
		messagesListComposite.setLayout(rl_messagesListComposite);
		messagesScrolledComposite.setContent(messagesListComposite);
		messagesScrolledComposite.setMinSize(messagesListComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT));

		final Composite composite = new Composite(shell, SWT.NONE);
		composite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		GridLayout gl_composite = new GridLayout(2, false);
		gl_composite.marginBottom = 5;
		gl_composite.marginLeft = 5;
		gl_composite.marginTop = 5;
		gl_composite.marginWidth = 0;
		gl_composite.marginRight = 5;
		gl_composite.marginHeight = 0;
		composite.setLayout(gl_composite);
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
		
		Listener scrollBarListener = new Listener () {
			private boolean recursiveLock = false;
			private float maxHeightPart = 0.4f;
			
		    @Override
		    public void handleEvent(Event event) {
		    	if (!recursiveLock) {
		    		int maxHeight = (int)(shell.getClientArea().height * maxHeightPart);
			        Text t = (Text)event.widget;
			        // use r1.x as wHint instead of SWT.DEFAULT
			        char[] chars = t.getTextChars();
			        int caretPos = t.getCaretPosition(); 
			        boolean spaceWorkaround = false;
			        if (chars.length == 0 || chars[chars.length - 1] == 10 /* return */) {
			        	recursiveLock = true;
			        	t.append(" ");
			        	recursiveLock = false;
			        	spaceWorkaround = true;
			        }
			        Rectangle r1 = t.getClientArea();
			        Rectangle r2 = t.computeTrim(r1.x, r1.y, r1.width, r1.height);
			        Point p = t.computeSize(r1.x, SWT.DEFAULT, false);
			        getShell().setText("x: " + r2.x + ", y: " + r2.y);
			        if (p.y > maxHeight) {
			        	p = t.computeSize(r1.x, maxHeight, true);
			        	t.getVerticalBar().setVisible(r2.height <= p.y);
			        } else {
			        	p = t.computeSize(r1.x, SWT.DEFAULT, true);
			        	t.getShell().layout(true);
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
		};
		text.addListener(SWT.Resize, scrollBarListener);
		text.addListener(SWT.Modify, scrollBarListener);

		OButton sendButton = new OButton(composite, SWT.NONE);
		GridData gd_button = new GridData(SWT.CENTER, SWT.CENTER, false, false,
				1, 1);
		gd_button.widthHint = 109;
		sendButton.setLayoutData(gd_button);
		sendButton.setText("Send");
		GridLayout gl_button = new GridLayout(1, false);
		gl_button.marginHeight = 1;
		gl_button.horizontalSpacing = 0;
		sendButton.setLayout(gl_button);
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
