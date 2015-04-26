package snake;

import org.eclipse.swt.graphics.Point;

import zetes.wings.base.ApplicationBase;
import zetes.wings.NullDocument;
import zetes.wings.DefaultAboutBox;


public class SnakeApplication extends ApplicationBase<DefaultAboutBox, NullDocument, GameWindow, MenuConstructor, ViewWindowsManager>
{
	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		final SnakeApplication app = new SnakeApplication();
		
		app.run(args);
	}

	@Override
	public String getTitle()
	{
		return "Snake";
	}

	@Override
	public DefaultAboutBox createAboutBox(GameWindow parentWindow)
	{
		DefaultAboutBox res = new DefaultAboutBox(parentWindow);
		res.setApplicationName(getTitle());
		res.setIconResourceName("/gltest/wingcube64.png");
		res.setDescriptionText("A graphics demonstration which uses OpenGL canvas.\nThis application shows the power of Avian + SWT");
		res.setCopyrightText("Copyright \u00a9 2013, Ilya Mizus");
		res.setWindowSize(new Point(410, 180));

		return res;
	}

	@Override
	public ViewWindowsManager createViewWindowsManager()
	{
		return new ViewWindowsManager();
	}

	@Override
	public MenuConstructor createMenuConstructor(ViewWindowsManager viewWindowsManager)
	{
		return new MenuConstructor(viewWindowsManager);
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
