package com.augment.golden.bulbcontrol.OnChangeListeners;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.graphics.ColorUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;

import com.augment.golden.bulbcontrol.Beans.HueApi.HueBulb;
import com.augment.golden.bulbcontrol.Beans.LifxApi.LifxBulb;
import com.augment.golden.bulbcontrol.Beans.SmartBulb;
import com.augment.golden.bulbcontrol.BulbAnimations;
import com.augment.golden.bulbcontrol.KelvinTable;
import com.larswerkman.holocolorpicker.ColorPicker;
import com.larswerkman.holocolorpicker.SaturationBar;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class BulbActionListeners {

    private SmartBulb bulb;

    public BulbActionListeners(SmartBulb bulb){
        this.bulb = bulb;
    }

    public SeekBar.OnSeekBarChangeListener getWarmthSeekBarChangeListener(){
        AtomicInteger prevProg = new AtomicInteger();
        return new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int newProg = (((progress) + 99) / 100 ) * 100;
                if(newProg != prevProg.get())
                {
                    prevProg.set(newProg);
                    if(bulb instanceof LifxBulb){
                        String hex = KelvinTable.getRGB(newProg + 2500);
                        seekBar.getProgressDrawable().setColorFilter(Color.parseColor(hex), PorterDuff.Mode.SRC_IN);
                        seekBar.getThumb().setColorFilter(Color.parseColor(hex), PorterDuff.Mode.SRC_IN);
                        LifxBulb lifxBulb = (LifxBulb) bulb;
                        lifxBulb.setKelvin(progress + 2500);
                        lifxBulb.setSaturation(0);
                        lifxBulb.changeHsbk();
                    }
                    if(bulb instanceof HueBulb){
                        String hex = KelvinTable.getRGB(newProg + 2000);
//                        seekBar.getProgressDrawable().setColorFilter(Color.parseColor(hex), PorterDuff.Mode.SRC_IN);
//                        seekBar.getThumb().setColorFilter(Color.parseColor(hex), PorterDuff.Mode.SRC_IN);
                        HueBulb hueBulb = (HueBulb) bulb;
                        hueBulb.setKelvin((347-progress) + 153);
                        hueBulb.changeKelvin();
                    }
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        };
    }

    public View.OnTouchListener getSeekBarTouchListener(ConstraintLayout layout){
        return new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                layout.requestDisallowInterceptTouchEvent(true);
                return false;
            }
        };
    }




    public SeekBar.OnSeekBarChangeListener getBrightnessSeekBarChangeListener(ImageView image){
        AtomicInteger prevProg = new AtomicInteger();
        return new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int newProg = ((progress + 99) / 100 ) * 100;
                if(newProg != prevProg.get())
                {
                    prevProg.set(newProg);

                    if(bulb instanceof LifxBulb) {
                        LifxBulb lifxBulb = (LifxBulb) bulb;
                        lifxBulb.setBrightness(progress);
                        lifxBulb.changeHsbk();
                    }
                    if(bulb instanceof HueBulb) {
                        HueBulb hueBulb = (HueBulb) bulb;
                        int brightness = (int) ((progress/65535f) * 254);
                        hueBulb.setBrightness(brightness);
                        hueBulb.changeState();
                    }
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int progressChangedValue = seekBar.getProgress();
                image.setColorFilter(Color.parseColor(changeBrightness((int)((progressChangedValue/65535f) * 15))));
            }
        };
    }

    public String changeBrightness(int num){
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


    public View.OnClickListener getBulbImageListener(){
        return new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(bulb instanceof LifxBulb){
                    LifxBulb lifxBulb = (LifxBulb) bulb;
                    ImageView imageView = (ImageView) v;
//                    BulbAnimations.bulbPowerAnimation(imageView, lifxBulb);
                    lifxBulb.setOn(!lifxBulb.isOn());
                    lifxBulb.changePower();
                }
                if(bulb instanceof HueBulb){
                    bulb.setOn(!bulb.isOn());
                    ((HueBulb) bulb).changeState();
                }
            }
        };
    }

    public ColorPicker.OnColorChangedListener createColorChangeListener() {
        AtomicInteger prevHue = new AtomicInteger();
        return new ColorPicker.OnColorChangedListener() {
            @Override
            public void onColorChanged(int color) {
                if(color != 0){
                    float[] hsv = new float[3];
                    Color.RGBToHSV(Color.red(color), Color.green(color), Color.blue(color), hsv);

                    int differenceH = getDifference(prevHue.get(), (int)hsv[0]);

                    if(differenceH > 10)
                    {
                        int newColor = (int)((hsv[0]/360) * 65535);
                        int newSat = (int)(hsv[1] * 65535);

                        prevHue.set(newColor);

                        if(bulb instanceof LifxBulb){
                            LifxBulb lifxBulb = (LifxBulb) bulb;
                            lifxBulb.setHue(newColor);
                            lifxBulb.setSaturation(newSat);
                            lifxBulb.changeHsbk();
                        }
                        if(bulb instanceof HueBulb){
                            HueBulb hueBulb = (HueBulb) bulb;
                            hueBulb.setHue(newColor);
                            hueBulb.setSaturation((int)(hsv[1] * 254));
                            hueBulb.changeState();
                        }
                    }
                }
            }
        };
    }

    public SaturationBar.OnSaturationChangedListener createSaturationChangeListener() {
        AtomicLong prevSaturation = new AtomicLong();
        return new SaturationBar.OnSaturationChangedListener() {
            @Override
            public void onSaturationChanged(int color) {
                if(color != 0){
                    float[] hsv = new float[3];
                    Color.RGBToHSV(Color.red(color), Color.green(color), Color.blue(color), hsv);
                    if(hsv[1] - prevSaturation.get() > 0.02)
                    {
                        prevSaturation.set((long)hsv[1]);

                        if(bulb instanceof LifxBulb){
                            int newSat = (int)(hsv[1] * 65535);
                            LifxBulb lifxBulb = (LifxBulb) bulb;
                            lifxBulb.setSaturation(newSat);
                            lifxBulb.changeHsbk();
                        }
                        if(bulb instanceof HueBulb){
                            int newSat = (int)(hsv[1] * 254);
                            HueBulb hueBulb = (HueBulb) bulb;
                            hueBulb.setSaturation(newSat);
                            hueBulb.changeState();
                        }
                    }
                }
            }
        };
    }

    final int RED_NUM_MIN = 0;
    final int RED_NUM_MAX = 65535;
    final int GREEN_NUM = 21845;
    final int BLUE_NUM = 43690;
    public int convert(int red, int green, int blue){
        int num = 0;
        if(blue == 0){
            if(red == 255)
                num = RED_NUM_MIN + calculateColorNum(green);
            if(green == 255)
                num = GREEN_NUM - calculateColorNum(red);
        }
        if(red == 0){
            if(green == 255)
                num = GREEN_NUM + calculateColorNum(blue);
            if(blue == 255)
                num = BLUE_NUM - calculateColorNum(green);
        }
        if(green == 0){
            if(blue == 255)
                num = BLUE_NUM + calculateColorNum(red);
            if(red == 255)
                num = RED_NUM_MAX - calculateColorNum(blue);
        }

        return num;
    }

    private int calculateColorNum(int color){
        return (int)((color/255f) * 10922);
    }
    private int getDifference(int x, int y){
        return Math.max(x, y) - Math.min(x, y);
    }
}
