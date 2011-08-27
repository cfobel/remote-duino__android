package net.fobel.android.remoteduino.devices;

import java.util.HashMap;
import java.util.Map;

import net.fobel.android.HttpHelper;

public class WebArduinoIRDevice {
	private String mUrl;

	public WebArduinoIRDevice(String url) {
		setUrl(url);
	}

	public String getUrl() {
		return mUrl;
	}

	public void setUrl(String url) {
		String newURL = url;
		if(!newURL.startsWith("http://")){
			newURL = "http://" + newURL;
		} 
		
		if(!newURL.endsWith("/")){
			newURL += "/";
		}
		mUrl = newURL;
	}

	
	public String learn() {
		return HttpHelper.process_get_request(mUrl + "learn");
	}

	/**
	 * Send the a request for the remote code/protocol to the appropriate IP
	 * address.
	 * @return 
	 */
	public String send(String code, String protocol) {
        Map<String, String> query_params = new HashMap<String, String>();
        query_params.put("c", code);
        query_params.put("p", protocol);
        
        return HttpHelper.process_get_request(mUrl + "send", query_params);
	}

}
