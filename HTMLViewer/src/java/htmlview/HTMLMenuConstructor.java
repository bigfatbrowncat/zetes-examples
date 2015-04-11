package htmlview;

import zetes.wings.HotKey;
import zetes.wings.base.MenuConstructorBase;
import zetes.wings.actions.Action;
import zetes.wings.actions.Handler;

public class HTMLMenuConstructor extends MenuConstructorBase<HTMLViewWindow>
{
	private Handler<HTMLViewWindow> fileOpenHandler;
	private Action<HTMLViewWindow> openAction;
	
	public HTMLMenuConstructor(HTMLViewWindowsManager viewWindowsManager) {
		super(viewWindowsManager);
		
		openAction = new Action<>("&Open");
		openAction.setHotKey(new HotKey(HotKey.MOD1, 'O'));
		getFileActionCategory().addFirstItem(openAction);
	}
	
	public Handler<HTMLViewWindow> getFileOpenHandler()
	{
		return fileOpenHandler;
	}

	public void setFileOpenHandler(Handler<HTMLViewWindow> fileOpenHandler)
	{
		this.fileOpenHandler = fileOpenHandler;
		if (openAction.getHandlers().get(null) == null) {

			openAction.getHandlers().put(null, fileOpenHandler);
		}
	}

}
