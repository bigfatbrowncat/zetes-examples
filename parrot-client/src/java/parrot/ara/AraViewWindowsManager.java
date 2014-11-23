package parrot.ara;

import parrot.client.APIClient;
import zetes.wings.base.ViewWindowsManagerBase;


public class AraViewWindowsManager extends ViewWindowsManagerBase<AraDocument, AraViewWindow>
{
	private APIClient apiClient;
	
	public AraViewWindowsManager(APIClient apiClient) {
		this.apiClient = apiClient;
	}

	@Override
	protected AraViewWindow createViewWindow() {
		return new AraViewWindow(apiClient);
	}

}
