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
    private HttpURLConnection connection;
    private JSONObject data;
    private String type;

    public RequestManager(String urlString, String type){
        try{
            URL url = new URL("http://" + urlString);
            connection = (HttpURLConnection) url.openConnection();
            if(!type.equals("GET")){
                connection.setDoOutput(true);
                connection.setDoInput(true);
                connection.setRequestProperty("Content-Type", "application/json");
            }
            connection.setRequestMethod(type);
            data = new JSONObject();
            this.type = type;
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public String sendData(){
        if(!type.equals("GET"))
            handleMessageSend();

        StringBuilder message = new StringBuilder();
        try(InputStream stream = connection.getInputStream()){
            int responseCode = connection.getResponseCode();
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
        try(OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream())){
            writer.write(data.toString());
            writer.flush();
            connection.connect();
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

    public boolean addData(String key, boolean value){
        try{
            data.put(key, value);
        }catch (JSONException e){
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public boolean addData(String key, int value){
        try{
            data.put(key, value);
        }catch (JSONException e){
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
