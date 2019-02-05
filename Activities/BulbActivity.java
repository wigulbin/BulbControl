package com.augment.golden.bulbcontrol.Activities;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.wear.widget.drawer.WearableNavigationDrawerView;
import android.support.wearable.activity.WearableActivity;
import android.support.wearable.input.RotaryEncoder;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.augment.golden.bulbcontrol.Beans.LifxApi.LifxBulb;
import com.augment.golden.bulbcontrol.Beans.LightInfo;
import com.augment.golden.bulbcontrol.R;
import com.augment.golden.bulbcontrol.SectionFragment;

import java.util.concurrent.atomic.AtomicInteger;

public class BulbActivity extends WearableActivity {

    private TextView mTextView;
    private boolean on = false;
    private AtomicInteger currentBrightness = new AtomicInteger(55);
    private String bulbHex;
    String prev = "#666666";
    private SeekBar mSeekBar;
    private ImageView mImage;
    private LifxBulb m_bulb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bulb);

        mTextView = findViewById(R.id.text);

        Bundle extras = getIntent().getExtras();
        if(extras == null){
            setColor("#666666");
        }else{
            String value1 = extras.getString("mac");
            if(value1 != null){
                bulbHex = value1;
                m_bulb = LifxBulb.findBulb(bulbHex);
            }
        }

        WearableNavigationDrawerView nav = findViewById(R.id.top_navigation_drawer);
        nav.setAdapter(new com.augment.golden.bulbcontrol.Adapters.NavigationAdapter(this));
        nav.addOnItemSelectedListener((i) -> {
            SectionFragment.handleClick(i, this, bulbHex);
            finish();
        });
        nav.getController().peekDrawer();

        mSeekBar = findViewById(R.id.simpleSeekBar);
        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progressChangedValue = 0;

            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            }

            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            public void onStopTrackingTouch(SeekBar seekBar) {
                progressChangedValue = seekBar.getProgress();
                LightInfo info = new LightInfo(bulbHex);
                currentBrightness.set(progressChangedValue);
                changeBrightness();
                new UpdateBulb().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, info.changeBrightness(currentBrightness, bulbHex));
            }
        });

        mImage = findViewById(R.id.imageView5);
        mImage.setOnClickListener((v) -> togglePower());

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
            new UpdateBulb().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, info.changeBrightness(currentBrightness, bulbHex));
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
        new UpdateBulb().execute(info.changePower(on, bulbHex));
        if(!on)
            setColor("#212121");
        else
            setColor(prev);

        on = !on;
    }

    public void changeBrightness(){
        int brightness = currentBrightness.get();
        int num = (int) ((brightness/65535f) * 15);

        m_bulb.setBrightness((brightness));

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
        ImageView lineColorCode = findViewById(R.id.imageView5);
        int color = Color.parseColor(hex); //The color you want
        lineColorCode.setColorFilter(color);
    }




    private final class NavigationAdapter extends WearableNavigationDrawerView.WearableNavigationDrawerAdapter{
        @Override
        public CharSequence getItemText(int i) {
            return null;
        }

        @Override
        public Drawable getItemDrawable(int i) {
            return null;
        }

        @Override
        public int getCount() {
            return 0;
        }

        @Override
        public void notifyDataSetChanged() {
            super.notifyDataSetChanged();
        }
    }

    private static class UpdateBulb extends AsyncTask<LightInfo, Void, Void> {
        @Override
        protected Void doInBackground(LightInfo... params) {
            LightInfo info = params[0];

            if(info.getMacAddress().length() > 0)
            {
                if(info.isChangePower())
                    LifxBulb.findBulb(info.getMacAddress()).changePower(info.isOnOrOff(), 500);

                if(info.isChangeBrightness() && info.getCurrentBrightness().intValue() > -1)
                    LifxBulb.findBulb(info.getMacAddress()).changeHSBK();
            }

            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
        }
    }
}
