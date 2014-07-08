package dropfile;

import java.util.HashSet;

import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetAdapter;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.wb.swt.SWTResourceManager;

import zetes.wings.actions.Handler;
import zetes.wings.base.ViewWindowBase;


public class SessionWindow extends ViewWindowBase<Session>
{
	private HashSet<DropTargetAdapter> dropTargetAdapters = new HashSet<DropTargetAdapter>();
	private DropTarget dropTarget;
	private Label lblRemoteMachine;
	private Label lblLocalMachine;
	private Label lblRemoteAddress;
	private Label lblLocalAddress;
	private Composite composite;
	private Composite composite_1;
	private Composite composite_2;
	
	private Handler<SessionWindow> sendFileHandler;
	
	public SessionWindow() {
		sendFileHandler = new Handler<SessionWindow>() {
			
			@Override
			public boolean isEnabled() {
				return getDocument() != null;
			}
			
			@Override
			public void execute(SessionWindow window) {
				getDocument().sendFile("test/testfile.txt");
			}
		};
	}
	
	public void addDropTargetListener(DropTargetAdapter dropTargetAdapter)
	{
		dropTargetAdapters.add(dropTargetAdapter);
		if (dropTarget != null && !dropTarget.isDisposed())
		{
			dropTarget.addDropListener(dropTargetAdapter);
		}
	}
	
	public void removeDropTargetListener(DropTargetAdapter dropTargetAdapter)
	{
		dropTargetAdapters.remove(dropTargetAdapter);
		if (dropTarget != null && !dropTarget.isDisposed())
		{
			dropTarget.removeDropListener(dropTargetAdapter);
		}
	}
	
	/**
	 * Create contents of the window.
	 * 
	 * @wbp.parser.entryPoint
	 */
	@Override
	protected Shell constructShell()
	{
		Shell shell = new Shell(SWT.SHELL_TRIM | SWT.BORDER);
		shell.setMinimumSize(new Point(280, 160));
		shell.setSize(433, 160);

		shell.setBackground(SWTResourceManager.getColor(SWT.COLOR_WIDGET_BACKGROUND));

		shell.setImages(new Image[] { 
				SWTResourceManager.getImage(SessionWindow.class, "/tinyviewer/wingphotos16.png"),		// Necessary in Windows (for taskbar)
				SWTResourceManager.getImage(SessionWindow.class, "/tinyviewer/wingphotos64.png"),		// Necessary in Windows (for Alt-Tab)
				SWTResourceManager.getImage(SessionWindow.class, "/tinyviewer/wingphotos512.png")		// Necessary in OS X
		});
		shell.setLayout(new GridLayout(2, true));
		
		composite_1 = new Composite(shell, SWT.BORDER);
		composite_1.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		composite_1.setLayout(new GridLayout(1, false));
		
		lblLocalMachine = new Label(composite_1, SWT.NONE);
		lblLocalMachine.setFont(SWTResourceManager.getFont("Lucida Grande", 10, SWT.NORMAL));
		lblLocalMachine.setText("Local machine:");
		
		lblLocalAddress = new Label(composite_1, SWT.NONE);
		lblLocalAddress.setFont(SWTResourceManager.getFont("Lucida Grande", 12, SWT.NORMAL));
		lblLocalAddress.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		lblLocalAddress.setText("0.0.0.0");
		
		composite_2 = new Composite(shell, SWT.BORDER);
		composite_2.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		composite_2.setLayout(new GridLayout(1, false));
		
		lblRemoteMachine = new Label(composite_2, SWT.NONE);
		lblRemoteMachine.setFont(SWTResourceManager.getFont("Lucida Grande", 10, SWT.NORMAL));
		lblRemoteMachine.setText("Remote machine:");
		
		lblRemoteAddress = new Label(composite_2, SWT.NONE);
		lblRemoteAddress.setFont(SWTResourceManager.getFont("Lucida Grande", 12, SWT.NORMAL));
		GridData gd_lblRemoteAddress = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gd_lblRemoteAddress.widthHint = 92;
		lblRemoteAddress.setLayoutData(gd_lblRemoteAddress);
		lblRemoteAddress.setText("0.0.0.0");
		
		// Drop targets
		dropTarget = new DropTarget(shell, DND.DROP_MOVE);
		dropTarget.setTransfer(new Transfer[] { FileTransfer.getInstance() });
		
		composite = new Composite(shell, SWT.NONE);
		GridLayout gl_composite = new GridLayout(2, false);
		gl_composite.marginWidth = 0;
		composite.setLayout(gl_composite);
		composite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true, 2, 1));
		
		Label lblUploading = new Label(composite, SWT.NONE);
		lblUploading.setFont(SWTResourceManager.getFont("Lucida Grande", 10, SWT.NORMAL));
		lblUploading.setText("Uploading:");
		
		ProgressBar progressBar = new ProgressBar(composite, SWT.NONE);
		progressBar.setSelection(50);
		GridData gd_progressBar = new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1);
		gd_progressBar.heightHint = 20;
		progressBar.setLayoutData(gd_progressBar);
		progressBar.setBounds(0, 0, 140, 14);
		for (DropTargetAdapter adapter : dropTargetAdapters)
		{
			dropTarget.addDropListener(adapter);
		}
		
		return shell;
	}
	
	@Override
	public void setDocument(Session document)
	{
		super.setDocument(document);
		lblLocalAddress.setText(document.getConnection().getLocalAddress());
		lblRemoteAddress.setText(document.getConnection().getRemoteAddress());
		
		getShell().forceActive();
	}
	
	@Override
	public boolean supportsFullscreen()
	{
		return false;
	}

	@Override
	public boolean supportsMaximizing()
	{
		return false;
	}
	
	public Handler<SessionWindow> getSendFileHandler() {
		return sendFileHandler;
	}
}
