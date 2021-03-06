package com.augment.golden.bulbcontrol.OnChangeListeners;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.support.constraint.ConstraintLayout;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;

import com.augment.golden.bulbcontrol.Beans.LifxApi.LifxBulb;
import com.augment.golden.bulbcontrol.BulbAnimations;
import com.augment.golden.bulbcontrol.Changeable;
import com.augment.golden.bulbcontrol.KelvinTable;
import com.larswerkman.holocolorpicker.ColorPicker;
import com.larswerkman.holocolorpicker.SaturationBar;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicInteger;

public class ChangeableActionListeners {
    Changeable changeable;


    public ChangeableActionListeners(Changeable changeable){
        this.changeable = changeable;
    }

    public SeekBar.OnSeekBarChangeListener getWarmthSeekBarChangeListener(){
        AtomicInteger prevProg = new AtomicInteger();
        TimerWrapper wrapper = new TimerWrapper() {
            @Override
            public void doUpdate(int kelvin, int number2) {
                changeable.setKelvin(kelvin);
                changeable.changeKelvin();
            }
        };
        return new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int newProg = (((progress) + 99) / 100 ) * 100;
                if(newProg != prevProg.get())
                {
                    prevProg.set(newProg);
                    int kelvin = 0;
                    String hex;
                    if(changeable instanceof LifxBulb){
                        hex = KelvinTable.getRGB(newProg + 2500);
                        kelvin = progress + 2500;
                        changeable.setSaturation(0);
                    } else{
                        hex = KelvinTable.getRGB(newProg + 2000);
                        kelvin = (347-progress) + 153;
                    }

                    seekBar.getProgressDrawable().setColorFilter(Color.parseColor(hex), PorterDuff.Mode.SRC_IN);
                    seekBar.getThumb().setColorFilter(Color.parseColor(hex), PorterDuff.Mode.SRC_IN);
                    wrapper.reset(kelvin, 0);
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

    //Prevent screen from moving on touch
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
        AtomicInteger prevBright = new AtomicInteger();

        TimerWrapper wrapper = new TimerWrapper() {
            @Override
            public void doUpdate(int number, int number2) {
                changeable.setBrightness(number);
                changeable.changeBrightness();
            }
        };
        return new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int newProg = ((progress + 99) / 100 ) * 100;
                if(newProg != prevProg.get())
                {
                    prevProg.set(newProg);
                    int brightness;

                    String brightHex;
                    if(changeable instanceof LifxBulb)
                        brightness = progress;
                    else
                        brightness = (int) ((progress/65535f) * 254);

                    wrapper.reset(brightness, 0);
                    prevBright.set(brightness);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int progressChangedValue = seekBar.getProgress();
                image.setColorFilter(Color.parseColor(changeBrightness((int) (progressChangedValue/65535f * 15))));
//                BulbAnimations.toggleBulbBrightAnimation(image, changeable);
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
                changeable.setOn(!changeable.isOn());
                BulbAnimations.bulbPowerAnimation((ImageView) v, changeable);
                changeable.changePower();
            }
        };
    }

    public ColorPicker.OnColorChangedListener createColorChangeListener() {
        TimerWrapper wrapper = new TimerWrapper() {
            @Override
            public void doUpdate(int hue, int saturation) {
                changeable.setHue(hue);
                changeable.setSaturation(hue);
                changeable.changeHue();
            }
        };
        return new ColorPicker.OnColorChangedListener() {
            @Override
            public void onColorChanged(int color) {
                if(color != 0){
                    float[] hsv = new float[3];
                    Color.RGBToHSV(Color.red(color), Color.green(color), Color.blue(color), hsv);
                    int newColor = (int)((hsv[0]/360) * 65535);
                    int newSat = (changeable instanceof LifxBulb) ? (int)(hsv[1] * 65535) : (int)(hsv[1] * 254);
                    wrapper.reset(newColor, newSat);
                }
            }
        };
    }

    public SaturationBar.OnSaturationChangedListener createSaturationChangeListener() {
        TimerWrapper wrapper = new TimerWrapper() {
            @Override
            public void doUpdate(int newSat, int num2) {
                changeable.setSaturation(newSat);
                changeable.changeState();
            }
        };
        return new SaturationBar.OnSaturationChangedListener() {
            @Override
            public void onSaturationChanged(int color) {
                if(color != 0){
                    float[] hsv = new float[3];
                    Color.RGBToHSV(Color.red(color), Color.green(color), Color.blue(color), hsv);
                    int newSat = changeable instanceof LifxBulb ? (int)(hsv[1] * 65535) : (int)(hsv[1] * 254);
                    wrapper.reset(newSat, 0);
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



    private abstract class TimerWrapper{
        TimerTask timerTask;
        Timer timer = new Timer();
        AtomicInteger current;
        AtomicInteger current2;

        public TimerWrapper(){
            current = new AtomicInteger();
            current2 = new AtomicInteger();
            timer = new Timer();
            timerTask = new TimerTask() {
                @Override
                public void run() {
                    timer.cancel();
                }
            };
        }

        abstract public void doUpdate(int number, int number2);


        public void reset(int number, int number2){
            timer.cancel();
            timerTask.cancel();
            current.set(number);
            current2.set(number2);
            timer = new Timer();
            timerTask = new TimerTask() {
                @Override
                public void run() {
                    doUpdate(current.intValue(), current2.intValue());
                    timer.cancel();
                }
            };
            timer.schedule(timerTask, 500);
        }

    }
}
