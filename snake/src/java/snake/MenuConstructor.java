package snake;

import zetes.wings.abstracts.ViewWindowsManagerListener;
import zetes.wings.HotKey;
import zetes.wings.base.MenuConstructorBase;
import zetes.wings.actions.Action;
import zetes.wings.actions.ActionList;
import zetes.wings.actions.Separator;

public class MenuConstructor extends MenuConstructorBase<GameWindow> {
	private Action<GameWindow> newGameAction;
	private ViewWindowsManagerListener<GameWindow> viewWindowsManagerListener = new ViewWindowsManagerListener<GameWindow> () {
		public void windowOpened(GameWindow window) 
		{
			newGameAction.getHandlers().put(window, window.getNewGameHandler());
			updateMenus(window);
		}
		
		public void windowClosed(GameWindow window) 
		{
			newGameAction.getHandlers().remove(window);
			updateMenus(window);
		}

		@Override
		public void lastWindowClosed() {
			
		}
	};
		 
	/*private Action<GLViewWindow> viewModelCubeAction; 
	private Action<GLViewWindow> viewModelMonkeyAction; 
	private Action<GLViewWindow> viewModelMonkeySubdivAction; 
	
	private ViewWindowsManagerListener<GLViewWindow> viewWindowsManagerListener = new ViewWindowsManagerListener<GLViewWindow>() {
		
		@Override
		public void windowOpened(GLViewWindow window) {
			viewModelCubeAction.getHandlers().put(window, window.getViewCubeActionHandler());
			viewModelMonkeyAction.getHandlers().put(window, window.getViewMonkeyActionHandler());
			viewModelMonkeySubdivAction.getHandlers().put(window, window.getViewMonkeySubdivActionHandler());
			updateMenus(window);
		}
		
		@Override
		public void windowClosed(GLViewWindow window) {
			viewModelCubeAction.getHandlers().remove(window);
			viewModelMonkeyAction.getHandlers().remove(window);
			viewModelMonkeySubdivAction.getHandlers().remove(window);
		}
		
		@Override public void lastWindowClosed() {}
	};*/
	
	public MenuConstructor(ViewWindowsManager viewWindowsManager) {
		super(viewWindowsManager);
		viewWindowsManager.addListener(viewWindowsManagerListener);
		
		ActionList<GameWindow> fileMenu = getFileActionCategory();
		fileMenu.setTitle("Game");
		
		newGameAction = new Action<>("Start new");
		newGameAction.setHotKey(new HotKey(HotKey.MOD1, 'N'));
		fileMenu.addFirstItem(newGameAction);
		
		/*ActionList<GLViewWindow> viewModelActionList = new ActionList<>();

		viewModelCubeAction = new Action<>("Cube");
		viewModelCubeAction.setHotKey(new HotKey(0, '1'));
		viewModelActionList.addLastItem(viewModelCubeAction);

		viewModelMonkeyAction = new Action<>("Monkey simple");
		viewModelMonkeyAction.setHotKey(new HotKey(0, '2'));
		viewModelActionList.addLastItem(viewModelMonkeyAction);

		viewModelMonkeySubdivAction = new Action<>("Monkey subdivided");
		viewModelMonkeySubdivAction.setHotKey(new HotKey(0, '3'));
		viewModelActionList.addLastItem(viewModelMonkeySubdivAction);
		
		getViewActionCategory().addFirstItem(new Separator<GLViewWindow>());
		getViewActionCategory().addFirstItem(viewModelActionList);*/
	}

	/*public Action<GLViewWindow> getViewModelCubeAction() {
		return viewModelCubeAction;
	}

	public Action<GLViewWindow> getViewModelMonkeyAction() {
		return viewModelMonkeyAction;
	}

	public Action<GLViewWindow> getViewModelMonkeySubdivAction() {
		return viewModelMonkeySubdivAction;
	}*/
}
