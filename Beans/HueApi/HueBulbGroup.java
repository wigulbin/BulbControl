package com.augment.golden.bulbcontrol.Beans.HueApi;

import com.augment.golden.bulbcontrol.BulbGroup;
import com.augment.golden.bulbcontrol.Changeable;

import java.util.List;

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
        new Thread(() -> new HueWrapper(group).changePower(group.on)).start();
    }
    public void changeBrightness(){
        HueBulbGroup group = this;
        new Thread(() -> new HueWrapper(group).changeBrightness(group.brightness)).start();
    }
    public void changeHue(){
        HueBulbGroup group = this;
        new Thread(() -> new HueWrapper(group).changeHue(group.hue)).start();
    }
    public void changeSaturation(){
        HueBulbGroup group = this;
        new Thread(() -> new HueWrapper(group).changeSaturation(group.saturation)).start();
    }
    public void changeKelvin(){
        HueBulbGroup group = this;
        new Thread(() -> new HueWrapper(group).changeKelvin(group.kelvin)).start();
    }
    public void changeState(){
        HueBulbGroup group = this;
        new Thread(() -> new HueWrapper(group).changeState(group.on, group.brightness, group.hue, group.saturation)).start();
    }
}
