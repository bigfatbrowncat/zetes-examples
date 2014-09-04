package dropfile;

import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DropTargetAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

import zetes.wings.base.ViewWindowsManagerBase;
import dropfile.protocol.Server;
import dropfile.protocol.Server.Action;
import dropfile.protocol.ServerConnection;

public class SessionViewWindowsManager extends ViewWindowsManagerBase<Session, SessionWindow>
{
	private Server server;
	
	private Server.ConnectionListener clientListener = new Server.ConnectionListener() {
		
		@Override
		public void onFailed(Server sender, Exception e) {
			showError("Client failed to connect.", e);
		}
		
		@Override
		public void onEstablished(Server sender, final ServerConnection connection) {
			Display.getDefault().asyncExec(new Runnable() {
				@Override
				public void run() {
					SessionViewWindowsManager.this.openWindowForDocument(new Session(connection));
				}
			});
		}
	};
	
	private Server.ServerListener serverListener = new Server.ServerListener() {
		
		@Override
		public void onStopped(Server sender) {
			// Do nothing.
		}
		
		@Override
		public Action onError(Server sender, Exception e) {
			showError("Server failure.", e);
			return Action.stop;
		}
	};
	
	private void showError(final String title, final Exception e) {
		Display.getDefault().asyncExec(new Runnable() {
			@Override
			public void run() {
				MessageBox mb = new MessageBox(new Shell(), SWT.ICON_ERROR | SWT.OK);
				String message = title;
				if (e != null && e.getMessage() != null) message += "\n" + e.getMessage(); 
				mb.setMessage(message);
				mb.setText("Error");
				mb.open();
			}
		});
	}
	
	private DropTargetAdapter viewWindowDropTargetAdapter = new DropTargetAdapter()
	{
		public void drop(DropTargetEvent event) {
			String fileList[] = null;
			FileTransfer ft = FileTransfer.getInstance();
			if (ft.isSupportedType(event.currentDataType)) {
				fileList = (String[]) event.data;
				for (int i = 0; i < fileList.length; i++)
				{
					/*ImageDocument document;
					try
					{
						document = new ImageDocument(fileList[i]);
						openWindowForDocument(document);
					}
					catch (IOException e)
					{
						e.printStackTrace();
					}*/
				}
			}
		}
	};

	
	public SessionViewWindowsManager(Server server) {
		this.server = server;
		server.addConnectionListener(clientListener);
		server.addServerListener(serverListener);
		server.listen();
	}
	
	@Override
	protected SessionWindow createViewWindow()
	{
		SessionWindow vw = new SessionWindow();
		vw.addDropTargetListener(viewWindowDropTargetAdapter);
		return vw;
	}

	public DropTargetAdapter getViewWindowDropTargetAdapter()
	{
		return viewWindowDropTargetAdapter;
	}
}
