package parrot.ara;

import zetes.wings.base.ViewWindowsManagerBase;


public class AraViewWindowsManager extends ViewWindowsManagerBase<AraDocument, AraViewWindow>
{

	@Override
	protected AraViewWindow createViewWindow() {
		return new AraViewWindow();
	}

}
