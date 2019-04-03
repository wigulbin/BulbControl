package com.augment.golden.bulbcontrol;

import android.content.Context;

import com.augment.golden.bulbcontrol.Beans.HueApi.HueBridge;
import com.augment.golden.bulbcontrol.Beans.LifxApi.LifxBulbGroup;
import com.augment.golden.bulbcontrol.Beans.SmartBulb;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class BulbGroup {
    private List<SmartBulb> bulbs;
    private String name = "";
    private String id = "";

    public BulbGroup(){}
    public BulbGroup(String name, List<SmartBulb> bulbs){
        this.name = name;
        this.bulbs = bulbs;
    }
    public BulbGroup(String name){
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BulbGroup bulbGroup = (BulbGroup) o;
        return Objects.equals(id, bulbGroup.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public static List<BulbGroup> convertBulbsToGroup(List<SmartBulb> bulbs){
        Map<String, List<SmartBulb>> bulbGroupMap = new HashMap<>();
        bulbs.forEach(bulb -> bulbGroupMap.computeIfAbsent(bulb.getGroup(), key -> new ArrayList<>()).add(bulb));

        List<BulbGroup> groups = new ArrayList<>(bulbGroupMap.size());
        bulbGroupMap.forEach((label, list) -> groups.add(new BulbGroup(label, list)));
        return groups;
    }

    public static List<BulbGroup> getAllGroups(Context context)
    {
        List<BulbGroup> groups = HueBridge.retrieveGroups(context);
        groups.addAll(LifxBulbGroup.getLifxGroups(context));

        return groups;
    }


    public List<SmartBulb> getBulbs() {
        return bulbs;
    }

    public void setBulbs(List<SmartBulb> bulbs) {
        this.bulbs = bulbs;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
