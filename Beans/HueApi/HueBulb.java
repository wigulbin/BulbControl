package com.augment.golden.bulbcontrol.Beans.HueApi;

import com.augment.golden.bulbcontrol.Beans.SmartBulb;

public class HueBulb extends SmartBulb {

    private double xy;


    public HueBulb(){
        super();
    }
    public HueBulb(String id){
        super(id);
    }
    public HueBulb(String id, String name){
        super (id, name);
    }


}
