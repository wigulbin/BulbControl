package com.augment.golden.bulbcontrol.Beans.HueApi;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class RequestManager {
    private String m_url = "";
    private HttpURLConnection connection;
    private JSONObject m_data;
    private String m_type;

    public static enum Request {
        POST, GET, PUT, DELETE
    }

    public RequestManager(String m_url, String type){
        try{
            URL url = new URL("http://" + m_url);
            connection = (HttpURLConnection) url.openConnection();
//            connection.setRequestProperty("Accept", "application/json");
            if(!type.equals("GET")){
                connection.setDoOutput(true);
                connection.setDoInput(true);
                connection.setRequestProperty("Content-Type", "application/json");
            }
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


    public String sendData(){
        if(!m_type.equals("GET"))
            handleMessageSend();

        StringBuilder message = new StringBuilder();
        try(InputStream stream = connection.getInputStream()){
            int responseCode = connection.getResponseCode();
            if (responseCode != HttpsURLConnection.HTTP_OK)
                throw new IOException("HTTP error code: " + responseCode);

            for( int c = stream.read(); c != -1; c = stream.read() )
                message.append((char)c);
            System.out.println(message);
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
