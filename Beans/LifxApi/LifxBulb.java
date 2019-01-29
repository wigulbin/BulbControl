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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class LifxBulb extends SmartBulb {
    private String mac = "";
    private String label = "";
    private String group = "";
    private String location = "";

    private int hue;
    private int saturation;
    private int brightness;
    private int kelvin;

    private static Map<String, LifxBulb> bulbMap = new ConcurrentHashMap<>();

    public LifxBulb(){};
    public LifxBulb(String mac){
        this.mac = mac;
    }
    public LifxBulb(String mac, String label){
        this.mac = mac;
        this.label = label;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LifxBulb lifxBulb = (LifxBulb) o;
        return Objects.equals(mac, lifxBulb.mac);
    }

    @Override
    public int hashCode() {
        return Objects.hash(mac);
    }

    public static void clearBulbs(Context context){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }

    public static void saveBulb(LifxBulb bulb, Context context){
        bulbMap.put(bulb.getMac(), bulb);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(bulb.getMac());
        editor.apply();

        // Find/Replace mac address
        Set<String> macAddresses = new HashSet<>(sharedPreferences.getStringSet("macAddresses", new HashSet<String>()));
        macAddresses.add(bulb.getMac());
        editor.putStringSet("macAddresses", macAddresses);

        Set<String> values = new HashSet<>();
        values.add("label_" + bulb.getLabel());
        values.add("group_" + bulb.getGroup());
        values.add("location_" + bulb.getLocation());

        values.add("hue_" + bulb.getHue());
        values.add("saturation_" + bulb.getSaturation());
        values.add("brightness_" + bulb.getBrightness());
        values.add("kelvin_" + bulb.getKelvin());
        editor.putStringSet(bulb.getMac(), values);
        editor.apply();
    }

    public static boolean exists(LifxBulb bulb, Context context){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        Set<String> macAddresses = sharedPreferences.getStringSet("macAddresses", new HashSet<String>());
        return macAddresses.contains(bulb.getMac());
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
                bulbMap.put(bulb.getMac(), bulb);
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

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public int getHue() {
        return hue;
    }

    public void setHue(int hue) {
        this.hue = hue;
    }

    public int getSaturation() {
        return saturation;
    }

    public void setSaturation(int saturation) {
        this.saturation = saturation;
    }

    public int getBrightness() {
        return brightness;
    }

    public void setBrightness(int brightness) {
        this.brightness = brightness;
    }

    public int getKelvin() {
        return kelvin;
    }

    public void setKelvin(int kelvin) {
        this.kelvin = kelvin;
    }



    //Bulb network methods
    public static void findAndSaveBulbs(final SmartBulbListAdapter adapter, final Activity activity){
        Set<String> macSet = new HashSet<>();
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

                LifxBulb.saveBulb(bulb, activity);
                System.out.println(hsbk);
            }
        }

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                List<LifxBulb> bulbs = LifxBulb.getAllBulbs(activity);
                ProgressBar spinner = activity.findViewById(R.id.progressBar);
                if(spinner != null)
                    spinner.setVisibility(View.GONE);
                for (LifxBulb bulb : bulbs) {
                    adapter.remove(bulb);
                    adapter.add(bulb);
                }
            }
        });
    }
    public void changePower(boolean on, int duration){
        LifxWrapper.setPower(this.getMac(), on, duration);
    }

    public void changeHSBK(){
        LifxWrapper.setHSBK(this);
    }

}
