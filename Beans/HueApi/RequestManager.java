package com.augment.golden.bulbcontrol.Beans.HueApi;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class RequestManager {
    private HttpURLConnection m_connection;
    private JSONObject m_data;
    private String m_type;

    public RequestManager(String urlString, String type){
        try{
            URL url = new URL("http://" + urlString);
            m_connection = (HttpURLConnection) url.openConnection();
            if(!type.equals("GET")){
                m_connection.setDoOutput(true);
                m_connection.setDoInput(true);
                m_connection.setRequestProperty("Content-Type", "application/json");
            }
            m_connection.setRequestMethod(type);
            m_data = new JSONObject();
            m_type = type;
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public String sendData(){
        if(!m_type.equals("GET"))
            handleMessageSend();

        StringBuilder message = new StringBuilder();
        try(InputStream stream = m_connection.getInputStream()){
            int responseCode = m_connection.getResponseCode();
            if (responseCode != HttpsURLConnection.HTTP_OK)
                throw new IOException("HTTP error code: " + responseCode);

            for( int c = stream.read(); c != -1; c = stream.read() )
                message.append((char)c);
        } catch (Exception e){
            e.printStackTrace();
        }

        return message.toString();
    }

    private void handleMessageSend(){
        try(OutputStreamWriter writer = new OutputStreamWriter(m_connection.getOutputStream())){
            writer.write(m_data.toString());
            writer.flush();
            m_connection.connect();
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

    public boolean addData(String key, boolean value){
        try{
            m_data.put(key, value);
        }catch (JSONException e){
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public boolean addData(String key, int value){
        try{
            m_data.put(key, value);
        }catch (JSONException e){
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
