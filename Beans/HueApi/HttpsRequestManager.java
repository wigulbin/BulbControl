package com.augment.golden.bulbcontrol.Beans.HueApi;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataInputStream;
import java.io.OutputStreamWriter;
import java.net.URL;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;

public class HttpsRequestManager {
    private JSONObject data;
    private String type;
    private URL url;

    public HttpsRequestManager(String urlString, String type){
        try{
            url = new URL("https://" + urlString);
            data = new JSONObject();
            this.type = type;
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public boolean addData(String key, String value){
        try{
            data.put(key, value);
        }catch (JSONException e){
            e.printStackTrace();
            return false;
        }
        return true;
    }


    String sendData(){
        StringBuilder message = new StringBuilder();
        try{
            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.setRequestMethod(type);
            connection.setRequestProperty("Accept", "application/json");
            connection.setRequestProperty("X-Environment", "android");
            connection.setHostnameVerifier((hostname, session) -> true);
            connection.setSSLSocketFactory((SSLSocketFactory) SSLSocketFactory.getDefault());
            connection.setReadTimeout(95 * 1000);
            connection.setConnectTimeout(95 * 1000);
//            connection.connect();
            if(!type.equals("GET"))
                handleMessageSend(connection);

            try(DataInputStream input = new DataInputStream(connection.getInputStream() )){
                for( int c = input.read(); c != -1; c = input.read() )
                    message.append((char)c );
            } catch (Exception e){
                e.printStackTrace();
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        return message.toString();
    }

    private void handleMessageSend(HttpsURLConnection connection){
        try(OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream())){
            writer.write(data.toString());
            writer.flush();
            connection.connect();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
