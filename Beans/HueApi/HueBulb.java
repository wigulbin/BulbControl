package com.augment.golden.bulbcontrol.Beans.HueApi;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.augment.golden.bulbcontrol.Beans.LifxApi.LifxBulb;
import com.augment.golden.bulbcontrol.Beans.SmartBulb;
import com.augment.golden.bulbcontrol.Changeable;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class HueBulb extends SmartBulb implements Changeable {

    private String bridgeId = "";

    public HueBulb(){
        super();
    }
    public HueBulb(String id, String name){
        super (id, name);
    }


    private final static int brightMax = 254;

    public static boolean exists(LifxBulb bulb, Context context){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        Set<String> macAddresses = sharedPreferences.getStringSet("hueids", new HashSet<>());
        return macAddresses.contains(bulb.getId());
    }

    public static List<HueBulb> getHueBulbs(Context context){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        List<HueBulb> bulbs = new ArrayList<>();

        Set<String> macs = sharedPreferences.getStringSet("hueids", new HashSet<>());
        if(macs != null)
        {
            for (String mac : macs)
            {
                HueBulb bulb = getBulb(mac, context);
                if(bulb != null)
                {
                    bulbs.add(bulb);
                    SmartBulb.addBulb(bulb);
                }
            }
        }

        return bulbs;
    }

    public static HueBulb getBulb(String id, Context context){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String bulbJSON = sharedPreferences.getString(id, "");
        Gson bulbJson = new Gson();
        return bulbJson.fromJson(bulbJSON, HueBulb.class);
    }



    public void save(Context context){
        HueBulb bulb = this;
        SmartBulb.addBulb(bulb);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(bulb.getId());
        editor.apply();

        Gson bulbJson = new Gson();
        editor.putString(bulb.getId(), bulbJson.toJson(bulb));

        // Find/Replace mac address
        Set<String> macAddresses = new HashSet<>(sharedPreferences.getStringSet("hueids", new HashSet<>()));
        macAddresses.add(bulb.getId());
        editor.putStringSet("hueids", macAddresses);
        editor.apply();
    }


    public String getBridgeId() {
        return bridgeId;
    }

    public void setBridgeId(String bridgeId) {
        this.bridgeId = bridgeId;
    }

    public static void addBulb(HueBulb bulb){
        SmartBulb.addBulb(bulb);
    }

    public static HueBulb findBulb(String id){
        return (HueBulb) SmartBulb.retrieveBulb(id);
    }



    public void changePower(){
        HueBulb bulb = this;
        new Thread(() -> new HueWrapper(bulb).changePower(bulb.isOn()).send()).start();
    }
    public void changeBrightness(){
        HueBulb bulb = this;
        new Thread(() -> new HueWrapper(bulb).changeBrightness(bulb.getBrightness()).send()).start();
    }
    public void changeHue(){
        HueBulb bulb = this;
        new Thread(() -> new HueWrapper(bulb).changeHue(bulb.getHue()).send()).start();
    }
    public void changeSaturation(){
        HueBulb bulb = this;
        new Thread(() -> new HueWrapper(bulb).changeSaturation(bulb.getSaturation()).send()).start();
    }
    public void changeKelvin(){
        HueBulb bulb = this;
        new Thread(() -> new HueWrapper(bulb).changeKelvin(bulb.getKelvin()).send()).start();
    }
    public void changeState(){
        HueBulb bulb = this;
        new Thread(() -> new HueWrapper(bulb).changeState(bulb.isOn(), bulb.getBrightness(), bulb.getHue(), bulb.getSaturation())).start();
    }

    @Override
    public void incrementHue(int amount) {
        HueBulb bulb = this;
        bulb.setHue(amount + bulb.getHue());
        new Thread(() -> new HueWrapper(bulb).incrementHue(amount).send()).start();
    }

    @Override
    public void incrementSaturation(int amount) {
        HueBulb bulb = this;
        bulb.setSaturation(amount + bulb.getSaturation());
        new Thread(() -> new HueWrapper(bulb).incrementSaturation(amount).send()).start();
    }

    @Override
    public void incrementKelvin(int amount) {
        HueBulb bulb = this;
        bulb.setKelvin(amount + bulb.getKelvin());
        new Thread(() -> new HueWrapper(bulb).incrementKelvin(amount).send()).start();

    }

    @Override
    public void incrementBrightness(int amount) {
        HueBulb bulb = this;
        bulb.setBrightness(bulb.getBrightness() + amount);
        new Thread(() -> new HueWrapper(bulb).incrementBrightness(amount).send()).start();
    }

    @Override
    public int retrieveBrightMax() {
        return brightMax;
    }
}
