package com.augment.golden.bulbcontrol.Beans;

import android.content.Context;

import com.augment.golden.bulbcontrol.Beans.HueApi.HueBulb;
import com.augment.golden.bulbcontrol.Beans.LifxApi.LifxBulb;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SmartBulb {
    private String id = "";
    private String label = "";
    private String group = "";

    private int hue;
    private int saturation;
    private int brightness;
    private int kelvin;

    private boolean on;

    public static boolean singleView;
    private static Map<String, SmartBulb> bulbMap = new ConcurrentHashMap<>();

    public static void addBulb(SmartBulb bulb){
        bulbMap.put(bulb.getId(), bulb);
    }
    public static SmartBulb retrieveBulb(String id){
        return bulbMap.get(id);
    }
    public static List<SmartBulb> retrieveBulbs(){
        return new ArrayList<>(bulbMap.values());
    }
    public static List<SmartBulb> findSmartBulbs(Context context){
        List<LifxBulb> lifxBulbs = LifxBulb.getLifxBulbs(context);
        List<HueBulb> hueBulbs = HueBulb.getHueBulbs(context);

        List<SmartBulb> bulbs = new ArrayList<>();
        bulbs.addAll(lifxBulbs);
        bulbs.addAll(hueBulbs);

        return bulbs;
    }


    public SmartBulb(){}
    public SmartBulb(String id){
        this.id = id;
    }
    public SmartBulb(String id, String label){
        this.id = id;
        this.label = label;
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

    public void setKelvin(int kelvin) {
        this.kelvin = kelvin;
    }

    public int getKelvin() {
        return this.kelvin;
    }

    public int getBrightness() {
        return brightness;
    }

    public void setBrightness(int brightness) {
        this.brightness = brightness;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isOn() {
        return on;
    }

    public void setOn(boolean on) {
        this.on = on;
    }
}
