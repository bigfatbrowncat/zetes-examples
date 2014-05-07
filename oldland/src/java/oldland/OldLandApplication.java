package oldland;

import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Display;

import zetes.ApplicationBase;
import zetes.NullDocument;
import zetes.ui.DefaultAboutBox;


public class OldLandApplication extends ApplicationBase<DefaultAboutBox, NullDocument, OldLandTerminalWindow, OldLandMenuConstructor, OldLandViewWindowsManager>
{
	private OldLandTerminalWindow glViewWindow;
	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		Display.getDefault();
		
		final OldLandApplication app = new OldLandApplication();
		
		app.run(args);
	}

	public void setViewWindow(OldLandTerminalWindow window)
	{
		this.glViewWindow = window;
	}
	
	@Override
	protected void onIdle()
	{
		if (glViewWindow == null)
		{
			if (getViewWindowsManager().getViewsForDocument(null).size() > 0)
			{
				glViewWindow = getViewWindowsManager().getViewsForDocument(null).get(0);
			}
		}
		glViewWindow.updateFrame();

	}
	
	@Override
	public String getTitle()
	{
		return "OldLand terminal screen emulator";
	}

	@Override
	public DefaultAboutBox createAboutBox(OldLandTerminalWindow parentWindow)
	{
		DefaultAboutBox res = new DefaultAboutBox(parentWindow);
		res.setApplicationName(getTitle());
		res.setIconResourceName("/oldland/wingscreen64.png");
		res.setDescriptionText("A graphics demonstration which uses OpenGL canvas.\nThis application shows the power of Avian + SWT");
		res.setCopyrightText("Copyright \u00a9 2014, Ilya Mizus");
		res.setWindowSize(new Point(410, 180));

		return res;
	}

	@Override
	public OldLandViewWindowsManager createViewWindowsManager()
	{
		return new OldLandViewWindowsManager();
	}

	@Override
	public OldLandMenuConstructor createMenuConstructor(OldLandViewWindowsManager viewWindowsManager)
	{
		return new OldLandMenuConstructor(viewWindowsManager);
	}

	@Override
	public NullDocument loadFromFile(String fileName)
	{
		return null;
	}

	@Override
	public boolean needsAtLeastOneView()
	{
		return true;
	}

}
