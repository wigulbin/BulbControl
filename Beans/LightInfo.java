package com.augment.golden.bulbcontrol.Beans;

import android.app.Activity;
import android.content.Context;
import android.widget.Adapter;
import android.widget.ArrayAdapter;

import com.augment.golden.bulbcontrol.Adapters.SmartBulbListAdapter;

import java.util.concurrent.atomic.AtomicInteger;

public class LightInfo {
    private String macAddress = "";

    private Activity activity;
    private SmartBulbListAdapter adapter;

    private boolean changePower;
    private boolean onOrOff;
    private boolean findService;

    private boolean response;

    private boolean changeBrightness;

    private boolean getBrightness;
    private AtomicInteger currentBrightness;

    public LightInfo(){
    }

    public LightInfo(String mac){
        this.macAddress = mac;
    }

    public LightInfo changePower(boolean onOrOff, String macAddress){
        this.changePower = true;
        this.onOrOff = onOrOff;

        this.macAddress = macAddress;

        return this;
    }

    public LightInfo changeBrightness(AtomicInteger currentBrightness, String macAddress){
        this.changeBrightness = true;
        this.currentBrightness = currentBrightness;
        this.macAddress = macAddress;

        return this;
    }

    public LightInfo getBrightness(AtomicInteger currentBrightness){
        this.getBrightness = true;
        this.currentBrightness = currentBrightness;

        return this;
    }


    public LightInfo getService(SmartBulbListAdapter adapter, Activity activity){
        this.adapter = adapter;
        this.findService = true;
        this.response = true;
        this.activity = activity;

        return this;
    }

    public Activity getActivity() {
        return activity;
    }

    public SmartBulbListAdapter getAdapter() {
        return adapter;
    }

    public boolean isChangePower() {
        return changePower;
    }

    public boolean isOnOrOff() {
        return onOrOff;
    }

    public boolean isFindService() {
        return findService;
    }

    public boolean isResponse() {
        return response;
    }

    public boolean isChangeBrightness() {
        return changeBrightness;
    }

    public boolean isGetBrightness() {
        return getBrightness;
    }

    public AtomicInteger getCurrentBrightness() {
        return currentBrightness;
    }

    public String getMacAddress() {
        return macAddress;
    }

    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
    }
}