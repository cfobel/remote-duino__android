package net.fobel.android.remoteduino;

import java.io.Serializable;
import java.util.regex.*;
import java.util.*;
import java.io.IOException;
import net.fobel.android.HttpHelper;


public class RemoteCommand implements Serializable {
	/* This class stores a remote command consisting of an (arbitrary) text
	 * label (to denote the command's function), along with the corresponding
	 * remote protocol/code. */
	private static final long serialVersionUID = -4352237220714175048L;
	public String label;
	public String protocol;
	public String code;
	
	public RemoteCommand(String label, String protocol, String code) {
		/* Create a new RemoteCommand instance based specific protocol and code values. */
		super();
		this.label = label;
		this.protocol = protocol;
		this.code = code;
	}
	
	
	public RemoteCommand(String label, String response) throws IOException {
		/* Create a new RemoteCommand instance based on response from "learn" command.
		 * We parse the "learn" response to obtain the protocol and code values. */
		super();
		this.label = label;
		String patternstr__code = "<h2>\\s*(.*?)\\s*</h2>";
		Pattern pattern__code = Pattern.compile(patternstr__code, Pattern.DOTALL | Pattern.MULTILINE);
		String patternstr__protocol = "Received\\s+(NEC|SONY|RC6|RC5)\\[(\\d+)\\]:";
		Pattern pattern__protocol = Pattern.compile(patternstr__protocol, Pattern.DOTALL | Pattern.MULTILINE);
		
		Matcher matcher = pattern__code.matcher(response);
		boolean matchFound = matcher.find();

		if(matchFound) {
	        this.code = "0x" + matcher.group(1);
		} else {
			throw new IOException("No code found!");
		}
		
		matcher = pattern__protocol.matcher(response);
		matchFound = matcher.find();

		if(matchFound) {
	        this.protocol = matcher.group(2);
		} else {
			// TODO:  Don't do this.  It makes Brad's Stereo work.
//			throw new IOException("No protocol found!");
			this.protocol = "ReceivedSONY[1]";
		}
	}
	
	
    public static RemoteCommand learn_command(String label) throws Exception {
    	/* Static method which waits for a learned command response and returns
    	 * a RemoteCommand instance based on the "learn" response. */
    	/* TODO: Add IP address as a parameter that can be set in a menu.
    	 * 			This will require the IP to be passed as additional parameter. */
        String url = "http://192.168.0.50/learn";
        
        String result = HttpHelper.process_get_request(url);
        RemoteCommand cmd;
		try {
			cmd = new RemoteCommand(label, result);
		} catch(Exception e) {
			//Toast.makeText(this, "Error parsing response:\n" + result, Toast.LENGTH_SHORT).show();
			throw e;
		}
		return cmd;
	}
    
    public String send() {
    	/* Send the a request for the remote code/protocol to the appropriate
    	 * IP address. */
    	/* TODO: Add IP address argument. (see learn_command()) */
        Map<String, String> query_params = new HashMap<String, String>();
        query_params.put("c", code);
        query_params.put("p", protocol);
        
        String url = "http://192.168.0.50/send";
        String result = HttpHelper.process_get_request(url, query_params);
        return result;
    }
}