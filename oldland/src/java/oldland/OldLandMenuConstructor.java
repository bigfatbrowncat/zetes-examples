package oldland;

import zetes.wings.abstracts.ViewWindowsManagerListener;
import zetes.wings.base.MenuConstructorBase;

public class OldLandMenuConstructor extends MenuConstructorBase<OldLandTerminalWindow> {
	private ViewWindowsManagerListener<OldLandTerminalWindow> viewWindowsManagerListener = new ViewWindowsManagerListener<OldLandTerminalWindow>() {
		
		@Override
		public void windowOpened(OldLandTerminalWindow window) {
			updateMenus(window);
		}
		
		@Override
		public void windowClosed(OldLandTerminalWindow window) {
		}
		
		@Override public void lastWindowClosed() {}
	};
	
	public OldLandMenuConstructor(OldLandViewWindowsManager viewWindowsManager) {
		super(viewWindowsManager);
		viewWindowsManager.addListener(viewWindowsManagerListener);
	}
}
