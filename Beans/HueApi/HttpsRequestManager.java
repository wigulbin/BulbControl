package com.augment.golden.bulbcontrol.Beans.HueApi;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class HttpsRequestManager {
    private HttpsURLConnection connection;
    private JSONObject m_data;
    private String m_type;

    public HttpsRequestManager(String urlString, String type){
        try{
            URL url = new URL("https://" + urlString);
            connection = (HttpsURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.setRequestMethod(type);
            m_data = new JSONObject();
            m_type = type;
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public boolean addData(String key, String value){
        try{
            m_data.put(key, value);
        }catch (JSONException e){
            e.printStackTrace();
            return false;
        }
        return true;
    }


    String sendData(){
        if(!m_type.equals("GET"))
            handleMessageSend();

        StringBuilder message = new StringBuilder();
        try(DataInputStream input = new DataInputStream(connection.getInputStream() )){
            for( int c = input.read(); c != -1; c = input.read() )
                message.append((char)c );
        } catch (Exception e){
            e.printStackTrace();
        }

        return message.toString();
    }

    private void handleMessageSend(){
        try(OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream())){
            writer.write(m_data.toString());
            writer.flush();
            connection.connect();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
