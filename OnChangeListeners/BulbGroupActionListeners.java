package com.augment.golden.bulbcontrol.OnChangeListeners;

import android.view.View;
import android.widget.ImageView;

import com.augment.golden.bulbcontrol.Beans.HueApi.HueBulb;
import com.augment.golden.bulbcontrol.Beans.HueApi.HueBulbGroup;
import com.augment.golden.bulbcontrol.Beans.LifxApi.LifxBulb;
import com.augment.golden.bulbcontrol.BulbAnimations;
import com.augment.golden.bulbcontrol.BulbGroup;

public class BulbGroupActionListeners {

    private BulbGroup group;

    public BulbGroupActionListeners(BulbGroup group){
        this.group = group;
    }



    public View.OnClickListener getBulbImageListener(){
        return new View.OnClickListener(){
            @Override
            public void onClick(View v) {
//                if(group instanceof HueBulbGroup){
//                    LifxBulb lifxBulb = (LifxBulb) bulb;
//                    ImageView imageView = (ImageView) v;
//                    BulbAnimations.bulbPowerAnimation(imageView, lifxBulb);
//                    lifxBulb.setOn(!lifxBulb.isOn());
//                    lifxBulb.changePower(500);
//                }
//                if(bulb instanceof HueBulb){
//                    bulb.setOn(!bulb.isOn());
//                    ((HueBulb) bulb).changeState();
//                }
            }
        };
    }
}
