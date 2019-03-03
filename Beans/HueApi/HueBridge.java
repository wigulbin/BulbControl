package com.augment.golden.bulbcontrol.Beans.HueApi;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.augment.golden.bulbcontrol.Beans.LifxApi.LifxBulb;
import com.augment.golden.bulbcontrol.BulbGroup;
import com.google.gson.Gson;

import org.json.JSONArray;
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

    public static void findBridges(Context context){
        new Thread(()-> {
            HttpsRequestManager requestManager = new HttpsRequestManager("discovery.meethue.com/", "GET");
            try{
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                JSONArray jsonArray = new JSONArray(requestManager.sendData());
                Set<String> bridgeIds = new HashSet<>(sharedPreferences.getStringSet("hue_bridges", new HashSet<String>()));

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
                    }
                }
                editor.putStringSet("hue_bridges", bridgeIds);
                editor.apply();
            }catch (Exception e){
                e.printStackTrace();
            }
        }).start();

    }

    public static List<HueBridge> retrieveBridges(Context context){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        List<HueBridge> bridges = new ArrayList<>();

        Set<String> ids = sharedPreferences.getStringSet("hue_bridges", new HashSet<String>());
        if(ids != null)
        {
            for (String id : ids)
            {
                HueBridge bridge = retrieveBridge(id, context);
                bridges.add(bridge);
            }
        }

        return bridges;
    }
    public static HueBridge retrieveBridge(String id, Context context){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        Gson json = new Gson();
        return json.fromJson(sharedPreferences.getString(id, ""), HueBridge.class);
    }

    public static List<HueBulb> findAllBulbs(Context context){
        List<HueBulb> bulbs = new ArrayList<>();
        List<HueBridge> bridges = retrieveBridges(context);
        for (HueBridge bridge : bridges) {
            bulbs.addAll(bridge.findBulbs());
        }

        return bulbs;
    }

    public static List<BulbGroup> retrieveGroups(Context context){
        List<BulbGroup> groups = new ArrayList<>();

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        Set<String> bridgeIds = sharedPreferences.getStringSet("groupIds", new HashSet<>());
        for (String groupId : bridgeIds) {
            groups.add(retrieveGroup(groupId, context));
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
        }

        editor.putStringSet("groupIds", groupIds);
        editor.apply();
    }

    public List<HueBulbGroup> findGroups(){
        List<HueBulbGroup> groups = new ArrayList<>();
        RequestManager manager = new RequestManager(this.getInternalIpAddress() + "/api/" + this.getUsername() + "/groups", "GET");
        try{
            JSONObject response = new JSONObject(manager.sendData());
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

    public List<HueBulb> findBulbs(){
        List<HueBulb> bulbs = new ArrayList<>();
        RequestManager manager = new RequestManager(this.getInternalIpAddress() + "/api/" + this.getUsername() + "/lights", "GET");
        try{
            JSONObject response = new JSONObject(manager.sendData());
            int i = 1;
            while(response.has(i + "")){
                HueBulb bulb = parseBulbJSON(response.getJSONObject(i + ""), i, this.getId());
                if(bulb != null) bulbs.add(bulb);
                i++;
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        return bulbs;
    }

    private static HueBulbGroup parseGroupJSON(JSONObject json, int id, String bridgeId){
        HueBulbGroup group = null;
        try{
            group = new HueBulbGroup(json.getString("name"));
            group.setType(json.getString("type"));
            JSONObject state = json.getJSONObject("action");
            group.setOn(state.getBoolean("on"));
            group.setBrightness(state.getInt("bri"));
            group.setHue(state.getInt("hue"));
            group.setSaturation(state.getInt("sat"));
            group.setKelvin(state.getInt("ct"));

        }catch (Exception e){
            e.printStackTrace();
        }

        return group;
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

    public void save(Context context){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        Gson bridgeJson = new Gson();
        editor.putString(this.getId(), bridgeJson.toJson(this));
        editor.apply();
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
