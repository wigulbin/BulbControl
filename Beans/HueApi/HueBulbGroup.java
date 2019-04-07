package com.augment.golden.bulbcontrol.Beans.HueApi;

import com.augment.golden.bulbcontrol.Beans.SmartBulb;
import com.augment.golden.bulbcontrol.BulbGroup;
import com.augment.golden.bulbcontrol.Changeable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class HueBulbGroup extends BulbGroup implements Changeable {
    private List<String> lights;
    private String type;
    private String bridgeId;

    private boolean on;
    private int brightness;
    private int hue;
    private int saturation;
    private int kelvin;
    private String effect;

    private final static int brightMax = 254;

    private static Map<String, HueBulbGroup> bulbGroupMap = new ConcurrentHashMap<>();

    public static HueBulbGroup retrieveGroup(String id){
        return bulbGroupMap.get(id);
    }
    public static void addGroup(HueBulbGroup group){
        bulbGroupMap.put(group.getId(), group);
    }

    public HueBulbGroup(String name) {
        super(name);
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
        HueBulbGroup group = this;
        new Thread(() -> new HueWrapper(group).changePower(group.on).send()).start();
    }
    public void changeBrightness(){
        HueBulbGroup group = this;
        new Thread(() -> new HueWrapper(group).changeBrightness(group.brightness).send()).start();
    }
    public void changeHue(){
        HueBulbGroup group = this;
        new Thread(() -> new HueWrapper(group).changeHue(group.hue).send()).start();
    }
    public void changeSaturation(){
        HueBulbGroup group = this;
        new Thread(() -> new HueWrapper(group).changeSaturation(group.saturation).send()).start();
    }
    public void changeKelvin(){
        HueBulbGroup group = this;
        new Thread(() -> new HueWrapper(group).changeKelvin(group.kelvin).send()).start();
    }
    public void changeState(){
        HueBulbGroup group = this;
        new Thread(() -> new HueWrapper(group).changeState(group.on, group.brightness, group.hue, group.saturation)).start();
    }

    @Override
    public void incrementHue(int amount) {
        HueBulbGroup bulb = this;
        bulb.setHue(amount + bulb.getHue());
        new Thread(() -> new HueWrapper(bulb).incrementHue(amount).send()).start();
    }

    @Override
    public void incrementSaturation(int amount) {
        HueBulbGroup bulb = this;
        bulb.setSaturation(amount + bulb.getSaturation());
        new Thread(() -> new HueWrapper(bulb).incrementSaturation(amount).send()).start();
    }

    @Override
    public void incrementKelvin(int amount) {
        HueBulbGroup bulb = this;
        bulb.setKelvin(amount + bulb.getKelvin());
        new Thread(() -> new HueWrapper(bulb).incrementKelvin(amount).send()).start();

    }

    @Override
    public void incrementBrightness(int amount) {
        HueBulbGroup bulb = this;
        bulb.setBrightness(bulb.getBrightness() + amount);
        new Thread(() -> new HueWrapper(bulb).incrementBrightness(amount).send()).start();
    }

    @Override
    public int retrieveBrightMax() {
        return brightMax;
    }

    @Override
    public List<SmartBulb> getBulbs(){
        List<SmartBulb> bulbs = new ArrayList<>(lights.size());
        lights.forEach(id -> bulbs.add(HueBulb.findBulb(id)));
        return bulbs;
    }

}
