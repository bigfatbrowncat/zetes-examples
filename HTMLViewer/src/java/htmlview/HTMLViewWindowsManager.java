package htmlview;

import zetes.wings.base.ViewWindowsManagerBase;


public class HTMLViewWindowsManager extends ViewWindowsManagerBase<HTMLDocument, HTMLViewWindow>
{

	@Override
	protected HTMLViewWindow createViewWindow() {
		return new HTMLViewWindow();
	}

}
