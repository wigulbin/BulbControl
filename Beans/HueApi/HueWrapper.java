package com.augment.golden.bulbcontrol.Beans.HueApi;

public class HueWrapper {
    String url = "";
    RequestManager manager;

    public HueWrapper(HueBulb bulb){
        HueBridge bridge = HueBridge.getBridge(bulb.getBridgeId());
        this.url = bridge.getInternalIpAddress() + "/api/" + bridge.getUsername() + "/lights/" + bulb.getId() + "/state";
        this.manager = new RequestManager(url, "PUT");
    }
    public HueWrapper(HueBulbGroup group){
        HueBridge bridge = HueBridge.getBridge(group.getBridgeId());
        String url = bridge.getInternalIpAddress() + "/api/" + bridge.getUsername() + "/lights/" + group.getName() + "/state";
        this.manager = new RequestManager(url, "PUT");
    }

    public void changePower(boolean on){
        RequestManager manager = this.manager;
        manager.addData("on", on);
    }
    public void changeBrightness(int brightness){
        RequestManager manager = this.manager;
        manager.addData("bri", brightness);
    }
    public void changeHue(int hue){
        RequestManager manager = this.manager;
        manager.addData("hue", hue);
    }
    public void changeSaturation(int saturation){
        RequestManager manager = this.manager;
        manager.addData("sat", saturation);
    }
    public void changeKelvin(int kelvin){
        RequestManager manager = this.manager;
        manager.addData("ct", kelvin);
    }
    public void changeState(boolean on, int brightness, int hue, int saturation){
        changePower(on);
        changeBrightness(brightness);
        changeHue(hue);
        changeSaturation(saturation);
        send();
    }

    public void send(){
        manager.sendData();
    }
}
