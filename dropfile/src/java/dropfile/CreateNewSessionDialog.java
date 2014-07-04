package dropfile;

import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.events.ShellListener;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

import dropfile.protocol.Connection;

public class CreateNewSessionDialog extends Dialog {

	private enum State {
		Input, Connecting, Connected
	}

	protected Object result;
	protected Shell shlCreateNewSession;
	private Text textAddress;
	private Button btnConnect, btnCancel;
	
	private State state = null;
	
	protected boolean addressIsValid() {
		return textAddress.getText().length() > 0;
	}

	protected void setControlsEnabled() {
		btnConnect.setEnabled(addressIsValid() && state == State.Input);
		textAddress.setEnabled(state == State.Input);
	}
	
	protected void setState(State state) {
		if (this.state != state) {
			this.state = state;
			
			setControlsEnabled();
			if (state == State.Input) {
				textAddress.setFocus();
				textAddress.selectAll();
			} else if (state == State.Connecting) {
				startConnectionThread();
			} else if (state == State.Connected) {
				shlCreateNewSession.close();
			}
		}
	}
	
	private void connectionThreadReport(Connection connection) {
		if (connection.getSuccess()) {
			setState(State.Connected);
		} else {
			shlCreateNewSession.setText("fail!");
			setState(State.Input);
		}
		
	}
	
	private void startConnectionThread() {
		final String address = textAddress.getText();
		
		Thread connectionThread = new Thread(new Runnable() {
			
			@Override
			public void run() {
				final Connection connection = new Connection(address);
				Display.getDefault().asyncExec(new Runnable() {
					
					@Override
					public void run() {
						connectionThreadReport(connection);
					}
				});
			}
		});
		
		connectionThread.start();
	}
	
	/**
	 * Create the dialog.
	 * @param parent
	 * @param style
	 */
	public CreateNewSessionDialog(Shell parent, int style) {
		super(parent, style);
		setText("Create a new session");
	}

	/**
	 * Open the dialog.
	 * @return the result
	 */
	public Object open() {
		createContents();
		shlCreateNewSession.open();
		shlCreateNewSession.layout();
		Display display = getParent().getDisplay();
		while (!shlCreateNewSession.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		return result;
	}

	/**
	 * Create contents of the dialog.
	 */
	private void createContents() {
		shlCreateNewSession = new Shell(getParent(), SWT.DIALOG_TRIM);

		shlCreateNewSession.setSize(295, 132);
		shlCreateNewSession.setText("Create a new session");
		shlCreateNewSession.setLayout(new GridLayout(3, false));
		
		Composite composite = new Composite(shlCreateNewSession, SWT.NONE);
		composite.setLayout(new GridLayout(2, false));
		composite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true, 3, 1));
		
		Label label_1 = new Label(composite, SWT.WRAP);
		GridData gd_label_1 = new GridData(SWT.LEFT, SWT.CENTER, true, false, 2, 1);
		gd_label_1.heightHint = 28;
		label_1.setLayoutData(gd_label_1);
		label_1.setText("Select a remote machine address to connect to it");
		
		Label label = new Label(composite, SWT.NONE);
		label.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		label.setText("Remote address:");
		label.setBounds(0, 0, 87, 15);
		
		textAddress = new Text(composite, SWT.BORDER);
		GridData gd_textAddress = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gd_textAddress.horizontalIndent = 10;
		gd_textAddress.widthHint = 1;
		textAddress.setLayoutData(gd_textAddress);
		textAddress.addModifyListener(new ModifyListener() {
			
			@Override
			public void modifyText(ModifyEvent arg0) {
				setControlsEnabled();
			}
		});
		
		Label lblNewLabel = new Label(shlCreateNewSession, SWT.NONE);
		GridData gd_lblNewLabel = new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1);
		gd_lblNewLabel.widthHint = 101;
		lblNewLabel.setLayoutData(gd_lblNewLabel);
		
		btnConnect = new Button(shlCreateNewSession, SWT.NONE);
		btnConnect.setSelection(true);
		GridData gd_btnConnect = new GridData(SWT.FILL, SWT.BOTTOM, false, false, 1, 1);
		gd_btnConnect.widthHint = 80;
		gd_btnConnect.minimumWidth = 80;
		btnConnect.setLayoutData(gd_btnConnect);
		btnConnect.setText("Connect");
		btnConnect.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				connect();
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {
				connect();
			}
		});
		
		btnCancel = new Button(shlCreateNewSession, SWT.NONE);
		GridData gd_btnCancel = new GridData(SWT.FILL, SWT.BOTTOM, false, false, 1, 1);
		gd_btnCancel.widthHint = 80;
		gd_btnCancel.minimumWidth = 80;
		btnCancel.setLayoutData(gd_btnCancel);
		btnCancel.setText("Cancel");
		btnCancel.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				shlCreateNewSession.close();
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {}
		});

		shlCreateNewSession.setDefaultButton(btnConnect);
		shlCreateNewSession.addShellListener(new ShellListener() {
			
			@Override public void shellIconified(ShellEvent arg0) {}
			@Override public void shellDeiconified(ShellEvent arg0) {}
			@Override public void shellDeactivated(ShellEvent arg0) {}
			@Override public void shellActivated(ShellEvent arg0) {}
			
			@Override
			public void shellClosed(ShellEvent arg0) {
				arg0.doit = cancel();
			}
			
		});
		
		// Positioning in the screen center
		Rectangle screenSize = getParent().getDisplay().getPrimaryMonitor().getBounds();
		shlCreateNewSession.setLocation((screenSize.width - shlCreateNewSession.getBounds().width) / 2, (screenSize.height - shlCreateNewSession.getBounds().height) / 2);

		setState(State.Input);
	}

	protected void connect() {
		setState(State.Connecting);
	}
	
	protected boolean cancel() {
		if (state == State.Input) {
			return true;
		} else if (state == State.Connecting) {
			setState(State.Input);
			return false;
		} else if (state == State.Connected) {
			return true;
		} else {
			throw new RuntimeException("Invalid state");
		}
	}
}
