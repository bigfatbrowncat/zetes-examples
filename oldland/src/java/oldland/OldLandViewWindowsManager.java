package oldland;

import zetes.NullDocument;
import zetes.ui.ViewWindowsManagerBase;

public class OldLandViewWindowsManager extends ViewWindowsManagerBase<NullDocument, OldLandTerminalWindow>
{
	@Override
	protected OldLandTerminalWindow createViewWindow()
	{
		return new OldLandTerminalWindow();
	}
}
