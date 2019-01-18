package com.augment.golden.bulbcontrol.Activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.augment.golden.bulbcontrol.Adapters.SmartBulbListAdapter;
import com.augment.golden.bulbcontrol.AsyncTasks.BulbTask;
import com.augment.golden.bulbcontrol.Beans.LightInfo;
import com.augment.golden.bulbcontrol.Beans.SmartBulb;
import com.augment.golden.bulbcontrol.R;

import java.util.List;

public class MainActivity extends WearableActivity {

    ListView mListView;
    SmartBulbListAdapter adapter;
    Context mContext;
    Activity mActivity;
    private ProgressBar spinner;
    private boolean on;
    String prev = "#666666";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SmartBulb.clearBulbs(this);

        mActivity = this;
        setContentView(R.layout.activity_main);
        mListView = (ListView) findViewById(R.id.list_view);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(position == 0){
                    //Search network for bulbs
                    spinner.setVisibility(View.VISIBLE);
                    new BulbTask().execute(new LightInfo().getService(adapter, mActivity));
                }else{
                    //Grab bulb information and display
                    SmartBulb bulb = (SmartBulb) parent.getItemAtPosition(position);

                    Intent intent = new Intent(getApplicationContext(), BulbActivity.class);
                    intent.putExtra("mac", bulb.getMac());

                    startActivity(intent);
                }
            }
        });

        spinner = (ProgressBar) findViewById(R.id.progressBar);
        spinner.setVisibility(View.GONE);

        updateUI();
        // Enables Always-on
        setAmbientEnabled();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event){
        if(keyCode == KeyEvent.KEYCODE_STEM_1){
            if(!on)
                setColor("#212121");
            else
                setColor(prev);


            new BulbTask().execute(new LightInfo().changePower(on, ""));
            on = !on;
        }

        return super.onKeyDown(keyCode, event);
    }

    public void updateUI(){
        List<SmartBulb> bulbs = SmartBulb.getAllBulbs(this);
        bulbs.add(new SmartBulb("123456789"));
        bulbs.add(new SmartBulb("123456789"));
        bulbs.add(new SmartBulb("123456789"));
        bulbs.add(new SmartBulb("123456789"));
        adapter = new SmartBulbListAdapter(this, 0, bulbs);
        mListView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    public void setColor(String hex) {
        ImageView lineColorCode = (ImageView)findViewById(R.id.imageView5);
        int color = Color.parseColor(hex); //The color you want
        lineColorCode.setColorFilter(color);
    }
}
