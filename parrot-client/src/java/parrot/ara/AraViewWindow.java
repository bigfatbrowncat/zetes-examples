package parrot.ara;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import libcore.net.url.UrlUtils;

import org.apache.commons.io.IOUtils;
import org.apache.http.RequestLine;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.wb.swt.SWTResourceManager;

import zetes.wings.base.ViewWindowBase;

import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

public class AraViewWindow extends ViewWindowBase<AraDocument>
{
	private Text text;
	
	private SelectionAdapter sendSelectionAdapter = new SelectionAdapter() {
		@Override
		public void widgetSelected(SelectionEvent arg0) {
			try {
				String url = "http://localhost:8080";
				String charset = "UTF-8";
				
				String login = "il";
				String password = "1234";
	
				String query = String.format("login=%s&password=%s", 
				     URLEncoder.encode(login, charset), 
				     URLEncoder.encode(password, charset));
				
				HttpURLConnection httpURLConnection = (HttpURLConnection) new URL(url + "?" + query).openConnection();
				int responseCode = httpURLConnection.getResponseCode();
				InputStream responseStream = httpURLConnection.getInputStream();
				String responseString = IOUtils.toString(responseStream, charset);
				
				text.setText("Code " + responseCode + "; " + responseString);
			
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	};
	
	/**
	 * Create contents of the window.
	 * 
	 * @wbp.parser.entryPoint
	 */
	@Override
	protected Shell constructShell()
	{
		Shell shell = new Shell(SWT.TITLE | SWT.CLOSE | SWT.MIN | SWT.MAX | SWT.RESIZE | SWT.BORDER | SWT.DOUBLE_BUFFERED);
		shell.setSize(480, 360);

		shell.setMinimumSize(new Point(250, 200));

		shell.setImages(new Image[] { 
				SWTResourceManager.getImage(AraViewWindow.class, "/flyer/flyer512.png"),		// Necessary in OS X
				SWTResourceManager.getImage(AraViewWindow.class, "/flyer/flyer64.png"),		// Necessary in Windows (for Alt-Tab)
				SWTResourceManager.getImage(AraViewWindow.class, "/flyer/flyer16.png")		// Necessary in Windows (for taskbar)
		});
		shell.setLayout(new GridLayout(2, false));
		
		StyledText styledText = new StyledText(shell, SWT.BORDER);
		styledText.setEditable(false);
		styledText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
		
		text = new Text(shell, SWT.BORDER);
		GridData gd_text = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gd_text.heightHint = 67;
		text.setLayoutData(gd_text);
		
		Button sendButton = new Button(shell, SWT.NONE);
		sendButton.addSelectionListener(sendSelectionAdapter);
		sendButton.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1));
		sendButton.setText("Send");
	
			
		return shell;
	}

	@Override
	public void setDocument(AraDocument document)
	{
		super.setDocument(document);
		getShell().forceActive();
	}
	
	@Override
	public boolean supportsFullscreen()
	{
		return true;
	}

	@Override
	public boolean supportsMaximizing()
	{
		return true;
	}
}
