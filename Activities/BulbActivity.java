package com.augment.golden.bulbcontrol.Activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.support.wearable.input.RotaryEncoder;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.augment.golden.bulbcontrol.AsyncTasks.BulbTask;
import com.augment.golden.bulbcontrol.Beans.LightInfo;
import com.augment.golden.bulbcontrol.Beans.SmartBulb;
import com.augment.golden.bulbcontrol.R;

import java.util.concurrent.atomic.AtomicInteger;

public class BulbActivity extends WearableActivity {

    private TextView mTextView;
    private boolean on = false;
    private AtomicInteger currentBrightness = new AtomicInteger(55);
    private String bulbHex;
    String prev = "#666666";
    private SeekBar mSeekBar;
    private ImageView mImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bulb);

        mTextView = (TextView) findViewById(R.id.text);

        Bundle extras = getIntent().getExtras();
        if(extras == null){
            setColor("#666666");
        }else{
            String value1 = extras.getString("mac");
            if(value1 != null){
                bulbHex = value1;
                SmartBulb bulb = SmartBulb.getBulb(value1, this);
            }
        }


        mSeekBar = (SeekBar) findViewById(R.id.simpleSeekBar);
        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progressChangedValue = 0;

            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progressChangedValue = progress;
                LightInfo info = new LightInfo(bulbHex);
                currentBrightness.set(progressChangedValue);
                changeBrightness();
                new BulbTask().execute(info.changeBrightness(currentBrightness, bulbHex));
            }

            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            public void onStopTrackingTouch(SeekBar seekBar) {
//                Toast.makeText(BulbActivity.this, "Seek bar progress is :" + progressChangedValue,
//                        Toast.LENGTH_SHORT).show();
            }
        });

        mImage = (ImageView) findViewById(R.id.imageView5);
        mImage.setOnClickListener(new ImageView.OnClickListener(){
            public void onClick(View v){
                togglePower();
            }
        });

        // Enables Always-on
        setAmbientEnabled();
    }
    @Override
    public boolean onGenericMotionEvent(MotionEvent ev){
        if(ev.getAction() == MotionEvent.ACTION_SCROLL && RotaryEncoder.isFromRotaryEncoder(ev)){
            float delta = -RotaryEncoder.getRotaryAxisValue(ev);
            currentBrightness.addAndGet((int)(1200 * delta));
            changeBrightness();
            LightInfo info = new LightInfo(bulbHex);
            mSeekBar.setProgress(currentBrightness.get());
            new BulbTask().execute(info.changeBrightness(currentBrightness, bulbHex));
        }

        return super.onGenericMotionEvent(ev);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event){
        if(keyCode == KeyEvent.KEYCODE_STEM_1)
            togglePower();

        return super.onKeyDown(keyCode, event);
    }

    private void togglePower(){
        LightInfo info = new LightInfo(bulbHex);
        info.changePower(on, bulbHex);
        new BulbTask().execute(info);
        if(!on)
            setColor("#212121");
        else
            setColor(prev);

        on = !on;
    }

    public void changeBrightness(){
        int brightness = currentBrightness.get();
        int num = (int) ((brightness/65535f) * 15);


        if(num == 1 || num == 0)
            prev = ("#111111");
        if(num == 1 || num == 0 || num == 2)
            prev = ("#333333");
        if(num == 3)
            prev = ("#555555");
        if(num == 4)
            prev = ("#666666");
        if(num == 5)
            prev = ("#777777");
        if(num == 6)
            prev = ("#888888");
        if(num == 7)
            prev = ("#999999");
        if(num == 8)
            prev = ("#AAAAAA");
        if(num == 9)
            prev = ("#ADADAD");
        if(num == 10)
            prev = ("#BBBBBB");
        if(num == 11)
            prev = ("#BDBDBD");
        if(num == 12)
            prev = ("#CCCCCC");
        if(num == 13)
            prev = ("#DDDDDD");
        if(num == 14)
            prev = ("#EEEEEE");
        if(num == 15)
            prev = ("#FFFFFF");

        setColor(prev);
    }

    public void setColor(String hex) {
        ImageView lineColorCode = (ImageView)findViewById(R.id.imageView5);
        int color = Color.parseColor(hex); //The color you want
        lineColorCode.setColorFilter(color);
    }
}
