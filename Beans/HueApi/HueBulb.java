package com.augment.golden.bulbcontrol.Beans.HueApi;

import com.augment.golden.bulbcontrol.Beans.LifxApi.LifxBulb;
import com.augment.golden.bulbcontrol.Beans.SmartBulb;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class HueBulb extends SmartBulb {

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

    public String getBridgeId() {
        return bridgeId;
    }

    public void setBridgeId(String bridgeId) {
        this.bridgeId = bridgeId;
    }

    public void changePower(){
        HueBulb bulb = this;
        new Thread(() -> HueWrapper.changePower(bulb)).start();
    }
    public void changeBrightness(){
        HueBulb bulb = this;
        new Thread(() -> HueWrapper.changeBrightness(bulb)).start();
    }
    public void changeHue(){
        HueBulb bulb = this;
        new Thread(() -> HueWrapper.changeHue(bulb)).start();
    }
    public void changeSaturation(){
        HueBulb bulb = this;
        new Thread(() -> HueWrapper.changeSaturation(bulb)).start();
    }
    public void changeKelvin(){
        HueBulb bulb = this;
        new Thread(() -> HueWrapper.changeKelvin(bulb)).start();
    }
    public void changeState(){
        HueBulb bulb = this;
        new Thread(() -> HueWrapper.changeState(bulb)).start();
    }

    public static void addBulb(HueBulb bulb){
        bulbMap.put(bulb.getId(), bulb);
    }

    public static HueBulb findBulb(String id){
        return bulbMap.get(id);
    }
}
