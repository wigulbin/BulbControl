package com.augment.golden.bulbcontrol.Beans.HueApi;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.augment.golden.bulbcontrol.Beans.LifxApi.LifxBulb;
import com.augment.golden.bulbcontrol.Beans.SmartBulb;
import com.augment.golden.bulbcontrol.Changeable;
import com.google.gson.Gson;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class HueBulb extends SmartBulb implements Changeable {

    private double xy;
    private String bridgeId = "";

    private static Map<String, HueBulb> bulbMap = new ConcurrentHashMap<>();

    public HueBulb(){
        super();
    }
    public HueBulb(String id){
        super(id);
    }
    public HueBulb(String id, String name){
        super (id, name);
    }
    public HueBulb(String id, String name, String bridgeId){
        super (id, name);
        this.bridgeId = bridgeId;
    }

    public static void saveBulb(HueBulb bulb, Context context){
        bulbMap.put(bulb.getId(), bulb);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(bulb.getId());
        editor.apply();

        Gson bulbJson = new Gson();
        editor.putString(bulb.getId(), bulbJson.toJson(bulb));

        // Find/Replace mac address
        Set<String> macAddresses = new HashSet<>(sharedPreferences.getStringSet("macAddresses", new HashSet<String>()));
        macAddresses.add(bulb.getId());
        editor.putStringSet("macAddresses", macAddresses);
        editor.apply();
    }

    public static boolean exists(LifxBulb bulb, Context context){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        Set<String> macAddresses = sharedPreferences.getStringSet("macAddresses", new HashSet<String>());
        return macAddresses.contains(bulb.getId());
    }

    public static HueBulb getBulb(String id, Context context){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String bulbJSON = sharedPreferences.getString(id, "");
        Gson bulbJson = new Gson();
        return bulbJson.fromJson(bulbJSON, HueBulb.class);
    }


    public String getBridgeId() {
        return bridgeId;
    }

    public void setBridgeId(String bridgeId) {
        this.bridgeId = bridgeId;
    }

    public static void addBulb(HueBulb bulb){
        bulbMap.put(bulb.getId(), bulb);
    }

    public static HueBulb findBulb(String id){
        return bulbMap.get(id);
    }


    public void changePower(){
        HueBulb bulb = this;
        new Thread(() -> new HueWrapper(bulb).changePower(bulb.isOn())).start();
    }
    public void changeBrightness(){
        HueBulb bulb = this;
        new Thread(() -> new HueWrapper(bulb).changeBrightness(bulb.getBrightness())).start();
    }
    public void changeHue(){
        HueBulb bulb = this;
        new Thread(() -> new HueWrapper(bulb).changeHue(bulb.getHue())).start();
    }
    public void changeSaturation(){
        HueBulb bulb = this;
        new Thread(() -> new HueWrapper(bulb).changeSaturation(bulb.getSaturation())).start();
    }
    public void changeKelvin(){
        HueBulb bulb = this;
        new Thread(() -> new HueWrapper(bulb).changeKelvin(bulb.getKelvin())).start();
    }
    public void changeState(){
        HueBulb bulb = this;
        new Thread(() -> new HueWrapper(bulb).changeState(bulb.isOn(), bulb.getBrightness(), bulb.getHue(), bulb.getSaturation())).start();
    }
}
