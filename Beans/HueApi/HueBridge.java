package com.augment.golden.bulbcontrol.Beans.HueApi;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.augment.golden.bulbcontrol.Beans.LifxApi.LifxBulb;
import com.augment.golden.bulbcontrol.BulbGroup;
import com.augment.golden.bulbcontrol.Common;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class HueBridge {
    private String id = "";
    private String internalIpAddress = "";
    private String macaddress = "";
    private String name = "";
    private String username = "";
    private boolean exists;
    private boolean reachable;

    private static Map<String, HueBridge> bridgeMap = new HashMap<>();
    public static HueBridge getBridge(String id){
        return bridgeMap.get(id);
    }

    public HueBridge(){

    }

    // Hue Bridge Methods
    public static void findBridges(Context context){
        new Thread(()-> {
            HttpsRequestManager requestManager = new HttpsRequestManager("discovery.meethue.com/", "GET");
            try{
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                JSONArray jsonArray = new JSONArray(requestManager.sendData());
                Set<String> bridgeIds = new HashSet<>(sharedPreferences.getStringSet("hue_bridges", new HashSet<>()));

                for(int i = 0; i < jsonArray.length(); i++){
                    JSONObject bridgeJson = jsonArray.getJSONObject(i);
                    String id = bridgeJson.getString("id");
                    if(!bridgeIds.contains(id)){
                        bridgeIds.add(id);
                        HueBridge bridge = new HueBridge();
                        bridge.setId(id);
                        bridge.setInternalIpAddress(bridgeJson.getString("internalipaddress"));
                        Gson gson = new Gson();
                        editor.putString(id, gson.toJson(bridge));
                        editor.apply();
                    } else {
                        HueBridge bridge = retrieveBridge(id, context);
                        bridge.setInternalIpAddress(bridgeJson.getString("internalipaddress"));
                        bridge.save(context);
                    }
                }
                editor.putStringSet("hue_bridges", bridgeIds);
                editor.apply();
            }catch (Exception e){
                e.printStackTrace();
            }
        }).start();

    }
    public static HueBridge retrieveBridge(String id, Context context){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        Gson json = new Gson();
        String jsonString = sharedPreferences.getString(id, "");
        if(jsonString.length() > 0)
            return json.fromJson(jsonString, HueBridge.class);
        return null;
    }
    public static List<HueBridge> retrieveBridges(Context context){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        List<HueBridge> bridges = new ArrayList<>();

        Set<String> ids = sharedPreferences.getStringSet("hue_bridges", new HashSet<>());
        if(ids != null)
        {
            for (String id : ids)
            {
                HueBridge bridge = retrieveBridge(id, context);
                if(bridge != null)
                    bridges.add(bridge);
            }
        }
        bridges.forEach(bridge -> bridgeMap.put(bridge.getId(), bridge));

        return bridges;
    }

    public void save(Context context){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        Gson bridgeJson = new Gson();
        editor.putString(this.getId(), bridgeJson.toJson(this));
        editor.apply();
    }


    // Hue Bulb Methods
    public static List<HueBulb> findAllBulbs(Context context){
        List<HueBulb> bulbs = new ArrayList<>();
        List<HueBridge> bridges = retrieveBridges(context);
        bridges.forEach(bridge -> bulbs.addAll(bridge.findBulbs(context)));

        return bulbs;
    }

    public List<HueBulb> findBulbs(Context context){
        List<HueBulb> bulbs = new ArrayList<>();
        RequestManager manager = new RequestManager(this.getInternalIpAddress() + "/api/" + this.getUsername() + "/lights", "GET");
        String data = "";
        try{
            data += manager.sendData();
            if(!Common.isValidJsonObject(data))
                data += "}";

            JSONObject response = new JSONObject(data);
            bulbs.addAll(addBulbsFromJson(response, context));
        }catch (Exception e){
            e.printStackTrace();
        }

        return bulbs;
    }
    private List<HueBulb> addBulbsFromJson(JSONObject response, Context context) throws JSONException {
        List<HueBulb> bulbs = new ArrayList<>();
        int i = 1;
        while(response.has(i + "")){
            HueBulb bulb = parseBulbJSON(response.getJSONObject(i + ""), i, this.getId());
            if(bulb != null) {
                bulbs.add(bulb);
                bulb.save(context);
            }
            i++;
        }
        return bulbs;
    }
    private static HueBulb parseBulbJSON(JSONObject json, int id, String bridgeId){
        HueBulb bulb = null;
        try{
            bulb = new HueBulb(id + "", json.getString("name"));
            bulb.setBridgeId(bridgeId);
            JSONObject bulbState = json.getJSONObject("state");
            bulb.setOn(bulbState.getBoolean("on"));
            bulb.setBrightness(bulbState.getInt("bri"));
            bulb.setHue(bulbState.getInt("hue"));
            bulb.setSaturation(bulbState.getInt("sat"));
            bulb.setKelvin(bulbState.getInt("ct"));

            HueBulb.addBulb(bulb);
        }catch (Exception e){
            e.printStackTrace();
        }

        return bulb;
    }


    //Hue Group Methods
    public static List<BulbGroup> retrieveGroups(Context context){
        List<BulbGroup> groups = new ArrayList<>();

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        Set<String> bridgeIds = sharedPreferences.getStringSet("groupIds", new HashSet<>());
        for (String groupId : bridgeIds) {
            HueBulbGroup group = retrieveGroup(groupId, context);
            if(group != null)
            {
                groups.add(group);
                HueBulbGroup.addGroup(group);
            }
        }

        return groups;
    }
    public static HueBulbGroup retrieveGroup(String id, Context context){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        Gson json = new Gson();
        return json.fromJson(sharedPreferences.getString(id, ""), HueBulbGroup.class);
    }

    public void findAndSaveGroups(Context context){
        List<HueBulbGroup> groups = findGroups();

        Set<String> groupIds = new HashSet<>();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        for (HueBulbGroup group : groups) {
            Gson gson = new Gson();
            editor.putString(group.getName(), gson.toJson(group));
            groupIds.add(group.getName());
            HueBulbGroup.addGroup(group);
        }

        editor.putStringSet("groupIds", groupIds);
        editor.apply();
    }

    public List<HueBulbGroup> findGroups(){
        List<HueBulbGroup> groups = new ArrayList<>();
        RequestManager manager = new RequestManager(this.getInternalIpAddress() + "/api/" + this.getUsername() + "/groups", "GET");
        try{
            String data = manager.sendData();
            if(!Common.isValidJsonObject(data))
                data += "}";

            JSONObject response = new JSONObject(data);
            int i = 1;
            while(response.has(i + "")){
                HueBulbGroup group = parseGroupJSON(response.getJSONObject(i + ""), i, this.getId());

                if(group != null) groups.add(group);
                i++;
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        return groups;
    }

    private static HueBulbGroup parseGroupJSON(JSONObject json, int id, String bridgeId){
        HueBulbGroup group = null;
        try{
            group = new HueBulbGroup(json.getString("name"));
            group.setType(json.getString("type"));

            JSONArray lightsArray = json.getJSONArray("lights");
            List<String> lights = new ArrayList<>();
            for(int i = 0; i < lightsArray.length(); i++)
                lights.add(lightsArray.getString(i));

            group.setLights(lights);
            JSONObject state = json.getJSONObject("action");
            group.setOn(state.getBoolean("on"));
            group.setBrightness(state.getInt("bri"));
            group.setHue(state.getInt("hue"));
            group.setSaturation(state.getInt("sat"));
            group.setKelvin(state.getInt("ct"));
            group.setBridgeId(bridgeId);
            group.setId(id + "");

        }catch (Exception e){
            e.printStackTrace();
        }

        return group;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getInternalIpAddress() {
        return internalIpAddress;
    }

    public void setInternalIpAddress(String internalIpAddress) {
        this.internalIpAddress = internalIpAddress;
    }

    public String getMacaddress() {
        return macaddress;
    }

    public void setMacaddress(String macaddress) {
        this.macaddress = macaddress;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
