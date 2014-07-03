package dropfile;

import java.util.HashSet;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetAdapter;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.wb.swt.SWTResourceManager;

import zetes.wings.base.ViewWindowBase;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Text;


public class SessionWindow extends ViewWindowBase<Session>
{
	private HashSet<DropTargetAdapter> dropTargetAdapters = new HashSet<DropTargetAdapter>();
	private DropTarget dropTarget;
	private Button btnUpload;
	private Text remoteAddressText;
	private Label lblRemoteMachine;
	private Label lblLocalMachine;
	private Text localAddressText;

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
		Shell shell = new Shell(SWT.TITLE | SWT.CLOSE | SWT.MIN | SWT.BORDER | SWT.DOUBLE_BUFFERED);
		shell.setSize(370, 117);

		shell.setBackground(SWTResourceManager.getColor(SWT.COLOR_WIDGET_BACKGROUND));
		shell.setMinimumSize(new Point(150, 200));

		shell.setImages(new Image[] { 
				SWTResourceManager.getImage(SessionWindow.class, "/tinyviewer/wingphotos16.png"),		// Necessary in Windows (for taskbar)
				SWTResourceManager.getImage(SessionWindow.class, "/tinyviewer/wingphotos64.png"),		// Necessary in Windows (for Alt-Tab)
				SWTResourceManager.getImage(SessionWindow.class, "/tinyviewer/wingphotos512.png")		// Necessary in OS X
		});
		shell.setLayout(new GridLayout(3, false));
		
		lblRemoteMachine = new Label(shell, SWT.NONE);
		lblRemoteMachine.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblRemoteMachine.setText("Remote machine:");
		
		remoteAddressText = new Text(shell, SWT.BORDER);
		remoteAddressText.setText("127.0.0.1");
		remoteAddressText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		btnUpload = new Button(shell, SWT.NONE);
		btnUpload.setText("Connect");
		
		lblLocalMachine = new Label(shell, SWT.NONE);
		lblLocalMachine.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblLocalMachine.setText("Local machine:");
		
		localAddressText = new Text(shell, SWT.BORDER);
		localAddressText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		new Label(shell, SWT.NONE);
		
		// Drop targets
		dropTarget = new DropTarget(shell, DND.DROP_MOVE);
		dropTarget.setTransfer(new Transfer[] { FileTransfer.getInstance() });
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
}
