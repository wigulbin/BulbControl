package com.augment.golden.bulbcontrol.Beans.LifxApi;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.ProgressBar;

import com.augment.golden.bulbcontrol.Adapters.SmartBulbListAdapter;
import com.augment.golden.bulbcontrol.Beans.HueApi.HueBulb;
import com.augment.golden.bulbcontrol.Beans.HueApi.HueWrapper;
import com.augment.golden.bulbcontrol.Beans.SmartBulb;
import com.augment.golden.bulbcontrol.Changeable;
import com.augment.golden.bulbcontrol.R;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class LifxBulb extends SmartBulb implements Changeable {
    private String location = "";

    private static Map<String, LifxBulb> bulbMap = new ConcurrentHashMap<>();

    public LifxBulb(){super();};
    public LifxBulb(String mac){
        super(mac);
    }
    public LifxBulb(String mac, String label){
        super(mac, label);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LifxBulb lifxBulb = (LifxBulb) o;
        return Objects.equals(getId(), lifxBulb.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }

    public static void clearBulbs(Context context){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove("macAddresses");
        editor.apply();
    }

    public static void saveBulb(LifxBulb bulb, Context context){
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

    public static List<LifxBulb> getAllBulbs(Context context){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        List<LifxBulb> bulbs = new ArrayList<>();

        Set<String> macs = sharedPreferences.getStringSet("macAddresses", new HashSet<String>());
        if(macs != null)
        {
            for (String mac : macs)
            {
                LifxBulb bulb = getBulb(mac, context);
                bulbs.add(bulb);
                bulbMap.put(bulb.getId(), bulb);
            }
        }

        return bulbs;
    }

    public static List<LifxBulb> getLifxBulbs(Context context){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        List<LifxBulb> bulbs = new ArrayList<>();

        Set<String> macs = sharedPreferences.getStringSet("macAddresses", new HashSet<String>());
        if(macs != null)
        {
            for (String mac : macs)
            {
                LifxBulb bulb = getBulb(mac, context);
                bulbs.add(bulb);
                bulbMap.put(bulb.getId(), bulb);
            }
        }

        return bulbs;
    }

    public static LifxBulb findBulb(String macAddress){
        return bulbMap.get(macAddress);
    }

    public static LifxBulb getBulb(String macAddress, Context context){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        LifxBulb bulb = new LifxBulb(macAddress);
        String bulbJSON = sharedPreferences.getString(macAddress, "");
        Gson bulbJson = new Gson();
        bulb = bulbJson.fromJson(bulbJSON, LifxBulb.class);

        return bulb;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }


    //Bulb network methods
    public static List<LifxBulb> findAllBulbs(final SmartBulbListAdapter adapter, final Activity activity){
        Set<String> macSet = new HashSet<>();
        List<LifxBulb> bulbs = new ArrayList<>();
        List<String> macAddresses = LifxWrapper.getAllMacAddresses();
        for (String macAddress : macAddresses) {
            if(macSet.add(macAddress)){
                final LifxBulb bulb = new LifxBulb(macAddress);
                String label = LifxWrapper.getLabel(macAddress);
                bulb.setLabel(label);

                HSBK hsbk = LifxWrapper.getHSBK(macAddress);
                bulb.setHue(hsbk.getHue());
                bulb.setSaturation(hsbk.getSaturation());
                bulb.setBrightness(hsbk.getBrightness());
                bulb.setKelvin(hsbk.getKelvin());

                bulbs.add(bulb);
                LifxBulb.saveBulb(bulb, activity);
            }
        }

        if(bulbs.size() == 0)
        {
            LifxBulb bulb = new LifxBulb("d073d53c6259", "Test");
            LifxBulb.saveBulb(bulb, activity);
            bulbs.add(bulb);
        }


        return bulbs;
    }
    public void changePower(){
        LifxBulb bulb = this;
        new Thread(() -> LifxWrapper.setPower(bulb.getId(), bulb.isOn(), 500)).start();
    }

    public void changeHsbk(){
        LifxBulb bulb = this;
        new Thread(() ->  LifxWrapper.setHSBK(bulb)).start();
    }


    public void changeBrightness(){
        LifxBulb bulb = this;
        new Thread(() ->  LifxWrapper.setHSBK(bulb)).start();
    }

    public void changeHue(){
        LifxBulb bulb = this;
        new Thread(() ->  LifxWrapper.setHSBK(bulb)).start();
    }

    public void changeSaturation(){
        LifxBulb bulb = this;
        new Thread(() ->  LifxWrapper.setHSBK(bulb)).start();
    }

    public void changeKelvin(){
        LifxBulb bulb = this;
        new Thread(() ->  LifxWrapper.setHSBK(bulb)).start();
    }

    public void changeState(){
        LifxBulb bulb = this;
        new Thread(() ->  LifxWrapper.setHSBK(bulb)).start();
    }



    @Override
    public void incrementHue(int amount) {
        LifxBulb bulb = this;
        bulb.setHue(amount + bulb.getHue());
        bulb.changeState();
    }

    @Override
    public void incrementSaturation(int amount) {
        LifxBulb bulb = this;
        bulb.setSaturation(amount + bulb.getSaturation());
        bulb.changeState();
    }

    @Override
    public void incrementKelvin(int amount) {
        LifxBulb bulb = this;
        bulb.setKelvin(amount + bulb.getKelvin());
        bulb.changeState();

    }

    @Override
    public void incrementBrightness(int amount) {
        LifxBulb bulb = this;
        bulb.setBrightness(bulb.getBrightness() + amount);
        bulb.changeState();
    }
}
