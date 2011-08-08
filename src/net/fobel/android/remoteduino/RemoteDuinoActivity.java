package net.fobel.android.remoteduino;

import android.app.Activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import android.widget.TextView;
import java.util.*;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.net.HttpURLConnection;
import java.io.BufferedReader;
import java.io.InputStreamReader;


public class RemoteDuinoActivity extends Activity {
	RemoteCommandManager cmd_manager;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        try {
        	cmd_manager = new RemoteCommandManager(getApplicationContext());
    	} catch (IOException e) {
			Toast.makeText(this, "IOException", Toast.LENGTH_SHORT).show();
		} catch(Exception e) {
			Toast.makeText(this, "Unexpected exception: " + e, Toast.LENGTH_SHORT).show();
		}
        setContentView(R.layout.main);
		update_codes_list();
    }
    
    
    void update_codes_list() {
    	try {
	    	TextView txt__available_commands = (TextView) findViewById(R.id.txt__available_commands);
	    	String message = "";
	    	boolean one_shot = true;
	    	for(RemoteCommand cmd : cmd_manager.get_commands()) {
	    		if(one_shot) {
	    			one_shot = false;
	    		} else {
		    		message += "\n";
	    		}
	    		message += String.format("p=%s, c=%s", cmd.code, cmd.protocol);
	    	}
	    	txt__available_commands.setText(message);
    	} catch(Exception e) {
			Toast.makeText(this, "Unexpected exception!: " + e, Toast.LENGTH_SHORT).show();
    	}
    }
    
    
    public void on__learn_code__handler(View view) {
    	RemoteCommand cmd;
		try {
			Toast.makeText(this, "Press a button on your remote...", Toast.LENGTH_LONG).show();
			cmd = learn_command();
			cmd_manager.add(cmd);
			Toast.makeText(this, "Added code to local list: " + cmd.code + ", " + cmd.protocol, Toast.LENGTH_SHORT).show();
			update_codes_list();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    	
    public RemoteCommand learn_command() throws Exception {
        String url = "http://192.168.1.182/learn";
        
        String result = process_get_request(url);
        RemoteCommand cmd;
		try {
			cmd = new RemoteCommand(result);
		} catch(Exception e) {
			Toast.makeText(this, "Error parsing response:\n" + result, Toast.LENGTH_SHORT).show();
			throw e;
		}
		return cmd;
	}
    
    
    public void on__on_off__handler(View view) {
        RemoteCommand cmd = new RemoteCommand("1", "0xEE1101FE");
        send_remote_command(cmd);
    }
    
    
    public void send_remote_command(RemoteCommand cmd) {
        Map<String, String> query_params = new HashMap<String, String>();
        query_params.put("c", cmd.code);
        query_params.put("p", cmd.protocol);
        
        String url = "http://192.168.1.182/send";
        String result = process_get_request(url, query_params);
		Toast.makeText(this, "Result: " + result, Toast.LENGTH_SHORT).show();
	}
    
    
    String process_get_request(String url, Map<String, String> query_params) {  
        String charset = "UTF-8";

        try {
        	if(query_params.size() > 0) {
		        String query = "?";
	        	boolean one_shot = true;
	        	for (Map.Entry<String, String> entry : query_params.entrySet()) {
	        		if(one_shot) {
	        			one_shot = false;
	        		} else {
	        			query += "&";
	        		}
					query += entry.getKey() + "=" + URLEncoder.encode(entry.getValue(), charset);
	        	}
	        	url += query;
        	}
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		return process_get_request(url);
    }
    
    
    String process_get_request(String url) {  
        String charset = "UTF-8";
        String result = "";
        
        Toast.makeText(this, "Sending: " + url, Toast.LENGTH_SHORT).show();
        HttpURLConnection connection;
		try {
			connection = (HttpURLConnection) new URL(url).openConnection();
			connection.setReadTimeout(0);
	        connection.setRequestProperty("Accept-Charset", charset);
	        InputStream response = connection.getInputStream();
	        result = convertStreamToString(response);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Toast.makeText(this, "MalformedURLException", Toast.LENGTH_SHORT).show();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Toast.makeText(this, "IOException", Toast.LENGTH_SHORT).show();
		} catch (Throwable t) {
			Toast.makeText(this, "Unexpected exception: " + t, Toast.LENGTH_SHORT).show();
		}
		return result;
    }
    
    
    /**
    *
    * @param is
    * @return String
    */
   public static String convertStreamToString(InputStream is) {
	   BufferedReader reader = new BufferedReader(new InputStreamReader(is));
       StringBuilder sb = new StringBuilder();

       String line = null;
       try {
           while ((line = reader.readLine()) != null) {
               sb.append(line + "\n");
               Thread.sleep(100);
           }
       } catch (IOException e) {
           e.printStackTrace();
       } catch (InterruptedException e) {
    	   // TODO Auto-generated catch block
    	   e.printStackTrace();
       } finally {
           try {
               is.close();
           } catch (IOException e) {
               e.printStackTrace();
           }
       }
       return sb.toString();
   }
}