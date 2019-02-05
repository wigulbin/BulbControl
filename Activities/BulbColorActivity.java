package com.augment.golden.bulbcontrol.Activities;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.wear.widget.drawer.WearableNavigationDrawerView;
import android.support.wearable.activity.WearableActivity;
import android.view.View;
import android.widget.TextView;

import com.augment.golden.bulbcontrol.Beans.LifxApi.LifxBulb;
import com.augment.golden.bulbcontrol.Beans.LightInfo;
import com.augment.golden.bulbcontrol.R;
import com.augment.golden.bulbcontrol.SectionFragment;
import com.larswerkman.holocolorpicker.ColorPicker;
import com.larswerkman.holocolorpicker.SaturationBar;
import com.rtugeek.android.colorseekbar.ColorSeekBar;

import java.math.BigDecimal;
import java.util.concurrent.atomic.AtomicInteger;

public class BulbColorActivity extends WearableActivity {

    private TextView mTextView;
    private LifxBulb m_bulb;
    private ColorPicker m_picker;
    private SaturationBar m_saturation;
    private int m_prevColor = 0;
    private int m_prevHue = 0;
    private int m_prevSaturation = 0;

    private int m_currentRed;
    private int m_currentGreen;
    private int m_currentBlue;

    final int RED_NUM_MIN = 0;
    final int RED_NUM_MAX = 65535;
    final int GREEN_NUM = 21845;
    final int BLUE_NUM = 43690;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bulb_color);

        mTextView = (TextView) findViewById(R.id.text);
        Bundle extras = getIntent().getExtras();
        if(extras != null){
            String value1 = extras.getString("mac");
            if(value1 != null){
                m_bulb = LifxBulb.findBulb(value1);
            }
        }

        WearableNavigationDrawerView nav = findViewById(R.id.top_navigation_drawer);
        nav.setAdapter(new com.augment.golden.bulbcontrol.Adapters.NavigationAdapter(this));
        nav.setCurrentItem(1, false);
        nav.addOnItemSelectedListener((i) -> {
            SectionFragment.handleClick(i, this, m_bulb.getMac());
            finish();
        });
        nav.getController().peekDrawer();

        m_picker = (ColorPicker) findViewById(R.id.picker);
        m_picker.setShowOldCenterColor(false);
        m_picker.setTouchAnywhereOnColorWheelEnabled(true);

        m_saturation = findViewById(R.id.saturationbar);

        m_currentRed = Color.red(m_picker.getColor());
        m_currentGreen = Color.green(m_picker.getColor());
        m_currentBlue = Color.blue(m_picker.getColor());

        new UpdateBulb().execute(new LightInfo().changeColor(0,0,m_bulb.getMac()));

        m_picker.addSaturationBar(m_saturation);
        m_picker.setOnColorChangedListener(new ColorPicker.OnColorChangedListener() {
            @Override
            public void onColorChanged(int color) {
                if(color != 0){
                    int red = Color.red(color);
                    int green = Color.green(color);
                    int blue = Color.blue(color);

                    float[] hsv = new float[3];
                    Color.RGBToHSV(red, green, blue, hsv);

                    int differenceR = getDifference(m_currentRed, red);
                    int differenceG = getDifference(m_currentGreen, green);
                    int differenceB = getDifference(m_currentBlue, blue);

                    int differenceH = getDifference(m_prevHue, (int)hsv[0]);
                    int differenceS = getDifference(m_prevSaturation, (int)hsv[1]);

                    if(differenceH > 100 || differenceS > 100)
                    {
                        m_prevColor = color;
                        m_currentRed = red;
                        m_currentGreen = green;
                        m_currentBlue = blue;
                        int newColor = (int)((hsv[0]/360) * 65535);
                        int newSat = (int)(hsv[1] * 65535);

                        System.out.println("Color: " + newColor);
                        System.out.println("Sat: " + newSat);
                        new UpdateBulb().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new LightInfo().changeColor(newColor, newSat, m_bulb.getMac()));
                    }
                }
            }
        });

        // Enables Always-on
        setAmbientEnabled();
    }

    private int getDifference(int x, int y){
        return Math.max(x, y) - Math.min(x, y);
    }

    private static class UpdateBulb extends AsyncTask<LightInfo, Void, Void> {
        @Override
        protected Void doInBackground(LightInfo... params) {
            LightInfo info = params[0];

            if(info.getMacAddress().length() > 0)
            {
                LifxBulb bulb = LifxBulb.findBulb(info.getMacAddress());
                bulb.setHue(info.getColor());
                bulb.setSaturation(65535);
                bulb.changeHSBK();
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
}
