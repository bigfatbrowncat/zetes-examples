package quadratic;

import zetes.wings.base.ViewWindowsManagerBase;


public class QuadraticViewWindowsManager extends ViewWindowsManagerBase<QuadraticDocument, QuadraticViewWindow>
{

	@Override
	protected QuadraticViewWindow createViewWindow() {
		return new QuadraticViewWindow();
	}

}
