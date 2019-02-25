package com.augment.golden.bulbcontrol.Beans.LifxApi;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.ProgressBar;

import com.augment.golden.bulbcontrol.Adapters.SmartBulbListAdapter;
import com.augment.golden.bulbcontrol.Beans.SmartBulb;
import com.augment.golden.bulbcontrol.R;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class LifxBulb extends SmartBulb {
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
        editor.clear();
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

        Set<String> values = new HashSet<>();
        values.add("label_" + bulb.getLabel());
        values.add("group_" + bulb.getGroup());
        values.add("location_" + bulb.getLocation());

        values.add("hue_" + bulb.getHue());
        values.add("saturation_" + bulb.getSaturation());
        values.add("brightness_" + bulb.getBrightness());
        values.add("kelvin_" + bulb.getKelvin());
        editor.putStringSet(bulb.getId(), values);
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

    public static List<SmartBulb> getAllBulbsAsSmartBulbs(Context context){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        List<SmartBulb> bulbs = new ArrayList<>();

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
        Set<String> bulbInfo = sharedPreferences.getStringSet(macAddress, new HashSet<String>());
        if(bulbInfo != null)
            for (String info : bulbInfo) {
                if(info.startsWith("label"))
                    bulb.setLabel(info.substring(info.indexOf('_') + 1));
                if(info.startsWith("group"))
                    bulb.setGroup(info.substring(info.indexOf('_') + 1));
                if(info.startsWith("location"))
                    bulb.setLocation(info.substring(info.indexOf('_') + 1));

                if(info.startsWith("hue"))
                    bulb.setHue(Integer.parseInt(info.substring(info.indexOf('_') + 1)));
                if(info.startsWith("saturation"))
                    bulb.setSaturation(Integer.parseInt(info.substring(info.indexOf('_') + 1)));

                if(info.startsWith("brightness"))
                    bulb.setBrightness(Integer.parseInt(info.substring(info.indexOf('_') + 1)));
                if(info.startsWith("kelvin"))
                    bulb.setKelvin(Integer.parseInt(info.substring(info.indexOf('_') + 1)));
            }

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
    public void changePower(int duration){
        LifxBulb bulb = this;
        new Thread(() -> LifxWrapper.setPower(bulb.getId(), bulb.isOn(), duration)).start();
    }

    public void changeHsbk(){
        LifxBulb bulb = this;
        new Thread(() ->  LifxWrapper.setHSBK(bulb)).start();
    }

}
