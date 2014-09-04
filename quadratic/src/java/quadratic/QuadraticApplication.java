package quadratic;

import java.io.IOException;

import org.eclipse.swt.graphics.Point;

import zetes.wings.DefaultAboutBox;
import zetes.wings.actions.Handler;
import zetes.wings.base.ApplicationBase;


public class QuadraticApplication extends ApplicationBase<DefaultAboutBox, QuadraticDocument, QuadraticViewWindow, QuadraticMenuConstructor, QuadraticViewWindowsManager>
{
	@Override
	public String getTitle()
	{
		return "Quadratic";
	}

	@Override
	public DefaultAboutBox createAboutBox(QuadraticViewWindow window)
	{
		DefaultAboutBox res = new DefaultAboutBox(window);
		res.setApplicationName(getTitle());
		res.setIconResourceName("/quadratic/quadratic64.png");
		res.setDescriptionText("A simple JNI-powered math solver\nSolves a quadratic equation");
		res.setCopyrightText("Copyright \u00a9 2014, Ilya Mizus");
		res.setWindowSize(new Point(370, 180));
		return res;
	}
	
	@Override
	public QuadraticDocument loadFromFile(String fileName)
	{
		try
		{
			return new QuadraticDocument(fileName);
		}
		catch (IOException e)
		{
			e.printStackTrace();
			return null;
		}
	}
	
	@Override
	public QuadraticViewWindowsManager createViewWindowsManager()
	{
		return new QuadraticViewWindowsManager();
	}

	@Override
	public QuadraticMenuConstructor createMenuConstructor(QuadraticViewWindowsManager viewWindowsManager)
	{
		QuadraticMenuConstructor menuConstructor = new QuadraticMenuConstructor(viewWindowsManager);
		menuConstructor.setFileNewHandler(new Handler<QuadraticViewWindow>() {
			
			@Override
			public void execute(QuadraticViewWindow arg0) {
				QuadraticDocument flyerDocument = new QuadraticDocument();
				getViewWindowsManager().openWindowForDocument(flyerDocument);
			}
		});
		return menuConstructor;
	}

	@Override
	public boolean needsAtLeastOneView()
	{
		return false;
	}

	public static void main(String... args)
	{
		new QuadraticApplication().run(args);
	}
}
