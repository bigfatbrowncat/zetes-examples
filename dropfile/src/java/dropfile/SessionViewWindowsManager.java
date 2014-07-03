package dropfile;

import org.eclipse.swt.dnd.DropTargetAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.FileTransfer;

import zetes.wings.base.ViewWindowsManagerBase;

public class SessionViewWindowsManager extends ViewWindowsManagerBase<Session, SessionWindow>
{
	private DropTargetAdapter viewWindowDropTargetAdapter = new DropTargetAdapter()
	{
		public void drop(DropTargetEvent event) {
			String fileList[] = null;
			FileTransfer ft = FileTransfer.getInstance();
			if (ft.isSupportedType(event.currentDataType)) {
				fileList = (String[]) event.data;
				for (int i = 0; i < fileList.length; i++)
				{
					/*ImageDocument document;
					try
					{
						document = new ImageDocument(fileList[i]);
						openWindowForDocument(document);
					}
					catch (IOException e)
					{
						e.printStackTrace();
					}*/
				}
			}
		}
	};
	
	@Override
	protected SessionWindow createViewWindow()
	{
		SessionWindow vw = new SessionWindow();
		vw.addDropTargetListener(viewWindowDropTargetAdapter);
		return vw;
	}

	public DropTargetAdapter getViewWindowDropTargetAdapter()
	{
		return viewWindowDropTargetAdapter;
	}
}
