package snake;

import zetes.wings.NullDocument;
import zetes.wings.base.ViewWindowsManagerBase;

public class ViewWindowsManager extends ViewWindowsManagerBase<NullDocument, GameWindow>
{
	@Override
	protected GameWindow createViewWindow()
	{
		return new GameWindow();
	}
}
