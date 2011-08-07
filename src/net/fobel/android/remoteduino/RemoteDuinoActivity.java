package net.fobel.android.remoteduino;

import android.app.Activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import java.util.*;
import java.io.InputStream;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.net.HttpURLConnection;


public class RemoteDuinoActivity extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
    }
    
    public byte[] serializeObject(Object o) { 
        ByteArrayOutputStream bos = new ByteArrayOutputStream(); 
     
        try { 
			ObjectOutput out = new ObjectOutputStream(bos); 
			out.writeObject(o); 
			out.close(); 
			// Get the bytes of the serialized object 
			byte[] buf = bos.toByteArray(); 
			return buf; 
        } catch(IOException ioe) { 
			Toast.makeText(this, "Error during serialization", Toast.LENGTH_SHORT).show();
			return null; 
        } 
	} 
    
    
    public Object deserializeObject(byte[] b) { 
        try { 
			ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(b)); 
			Object object = in.readObject(); 
			in.close(); 
			     
			return object; 
        } catch(ClassNotFoundException cnfe) { 
			Toast.makeText(this, "Error deserializeObject: class not found error", Toast.LENGTH_SHORT).show();
			     
			return null; 
        } catch(IOException ioe) { 
			Toast.makeText(this, "Error deserializeObject: io error", Toast.LENGTH_SHORT).show();
			     
			return null; 
        } 
	} 
    
    
    public void on__learn_code__handler(View view) throws IOException {
        String url = "http://192.168.1.182/learn";
        
        String result = process_get_request(view, url);
		try {
			RemoteCommand cmd = new RemoteCommand(result);
			Toast.makeText(this, "Found protocol: " + cmd.protocol, Toast.LENGTH_LONG).show();
			Toast.makeText(this, "Found code: " + cmd.code, Toast.LENGTH_LONG).show();
			byte[] cmd_bytes = serializeObject(cmd);
			RemoteCommand cmd2 = (RemoteCommand) deserializeObject(cmd_bytes); 
			Toast.makeText(this, "Deserialized: " + cmd2.code + ", " + cmd2.protocol, Toast.LENGTH_LONG).show();
		} catch(Exception e) {
			Toast.makeText(this, "Error parsing response:\n" + result, Toast.LENGTH_LONG).show();
		}
		
	}
    
    
    public void on__on_off__handler(View view) {
        String url = "http://192.168.1.182/send";
        Map<String, String> query_params = new HashMap<String, String>();
        query_params.put("c", "0xEE1101FE");
        query_params.put("p", "1");
        
        String result = process_get_request(view, url, query_params);
		Toast.makeText(this, "Result: " + result, Toast.LENGTH_SHORT).show();
	}
    
    
    String process_get_request(View view, String url, Map<String, String> query_params) {  
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
		return process_get_request(view, url);
    }
    
    
    String process_get_request(View view, String url) {  
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