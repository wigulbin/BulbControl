package com.augment.golden.bulbcontrol;

import com.augment.golden.bulbcontrol.Beans.SmartBulb;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BulbGroup {
    private String label = "";
    private List<SmartBulb> bulbs;

    public BulbGroup(){}
    public BulbGroup(String label, List<SmartBulb> bulbs){
        this.label = label;
        this.bulbs = bulbs;
    }

    public static List<BulbGroup> convertBulbsToGroup(List<SmartBulb> bulbs){
        Map<String, List<SmartBulb>> bulbGroupMap = new HashMap<>();
        bulbs.forEach(bulb -> bulbGroupMap.computeIfAbsent(bulb.getGroup(), key -> new ArrayList<>()).add(bulb));

        List<BulbGroup> groups = new ArrayList<>(bulbGroupMap.size());
        bulbGroupMap.forEach((label, list) -> groups.add(new BulbGroup(label, list)));
        return groups;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public List<SmartBulb> getBulbs() {
        return bulbs;
    }

    public void setBulbs(List<SmartBulb> bulbs) {
        this.bulbs = bulbs;
    }
}
