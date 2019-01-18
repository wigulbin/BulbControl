package com.augment.golden.bulbcontrol.Beans;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class SmartBulb {
    private String mac = "";
    private String label = "";
    private String group = "";
    private String location = "";


    public SmartBulb(){};
    public SmartBulb(String mac){
        this.mac = mac;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SmartBulb smartBulb = (SmartBulb) o;
        return Objects.equals(mac, smartBulb.mac);
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

    public static void saveBulb(SmartBulb bulb, Context context){
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
        editor.putStringSet(bulb.getMac(), values);
        editor.apply();
    }

    public static boolean exists(SmartBulb bulb, Context context){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        Set<String> macAddresses = sharedPreferences.getStringSet("macAddresses", new HashSet<String>());
        return macAddresses.contains(bulb.getMac());
    }

    public static List<SmartBulb> getAllBulbs(Context context){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        List<SmartBulb> bulbs = new ArrayList<>();

        Set<String> macs = sharedPreferences.getStringSet("macAddresses", new HashSet<String>());
        if(macs != null)
            for (String mac : macs)
                bulbs.add(getBulb(mac, context));

        return bulbs;
    }

    public static SmartBulb getBulb(String macAddress, Context context){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SmartBulb bulb = new SmartBulb(macAddress);
        Set<String> bulbInfo = sharedPreferences.getStringSet(macAddress, new HashSet<String>());
        if(bulbInfo != null)
            for (String info : bulbInfo) {
                if(info.startsWith("label"))
                    bulb.setLabel(info.substring(info.indexOf('_') + 1));
                if(info.startsWith("group"))
                    bulb.setGroup(info.substring(info.indexOf('_') + 1));
                if(info.startsWith("location"))
                    bulb.setLocation(info.substring(info.indexOf('_') + 1));
            }

        return bulb;
    }

    public static String parseMACFromReturn(String hex){
        int start = hex.indexOf("d073d5");
        int end = start + 12;
        if(start != -1 && end < hex.length())
            return hex.substring(start, end);

        return "";
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

}
