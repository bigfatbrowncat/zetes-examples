package dropfile;

import zetes.wings.HotKey;
import zetes.wings.actions.Action;
import zetes.wings.actions.Handler;
import zetes.wings.base.MenuConstructorBase;
import zetes.wings.base.ViewWindowsManagerBase;

public class DropFileMenuConstructor extends MenuConstructorBase<SessionWindow>
{
	private Handler<SessionWindow> newSessionHandler;
	private Action<SessionWindow> newSessionAction;
	
	public DropFileMenuConstructor(ViewWindowsManagerBase<Session, SessionWindow> viewWindowsManager) {
		super(viewWindowsManager);
		
		newSessionAction = new Action<>("&Create session...");
		newSessionAction.setHotKey(new HotKey(HotKey.MOD1, 'N'));
		getFileActionCategory().addFirstItem(newSessionAction);
	}
	
	public Handler<SessionWindow> getNewSessionHandler() {
		return newSessionHandler;
	}
	
	public void setNewSessionHandler(Handler<SessionWindow> newSessionHandler) {
		this.newSessionHandler = newSessionHandler;
		newSessionAction.getHandlers().put(null, newSessionHandler);
	}
	
}
