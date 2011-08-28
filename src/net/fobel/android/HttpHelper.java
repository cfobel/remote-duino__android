package net.fobel.android;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;

public class HttpHelper {
    public static String process_get_request(String url,
            Map<String, String> query_params) {
        String charset = "UTF-8";

        try {
            if(query_params.size() > 0) {
                String query = "?";
                boolean one_shot = true;
                for(Map.Entry<String, String> entry : query_params.entrySet()) {
                    if(one_shot) {
                        one_shot = false;
                    } else {
                        query += "&";
                    }
                    query += entry.getKey() + "="
                            + URLEncoder.encode(entry.getValue(), charset);
                }
                url += query;
            }
        } catch(UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return process_get_request(url);
    }

    public static String process_get_request(String url) {
        String charset = "UTF-8";
        String result = "";

        // Toast.makeText(this, "Sending: " + url, Toast.LENGTH_SHORT).show();
        HttpURLConnection connection;
        try {
            connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setReadTimeout(0);
            connection.setRequestProperty("Accept-Charset", charset);
            InputStream response = connection.getInputStream();
            result = convertStreamToString(response);
        } catch(MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            // Toast.makeText(this, "MalformedURLException",
            // Toast.LENGTH_SHORT).show();
        } catch(IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            // Toast.makeText(this, "IOException", Toast.LENGTH_SHORT).show();
        } catch(Throwable t) {
            t.printStackTrace();
            // Toast.makeText(this, "Unexpected exception: " + t,
            // Toast.LENGTH_SHORT).show();
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
            while((line = reader.readLine()) != null) {
                sb.append(line + "\n");
                Thread.sleep(100);
            }
        } catch(IOException e) {
            e.printStackTrace();
        } catch(InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch(IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }
}
