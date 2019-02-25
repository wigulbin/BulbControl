package com.augment.golden.bulbcontrol.Beans.HueApi;

public class HueWrapper {

    private static RequestManager stateManager(HueBulb bulb){
        HueBridge bridge = HueBridge.getBridge(bulb.getBridgeId());
        String url = bridge.getInternalIpAddress() + "/api/" + bridge.getUsername() + "/lights/" + bulb.getId() + "/state";
        return new RequestManager(url, "PUT");
    }

    public static void changePower(HueBulb bulb){
        RequestManager manager = stateManager(bulb);
        manager.addData("on", bulb.isOn());
        manager.sendData();
    }
    public static void changeBrightness(HueBulb bulb){
        RequestManager manager = stateManager(bulb);
        manager.addData("bri", bulb.getBrightness());
        manager.sendData();
    }
    public static void changeHue(HueBulb bulb){
        RequestManager manager = stateManager(bulb);
        manager.addData("hue", bulb.getHue());
        manager.sendData();
    }
    public static void changeSaturation(HueBulb bulb){
        RequestManager manager = stateManager(bulb);
        manager.addData("sat", bulb.getSaturation());
        manager.sendData();
    }
    public static void changeKelvin(HueBulb bulb){
        RequestManager manager = stateManager(bulb);
        manager.addData("ct", bulb.getKelvin());
        manager.sendData();
    }
    public static void changeState(HueBulb bulb){
        RequestManager manager = stateManager(bulb);
        manager.addData("on", bulb.isOn());
        manager.addData("bri", bulb.getBrightness());
        manager.addData("hue", bulb.getHue());
        manager.addData("sat", bulb.getSaturation());
        manager.sendData();
    }
}
