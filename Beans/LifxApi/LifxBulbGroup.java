package com.augment.golden.bulbcontrol.Beans.LifxApi;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.augment.golden.bulbcontrol.Beans.SmartBulb;
import com.augment.golden.bulbcontrol.BulbGroup;
import com.augment.golden.bulbcontrol.Changeable;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class LifxBulbGroup extends BulbGroup implements Changeable {
    private List<String> lights;
    private String type;
    private String bridgeId;

    private boolean on;
    private int brightness;
    private int hue;
    private int saturation;
    private int kelvin;
    private String effect;

    private final static int brightMax = 65535;

    private static Map<String, LifxBulbGroup> bulbGroupMap = new ConcurrentHashMap<>();


    public LifxBulbGroup(String name) {
        super(name);
        this.lights = new ArrayList<>();
    }


    public static LifxBulbGroup retrieveGroup(String id){
        return bulbGroupMap.get(id);
    }
    public static void addGroup(LifxBulbGroup group){
        bulbGroupMap.put(group.getId(), group);
    }

    public static List<LifxBulbGroup> convertLifxBulbsToGroup(List<LifxBulb> bulbs){
        Map<String, List<LifxBulb>> groupMap = new HashMap<>();
        bulbs.forEach(bulb -> groupMap.computeIfAbsent(bulb.getGroup(), key->new ArrayList<>()).add(bulb));
        List<LifxBulbGroup> groups = new ArrayList<>();
        AtomicInteger groupsNum = new AtomicInteger();
        groupMap.forEach((key, groupBulbs) -> {
            LifxBulbGroup group = new LifxBulbGroup(key);
            LifxBulb bulb = groupBulbs.get(0);
            group.setOn(bulb.isOn());
            group.setBrightness(bulb.getBrightness());
            group.setHue(bulb.getHue());
            group.setSaturation(bulb.getSaturation());
            group.setKelvin(bulb.getKelvin());
            group.setId(groupsNum.getAndIncrement() + "");
            for (LifxBulb groupBulb : groupBulbs)
                group.lights.add(groupBulb.getId());

            group.setName(bulb.getGroup());

            groups.add(group);
        });

        return groups;
    }

    public static void findAndSaveLifxBulbGroups(List<LifxBulb> bulbs, Context context){
        Set<LifxBulb> bulbSet = new HashSet<>(bulbs);
        bulbSet.addAll(LifxBulb.getLifxBulbs(context));
        List<LifxBulbGroup> groups = convertLifxBulbsToGroup(new ArrayList<>(bulbs));
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Set<String> lifxGroupIds = new HashSet<>();
        for (LifxBulbGroup group : groups) {
            Gson gson = new Gson();
            editor.putString(group.getId(), gson.toJson(group));
            bulbGroupMap.put(group.getId(), group);
            lifxGroupIds.add(group.getId());
        }

        editor.putStringSet("lifxGroups", lifxGroupIds);
        editor.apply();
    }

    public static void saveGroup(LifxBulbGroup group, Context context){
        LifxBulbGroup.addGroup(group);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(group.getId());
        editor.apply();

        Gson bulbJson = new Gson();
        editor.putString(group.getId(), bulbJson.toJson(group));

        // Find/Replace mac address
        Set<String> lifxGroups = new HashSet<>(sharedPreferences.getStringSet("lifxGroups", new HashSet<String>()));
        lifxGroups.add(group.getId());
        editor.putStringSet("lifxGroups", lifxGroups);
        editor.apply();
    }
    public static List<LifxBulbGroup> getLifxGroups(Context context){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        List<LifxBulbGroup> groups = new ArrayList<>();

        Set<String> lifxGroups = sharedPreferences.getStringSet("lifxGroups", new HashSet<String>());
        if(lifxGroups != null)
        {
            for (String id : lifxGroups)
            {
                LifxBulbGroup group = getGroup(id, context);
                groups.add(group);
                LifxBulbGroup.addGroup(group);
            }
        }

        return groups;
    }

    public static LifxBulbGroup getGroup(String id, Context context){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String bulbJSON = sharedPreferences.getString(id, "");
        Gson bulbJson = new Gson();
        return bulbJson.fromJson(bulbJSON, LifxBulbGroup.class);
    }

    public List<String> getLights() {
        return lights;
    }

    public void setLights(List<String> lights) {
        this.lights = lights;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isOn() {
        return on;
    }

    public void setOn(boolean on) {
        this.on = on;
    }

    public int getBrightness() {
        return brightness;
    }

    public void setBrightness(int brightness) {
        this.brightness = brightness;
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

    public String getEffect() {
        return effect;
    }

    public void setEffect(String effect) {
        this.effect = effect;
    }

    public int getKelvin() {
        return kelvin;
    }

    public void setKelvin(int kelvin) {
        this.kelvin = kelvin;
    }

    public String getBridgeId() {
        return bridgeId;
    }

    public void setBridgeId(String bridgeId) {
        this.bridgeId = bridgeId;
    }


    public void changePower(){
        LifxBulbGroup group = this;
        for(String bulbId : group.lights){
            LifxBulb bulb = LifxBulb.findBulb(bulbId);
            bulb.setOn(group.on);
            bulb.changePower();
        }
    }
    public void changeBrightness(){
        LifxBulbGroup group = this;
        for(String bulbId : group.lights){
            LifxBulb bulb = LifxBulb.findBulb(bulbId);
            bulb.setBrightness(group.brightness);
            bulb.changeBrightness();
        }
    }
    public void changeHue(){
        LifxBulbGroup group = this;
        for(String bulbId : group.lights){
            LifxBulb bulb = LifxBulb.findBulb(bulbId);
            bulb.setHue(group.hue);
            bulb.changeHue();
        }
    }
    public void changeSaturation(){
        LifxBulbGroup group = this;
        for(String bulbId : group.lights){
            LifxBulb bulb = LifxBulb.findBulb(bulbId);
            bulb.setSaturation(group.saturation);
            bulb.changeSaturation();
        }
    }
    public void changeKelvin(){
        LifxBulbGroup group = this;
        for(String bulbId : group.lights){
            LifxBulb bulb = LifxBulb.findBulb(bulbId);
            bulb.setKelvin(group.kelvin);
            bulb.changeKelvin();
        }
    }
    public void changeState(){
        LifxBulbGroup group = this;
        for(String bulbId : group.lights){
            LifxBulb bulb = LifxBulb.findBulb(bulbId);
            bulb.setOn(group.on);
            bulb.setBrightness(group.brightness);
            bulb.setHue(group.hue);
            bulb.setSaturation(group.saturation);
            bulb.setKelvin(group.kelvin);
            bulb.changeState();
        }
    }



    @Override
    public void incrementHue(int amount) {
    }

    @Override
    public void incrementSaturation(int amount) {
    }

    @Override
    public void incrementKelvin(int amount) {

    }

    @Override
    public void incrementBrightness(int amount) {
    }

    @Override
    public int retrieveBrightMax() {
        return brightMax;
    }

    @Override
    public List<SmartBulb> getBulbs(){
        List<SmartBulb> bulbs = new ArrayList<>(lights.size());
        lights.forEach(id -> bulbs.add(LifxBulb.findBulb(id)));
        return bulbs;
    }
}
