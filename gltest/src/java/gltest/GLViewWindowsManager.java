package gltest;

import zetes.wings.NullDocument;
import zetes.wings.base.ViewWindowsManagerBase;

public class GLViewWindowsManager extends ViewWindowsManagerBase<NullDocument, GLViewWindow>
{
	@Override
	protected GLViewWindow createViewWindow()
	{
		return new GLViewWindow();
	}
}
