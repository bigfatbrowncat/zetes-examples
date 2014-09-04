package quadratic;

import zetes.wings.HotKey;
import zetes.wings.base.MenuConstructorBase;
import zetes.wings.actions.Action;
import zetes.wings.actions.Handler;

public class QuadraticMenuConstructor extends MenuConstructorBase<QuadraticViewWindow>
{
	private Handler<QuadraticViewWindow> fileOpenHandler;
	private Action<QuadraticViewWindow> newAction;
	
	public QuadraticMenuConstructor(QuadraticViewWindowsManager viewWindowsManager) {
		super(viewWindowsManager);
		
		newAction = new Action<>("&New");
		newAction.setHotKey(new HotKey(HotKey.MOD1, 'N'));
		getFileActionCategory().addFirstItem(newAction);
	}
	
	public Handler<QuadraticViewWindow> getFileOpenHandler()
	{
		return fileOpenHandler;
	}

	public void setFileNewHandler(Handler<QuadraticViewWindow> fileNewHandler)
	{
		this.fileOpenHandler = fileNewHandler;
		if (newAction.getHandlers().get(null) == null) {

			newAction.getHandlers().put(null, fileNewHandler);
		}
	}

}
