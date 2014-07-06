package dropfile;

import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Shell;

import zetes.wings.DefaultAboutBox;
import zetes.wings.actions.Handler;
import zetes.wings.base.ApplicationBase;
import dropfile.protocol.ClientConnection;


public class DropFileApplication extends ApplicationBase<DefaultAboutBox, Session, SessionWindow, DropFileMenuConstructor, SessionViewWindowsManager>
{
	//static {
	//	System.loadLibrary("tinyview.debug");
	//}
	
	@Override
	public String getTitle()
	{
		return "DropFile";
	}

	@Override
	public DefaultAboutBox createAboutBox(SessionWindow window)
	{
		DefaultAboutBox res = new DefaultAboutBox(window);
		res.setApplicationName(getTitle());
		res.setIconResourceName("/tinyviewer/wingphotos64.png");
		res.setDescriptionText("A simple network file sender & receiver.\nThis application shows the power of ZetesWings with Android classpath");
		res.setCopyrightText("Copyright \u00a9 2014, Ilya Mizus");
		res.setWindowSize(new Point(370, 180));
		return res;
	}
	
	@Override
	public Session loadFromFile(String fileName)
	{
		//return new Session(fileName);
		return null;
	}
	
	private Handler<SessionWindow> newSessionHandler = new Handler<SessionWindow>() {
		
		@Override
		public void execute(SessionWindow window) {
/*			Shell dummyShell = new Shell(Display.getDefault());
			FileDialog fileDialog = new FileDialog(dummyShell, SWT.OPEN | SWT.MULTI);
			fileDialog.setText("Open file");
			fileDialog.setFilterNames(new String[] { "All files" });
			fileDialog.setFilterExtensions(new String[] { "*.*" });
			String firstFile = fileDialog.open();
			if (firstFile != null)
			{
				String[] names = fileDialog.getFileNames();
				ArrayList<Session> documents = new ArrayList<Session>();
				
				// Creating documents for files
				for (int i = 0; i < names.length; i++)
				{
					String fileName = fileDialog.getFilterPath() + "/" + names[i];
					documents.add(new Session(fileName));
				}
				
				getViewWindowsManager().openWindowsForDocuments(documents.toArray(new Session[] {}));
			}
			dummyShell.dispose();*/
			
			Shell shell = window != null ? window.getShell() : new Shell();
			CreateNewSessionDialog dialog = new CreateNewSessionDialog(shell, 0);
			ClientConnection newConnection = (ClientConnection) dialog.open();
			if (newConnection != null) {
				getViewWindowsManager().openWindowForDocument(new Session(newConnection));
			}
		}
	};
	
	public DropFileApplication()
	{
	}
	
	@Override
	public SessionViewWindowsManager createViewWindowsManager()
	{
		return new SessionViewWindowsManager();
	}

	@Override
	public DropFileMenuConstructor createMenuConstructor(SessionViewWindowsManager viewWindowsManager)
	{
		DropFileMenuConstructor menuConstructor = new DropFileMenuConstructor(viewWindowsManager);
		menuConstructor.setNewSessionHandler(newSessionHandler);
		return menuConstructor;
	}

	@Override
	public boolean needsAtLeastOneView()
	{
		return false;
	}

	public static void main(String... args)
	{
		new DropFileApplication().run(args);
	}
}
