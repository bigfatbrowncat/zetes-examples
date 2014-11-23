package parrot.client;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.CookieStore;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class APIClient {
	public enum Method {
		GET("GET"),
		POST("POST");
		
		private String code;
		Method(String code) {
			this.code = code;
		}
		
		public String getCode() {
			return code;
		}
	}
	
	private String serverAddress;
	private CookieManager cookieManager;
	
	public APIClient(final String serverAddress) {
		this.serverAddress = serverAddress;
		cookieManager = new CookieManager( new CookieStore() {
			
			List<HttpCookie> cookies = new ArrayList<>();
			
			@Override
			public boolean removeAll() {
				cookies.clear();
				return true;
			}
			
			@Override
			public boolean remove(URI arg0, HttpCookie arg1) {
				return cookies.remove(arg1);
			}
			
			@Override
			public List<URI> getURIs() {
				try {
					return Arrays.asList(new URI[] { new URI(serverAddress) });
				} catch (URISyntaxException e) {
					e.printStackTrace();
					return null;
				}
			}
			
			@Override
			public List<HttpCookie> getCookies() {
				return cookies;
			}
			
			@Override
			public List<HttpCookie> get(URI arg0) {
				return cookies;
			}
			
			@Override
			public void add(URI arg0, HttpCookie arg1) {
				cookies.add(arg1);
			}
		}, CookiePolicy.ACCEPT_ALL );
		CookieHandler.setDefault(cookieManager);
	}
	
	public <T> T requestObject(String url, Map<String, Object> parameters, Class<T> clz, Method method, String toSend) throws ClientConnectionProblemException{
		HttpURLConnection httpURLConnection = null;
		InputStream responseStream = null;
		PrintWriter requestStreamWriter = null;
		
		try {
			String charset = "UTF-8";
			
			StringBuilder sb = new StringBuilder();
			if (parameters != null && parameters.size() > 0) {
				sb.append("?");
				for (String key : parameters.keySet()) {
					sb.append(key);
					sb.append("=");
					sb.append(URLEncoder.encode(parameters.get(key).toString(), charset));
					sb.append("&");
				}
				sb.setLength(sb.length() - 1); // Cutting away the last '&'
			}
			httpURLConnection = (HttpURLConnection) new URL(serverAddress + "/" + url + sb.toString()).openConnection();

			/*System.out.println("REQUEST");
			System.out.println("Request: " + serverAddress + "/" + url + sb.toString() + "\n" + httpURLConnection.toString());
			*/
			httpURLConnection.setRequestMethod(method.getCode());
			if (method == Method.POST) {
				httpURLConnection.addRequestProperty("Content-type","text/plain; charset=" + charset);
				httpURLConnection.setDoInput(true);
				httpURLConnection.setDoOutput(true);

				requestStreamWriter = new PrintWriter(httpURLConnection.getOutputStream());
				requestStreamWriter.print(toSend);
				requestStreamWriter.flush();
			}
			
			int responseCode = httpURLConnection.getResponseCode();
			/*System.out.println("RESPONSE");
			System.out.println("Response code: " + responseCode);
			for (String list : httpURLConnection.getHeaderFields().keySet()) {
				System.out.print(list + ": ");
				for (String s : httpURLConnection.getHeaderFields().get(list)) {
					System.out.print(s + ", ");
				}
				System.out.println();
			}*/
			
			if (responseCode % 100 != 5) {
				responseStream = httpURLConnection.getInputStream();
				InputStreamReader responseReader = new InputStreamReader(responseStream);
				
				GsonBuilder builder = new GsonBuilder();
				Gson gson = builder.create();
				return gson.fromJson(responseReader, clz);
				
				
			} else {
				throw new RuntimeException("Server returned code " + responseCode);
			}
			
		} catch (Exception e) {
			throw new ClientConnectionProblemException("Can't login to the server", e);
		} finally {
			if (responseStream != null) {
				try {
					responseStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
			if (requestStreamWriter != null) {
				requestStreamWriter.close();
			}
			
			if (httpURLConnection != null) {
				httpURLConnection.disconnect();
			}
		}
	}
	
	public String getServerAddress() {
		return serverAddress;
	}
}
