package parrot.ara;

import zetes.wings.HotKey;
import zetes.wings.base.MenuConstructorBase;
import zetes.wings.actions.Action;
import zetes.wings.actions.Handler;

public class AraMenuConstructor extends MenuConstructorBase<AraViewWindow>
{
	private Handler<AraViewWindow> fileOpenHandler;
	private Action<AraViewWindow> openAction;
	
	public AraMenuConstructor(AraViewWindowsManager viewWindowsManager) {
		super(viewWindowsManager);
		
		openAction = new Action<>("&Open");
		openAction.setHotKey(new HotKey(HotKey.MOD1, 'O'));
		getFileActionCategory().addFirstItem(openAction);
	}
	
	public Handler<AraViewWindow> getFileOpenHandler()
	{
		return fileOpenHandler;
	}

	public void setFileOpenHandler(Handler<AraViewWindow> fileOpenHandler)
	{
		this.fileOpenHandler = fileOpenHandler;
		if (openAction.getHandlers().get(null) == null) {

			openAction.getHandlers().put(null, fileOpenHandler);
		}
	}

}
