package oldland;

import zetes.wings.NullDocument;
import zetes.wings.base.ViewWindowsManagerBase;

public class OldLandViewWindowsManager extends ViewWindowsManagerBase<NullDocument, OldLandTerminalWindow>
{
	@Override
	protected OldLandTerminalWindow createViewWindow()
	{
		return new OldLandTerminalWindow();
	}
}
