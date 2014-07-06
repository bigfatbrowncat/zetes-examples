package dropfile;

import java.io.IOException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DropTargetAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

import dropfile.protocol.Server;
import dropfile.protocol.Server.ClientConnectedListener;
import dropfile.protocol.ServerConnection;
import zetes.wings.base.ViewWindowsManagerBase;

public class SessionViewWindowsManager extends ViewWindowsManagerBase<Session, SessionWindow>
{
	private Server server;
	
	private ClientConnectedListener serverClientConnectedListener = new ClientConnectedListener() {

		@Override
		public void onClientConnected(Server sender, final ServerConnection connection) {
			Display.getDefault().asyncExec(new Runnable() {
				@Override
				public void run() {
					SessionViewWindowsManager.this.openWindowForDocument(new Session(connection));
				}
			});
		}
	};
	
	public SessionViewWindowsManager() {
		Thread serverThread = new Thread(new Runnable() {
			
			@Override
			public void run() {
				try {
					server = new Server();
					server.setClientConnectedListener(serverClientConnectedListener);
					server.listen();
				} catch (final IOException e) {
					Display.getDefault().asyncExec(new Runnable() {
						@Override
						public void run() {
							MessageBox mb = new MessageBox(new Shell(), SWT.ICON_ERROR | SWT.OK);
							mb.setMessage("Can't bind the server socket.\n" + e.getMessage());
							mb.setText("Error");
							mb.open();
							
							Display.getDefault().close();
						}
					});
					
					e.printStackTrace();
				}
				
			}
		});
		
		serverThread.setDaemon(true);
		serverThread.start();
		
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
