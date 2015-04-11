package htmlview;

import java.io.IOException;
import java.util.ArrayList;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;

import zetes.feet.WinLinMacApi;
import zetes.wings.DefaultAboutBox;
import zetes.wings.abstracts.Application;
import zetes.wings.abstracts.ApplicationListener;
import zetes.wings.actions.Handler;
import zetes.wings.base.ApplicationBase;

public class HTMLApplication extends ApplicationBase<DefaultAboutBox, HTMLDocument, HTMLViewWindow, HTMLMenuConstructor, HTMLViewWindowsManager>
{
	@Override
	public String getTitle()
	{
		return "LiteHTML Demo";
	}

	@Override
	public DefaultAboutBox createAboutBox(HTMLViewWindow window)
	{
		DefaultAboutBox res = new DefaultAboutBox(window);
		res.setApplicationName(getTitle());
		res.setIconResourceName("/html/html64.png");
		res.setDescriptionText("An HTML viewer application which doesn't depend on any external browser engine.\nIt's based on a small included liteHTML engine (http://www.litehtml.com)");
		res.setCopyrightText("Copyright \u00a9 2015, Ilya Mizus");
		res.setWindowSize(new Point(370, 220));
		return res;
	}
	
	@Override
	public HTMLDocument loadFromFile(String fileName)
	{
		try
		{
			return new HTMLDocument(fileName);
		}
		catch (IOException e)
		{
			e.printStackTrace();
			return null;
		}
	}
	
	private Handler<HTMLViewWindow> fileOpenHandler = new Handler<HTMLViewWindow>() {
		
		@Override
		public void execute(HTMLViewWindow window) {
			Shell dummyShell = new Shell(Display.getDefault());
			FileDialog fileDialog = new FileDialog(dummyShell, SWT.OPEN | SWT.MULTI);
			fileDialog.setText("Open image");
			fileDialog.setFilterNames(new String[] { "HTML page (*.html; *.htm)", "All files" });
			fileDialog.setFilterExtensions(new String[] { "*.html; *.htm", "*.*" });
			String firstFile = fileDialog.open();
			if (firstFile != null)
			{
				String[] names = fileDialog.getFileNames();
				ArrayList<HTMLDocument> documents = new ArrayList<HTMLDocument>();
				String fileStart = fileDialog.getFilterPath() + System.getProperty("file.separator");
				
				// Creating documents for files
				for (int i = 0; i < names.length; i++)
				{
					String fileName = fileStart + names[i];
					try
					{
						documents.add(new HTMLDocument(fileName));
					}
					catch (IOException e)
					{
						// TODO Show a message box here
						e.printStackTrace();
					}
				}
				
				getViewWindowsManager().openWindowsForDocuments(documents.toArray(new HTMLDocument[] {}));
			}
			dummyShell.dispose();		
		}
	};

	@Override
	public HTMLViewWindowsManager createViewWindowsManager()
	{
		return new HTMLViewWindowsManager();
	}

	@Override
	public HTMLMenuConstructor createMenuConstructor(HTMLViewWindowsManager viewWindowsManager)
	{
		HTMLMenuConstructor menuConstructor = new HTMLMenuConstructor(viewWindowsManager);
		menuConstructor.setFileOpenHandler(fileOpenHandler);
		return menuConstructor;
	}

	@Override
	public boolean needsAtLeastOneView()
	{
		return false;
	}

	public static void main(String... args)
	{
		HTMLApplication app = new HTMLApplication();
		
		app.setListener(new ApplicationListener() {
			
			@Override
			public void stopped(Application<?, ?, ?, ?, ?> application) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void started(Application<?, ?, ?, ?, ?> application) {
				HTMLDocument demoDoc = (HTMLDocument) application.loadFromFile(WinLinMacApi.locateResource("htmls", "demo.html"));
				((HTMLApplication)application).getViewWindowsManager().openWindowForDocument(demoDoc);
			}
		});
		
		app.run(args);
	}
}
