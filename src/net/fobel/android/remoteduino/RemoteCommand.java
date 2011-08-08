package net.fobel.android.remoteduino;

import java.io.Serializable;
import java.util.regex.*;
import java.io.IOException;


public class RemoteCommand implements Serializable {
	private static final long serialVersionUID = -4352237220714175048L;
	public String protocol;
	public String code;
	
	public RemoteCommand(String protocol, String code) {
		super();
		this.protocol = protocol;
		this.code = code;
	}
	
	public RemoteCommand(String response) throws IOException {
		super();
		String patternstr__code = "<h2>\\s*(.*?)\\s*</h2>";
		Pattern pattern__code = Pattern.compile(patternstr__code, Pattern.DOTALL | Pattern.MULTILINE);
		String patternstr__protocol = "Received\\s+(NEC|SONY|RC6|RC5)\\[(\\d+)\\]:";
		Pattern pattern__protocol = Pattern.compile(patternstr__protocol, Pattern.DOTALL | Pattern.MULTILINE);
		
		Matcher matcher = pattern__code.matcher(response);
		boolean matchFound = matcher.find();

		if(matchFound) {
	        this.code = matcher.group(1);
		} else {
			throw new IOException("No code found!");
		}
		
		matcher = pattern__protocol.matcher(response);
		matchFound = matcher.find();

		if(matchFound) {
	        this.protocol = matcher.group(2);
		} else {
			throw new IOException("No protocol found!");
		}
	}
}