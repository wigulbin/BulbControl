package com.augment.golden.bulbcontrol;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.graphics.Color;
import android.widget.ImageView;

import com.augment.golden.bulbcontrol.Beans.LifxApi.LifxBulb;
import com.augment.golden.bulbcontrol.Beans.SmartBulb;

public class BulbAnimations {


    public static void bulbPowerAnimation(ImageView imageView, Changeable changeable){
        String hex = changeBrightness(changeable.getBrightness()/changeable.retrieveBrightMax() * 15);
        boolean on = changeable.isOn();

        if(!on) hex = "#222222";
        int colorFrom = Color.parseColor(hex);

        int colorTo = 0;
        if(!on)
            colorTo = Color.parseColor("#222222");
        else
            colorTo = Color.parseColor(hex);

        ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);
        colorAnimation.setDuration(500); // milliseconds
        colorAnimation.addUpdateListener(animator -> imageView.setColorFilter((int) animator.getAnimatedValue()));
        colorAnimation.start();
    }

    public static void toggleBulbBrightAnimation(ImageView image, Changeable changeable){
        image.setColorFilter(Color.parseColor(changeBrightness(changeable.getBrightness()/changeable.retrieveBrightMax() * 15)));
    }

    public static String changeBrightness(int num){
        String hex = "";

        if(num == 1 || num == 0)
            hex = ("#333333");
        if(num == 1 || num == 0 || num == 2)
            hex = ("#444444");
        if(num == 3)
            hex = ("#555555");
        if(num == 4)
            hex = ("#666666");
        if(num == 5)
            hex = ("#777777");
        if(num == 6)
            hex = ("#888888");
        if(num == 7)
            hex = ("#999999");
        if(num == 8)
            hex = ("#AAAAAA");
        if(num == 9)
            hex = ("#ADADAD");
        if(num == 10)
            hex = ("#BBBBBB");
        if(num == 11)
            hex = ("#BDBDBD");
        if(num == 12)
            hex = ("#CCCCCC");
        if(num == 13)
            hex = ("#DDDDDD");
        if(num == 14)
            hex = ("#EEEEEE");
        if(num == 15)
            hex = ("#FFFFFF");

        return hex; //The color you want
    }
}
