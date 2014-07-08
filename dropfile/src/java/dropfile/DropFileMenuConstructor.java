package dropfile;

import zetes.wings.HotKey;
import zetes.wings.abstracts.ViewWindowsManagerListener;
import zetes.wings.actions.Action;
import zetes.wings.actions.Handler;
import zetes.wings.base.MenuConstructorBase;
import zetes.wings.base.ViewWindowsManagerBase;

public class DropFileMenuConstructor extends MenuConstructorBase<SessionWindow>
{
	private Handler<SessionWindow> newSessionHandler;
	private Action<SessionWindow> newSessionAction, sendFileAction;
	
	private ViewWindowsManagerListener<SessionWindow> viewWindowsManagerListener = new ViewWindowsManagerListener<SessionWindow>() {
		
		@Override
		public void windowOpened(SessionWindow window) {
			sendFileAction.getHandlers().put(window, window.getSendFileHandler());
			
		}
		
		@Override
		public void windowClosed(SessionWindow window) {
			sendFileAction.getHandlers().remove(window);
		}
		
		@Override public void lastWindowClosed() { }
	};
	
	public DropFileMenuConstructor(ViewWindowsManagerBase<Session, SessionWindow> viewWindowsManager) {
		super(viewWindowsManager);
		
		viewWindowsManager.addListener(viewWindowsManagerListener);
		
		newSessionAction = new Action<>("&Create session...");
		newSessionAction.setHotKey(new HotKey(HotKey.MOD1, 'N'));
		getFileActionCategory().addFirstItem(newSessionAction);
		
		sendFileAction = new Action<>("&Send file...");
		sendFileAction.setHotKey(new HotKey(HotKey.MOD1 | HotKey.ALT, 'S'));
		getFileActionCategory().addLastItem(sendFileAction);
	}
	
	public Handler<SessionWindow> getNewSessionHandler() {
		return newSessionHandler;
	}
	
	public void setNewSessionHandler(Handler<SessionWindow> newSessionHandler) {
		this.newSessionHandler = newSessionHandler;
		newSessionAction.getHandlers().put(null, newSessionHandler);
	}
}
