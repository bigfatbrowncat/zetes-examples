package parrot.ara;

import java.io.IOException;

import org.eclipse.swt.graphics.Point;

import parrot.client.APIClient;
import zetes.wings.DefaultAboutBox;
import zetes.wings.base.ApplicationBase;


public class AraApplication extends ApplicationBase<DefaultAboutBox, AraDocument, AraViewWindow, AraMenuConstructor, AraViewWindowsManager>
{
	private APIClient apiClient;

	public AraApplication() {
		apiClient = new APIClient("http://localhost:8080");	// TODO Make address customizable
	}
	
	public APIClient getAPIClient() {
		return apiClient;
	}
	
	@Override
	public String getTitle()
	{
		return "Flyer";
	}

	@Override
	public DefaultAboutBox createAboutBox(AraViewWindow window)
	{
		DefaultAboutBox res = new DefaultAboutBox(window);
		res.setApplicationName(getTitle());
		res.setIconResourceName("/ara/ara64.png");
		res.setDescriptionText("A Parrot client application.\nIt should be used to connect to parrot-server app");
		res.setCopyrightText("Copyright \u00a9 2014, Ilya Mizus");
		res.setWindowSize(new Point(370, 180));
		return res;
	}
	
	@Override
	public AraDocument loadFromFile(String fileName)
	{
		try
		{
			return new AraDocument(fileName);
		}
		catch (IOException e)
		{
			e.printStackTrace();
			return null;
		}
	}
	
	@Override
	public AraViewWindowsManager createViewWindowsManager()
	{
		return new AraViewWindowsManager(apiClient);
	}

	@Override
	public AraMenuConstructor createMenuConstructor(AraViewWindowsManager viewWindowsManager)
	{
		AraMenuConstructor menuConstructor = new AraMenuConstructor(viewWindowsManager);
		return menuConstructor;
	}

	@Override
	public boolean needsAtLeastOneView()
	{
		return true;
	}

	public static void main(String... args)
	{
		new AraApplication().run(args);
	}
}
