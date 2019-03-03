package com.augment.golden.bulbcontrol;

import com.augment.golden.bulbcontrol.Beans.SmartBulb;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BulbGroup {
    private List<SmartBulb> bulbs;
    private String name = "";

    public BulbGroup(){}
    public BulbGroup(String name, List<SmartBulb> bulbs){
        this.name = name;
        this.bulbs = bulbs;
    }
    public BulbGroup(String name){
        this.name = name;
    }

    public static List<BulbGroup> convertBulbsToGroup(List<SmartBulb> bulbs){
        Map<String, List<SmartBulb>> bulbGroupMap = new HashMap<>();
        bulbs.forEach(bulb -> bulbGroupMap.computeIfAbsent(bulb.getGroup(), key -> new ArrayList<>()).add(bulb));

        List<BulbGroup> groups = new ArrayList<>(bulbGroupMap.size());
        bulbGroupMap.forEach((label, list) -> groups.add(new BulbGroup(label, list)));
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
}
