package dropfile;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.events.ShellListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import dropfile.protocol.Client;
import dropfile.protocol.ClientConnection;

public class CreateNewSessionDialog extends Dialog {

	private enum State {
		Input, Connecting, Connected
	}
	
	private Client client;
	
	private class ConnectionTask {
		private volatile boolean cancelled = false;
		private String address;

		private Client.ConnectionListener connectionListener = new Client.ConnectionListener() {
			
			@Override
			public void onFailed(Client sender, final Exception e, int attempt) {
				if (!cancelled) {
					Display.getDefault().syncExec(new Runnable() {
						
						@Override
						public void run() {
							CreateNewSessionDialog.this.connectionFailThreadReport(e);
						}
					});
				}
			}
			
			@Override
			public void onEstablished(Client client, final ClientConnection connection) {
				if (!cancelled) {
					Display.getDefault().syncExec(new Runnable() {
						
						@Override
						public void run() {
							CreateNewSessionDialog.this.connectionSuccessThreadReport(connection);
						}
					});
				}
			}
		};
		
		public ConnectionTask(String address) {
			this.address = address;
		}
		
		public void setCancelled() { 
			this.cancelled = true; 
		
		}
		public void start() {
			client.connect(address, connectionListener);
		}
	}

	protected Object result;
	protected Shell shlCreateNewSession;
	private Text textAddress;
	private Button btnConnect, btnCancel;
	private ClientConnection connection;
	private volatile ConnectionTask connectionTask;
	
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
				connectionTask = new ConnectionTask(textAddress.getText());
				System.out.println("connecting");
				connectionTask.start();
			} else if (state == State.Connected) {
				System.out.println("connected");
				result = connection;
				shlCreateNewSession.close();
			}
		}
	}

	private void connectionFailThreadReport(Exception connectionException) {
		try {
			setState(State.Input);
			
			MessageBox mb = new MessageBox(shlCreateNewSession, SWT.ICON_ERROR | SWT.OK);
			mb.setMessage("Can't connect to " + textAddress.getText() + "\n" + connectionException.getMessage());
			mb.setText("Error");
			mb.open();
		}
		catch (Exception e) {
			System.out.println("error in connectionFailThreadReport");
			e.printStackTrace();
		}
	}

	private void connectionSuccessThreadReport(ClientConnection newConnection) {
		try {
			this.connection = newConnection;
			setState(State.Connected);
		}
		catch (Exception e) {
			System.out.println("error in connectionSuccessThreadReport");
			e.printStackTrace();
		}
	}
	
	/**
	 * Create the dialog.
	 * @param parent
	 * @param style
	 */
	public CreateNewSessionDialog(Shell parent, Client client) {
		super(parent, 0);
		this.client = client;
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

	private void setBoldFont(Control label)
	{
		// Making the button
		Font defaultFont = label.getFont();
		FontData fontData = defaultFont.getFontData()[0];
		fontData.setStyle(fontData.getStyle() | SWT.BOLD);
		
		Font newFont = new Font(label.getDisplay(), fontData);
		label.setFont(newFont);
	}
	
	/**
	 * Create contents of the dialog.
	 */
	private void createContents() {
		shlCreateNewSession = new Shell(getParent(), SWT.DIALOG_TRIM);
		shlCreateNewSession.setVisible(false);

		shlCreateNewSession.setSize(344, 171);
		shlCreateNewSession.setText("Create a new session");
		GridLayout gl_shlCreateNewSession = new GridLayout(3, false);
		shlCreateNewSession.setLayout(gl_shlCreateNewSession);
		
		Composite composite = new Composite(shlCreateNewSession, SWT.NONE);
		GridLayout gl_composite = new GridLayout(2, false);
		gl_composite.verticalSpacing = 10;
		composite.setLayout(gl_composite);
		composite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true, 3, 1));
		
		Label lblSelectARemote = new Label(composite, SWT.WRAP);
		GridData gd_lblSelectARemote = new GridData(SWT.LEFT, SWT.CENTER, true, true, 2, 1);
		gd_lblSelectARemote.minimumHeight = 40;
		lblSelectARemote.setLayoutData(gd_lblSelectARemote);
		lblSelectARemote.setText("Select a remote machine address to create a connection with it");
		setBoldFont(lblSelectARemote);
		
		Label label = new Label(composite, SWT.NONE);
		label.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		label.setText("Remote address:");
		label.setBounds(0, 0, 87, 15);
		
		textAddress = new Text(composite, SWT.BORDER);
		textAddress.setText("127.0.0.1");
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
		gd_btnConnect.heightHint = 25;
		gd_btnConnect.widthHint = 90;
		gd_btnConnect.minimumWidth = 90;
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
		gd_btnCancel.heightHint = 25;
		gd_btnCancel.widthHint = 90;
		gd_btnCancel.minimumWidth = 90;
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
				arg0.doit = canClose();
			}
			
		});
		
		// Positioning in the screen center
		Rectangle screenSize = getParent().getDisplay().getPrimaryMonitor().getBounds();
		shlCreateNewSession.setLocation((screenSize.width - shlCreateNewSession.getBounds().width) / 2, (screenSize.height - shlCreateNewSession.getBounds().height) / 2);

		setState(State.Input);
		
		shlCreateNewSession.setVisible(true);

	}

	protected void connect() {
		setState(State.Connecting);
	}
	
	protected boolean canClose() {
		if (state == State.Input) {
			return true;
		} else if (state == State.Connecting) {
			setState(State.Input);
			System.out.println("cancelled");
			connectionTask.setCancelled();
			return false;
		} else if (state == State.Connected) {
			// Can't cancel connected
			return true;
		} else {
			throw new RuntimeException("Invalid state");
		}
	}
}
