package com.augment.golden.bulbcontrol.Beans.HueApi;

import com.augment.golden.bulbcontrol.Beans.SmartBulb;

public class HueBulb extends SmartBulb {

    private double xy;
    private String bridgeId = "";

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
}
