package com.augment.golden.bulbcontrol.Beans.HueApi;

public class HueWrapper {

    public static void changePower(HueBulb bulb){
        HueBridge bridge = HueBridge.getBridge(bulb.getBridgeId());
        String url = bridge.getInternalIpAddress() + "/api/" + bridge.getUsername() + "/lights/" + bulb.getId() + "/state";
        RequestManager manager = new RequestManager(url, "PUT");
        manager.addData("on", bulb.isOn());
        manager.sendData();
    }
}
