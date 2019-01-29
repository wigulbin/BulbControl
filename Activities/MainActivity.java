package com.augment.golden.bulbcontrol.Activities;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.support.wear.widget.WearableLinearLayoutManager;
import android.support.wear.widget.WearableRecyclerView;
import android.support.wear.widget.drawer.WearableDrawerView;
import android.support.wearable.activity.WearableActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.augment.golden.bulbcontrol.Adapters.SmartBulbListAdapter;
import com.augment.golden.bulbcontrol.AsyncTasks.BulbTask;
import com.augment.golden.bulbcontrol.Beans.LightInfo;
import com.augment.golden.bulbcontrol.Beans.LifxApi.LifxBulb;
import com.augment.golden.bulbcontrol.CustomScrollingLayoutCallback;
import com.augment.golden.bulbcontrol.R;

import java.util.List;

public class MainActivity extends WearableActivity {
    Activity mActivity;
    private ProgressBar spinner;
    private boolean on;
    String prev = "#666666";
    private SmartBulbListAdapter mAdapter;
    private WearableRecyclerView mRecyclerView;
    private RelativeLayout mMainRefresh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LifxBulb.clearBulbs(this);

        mActivity = this;
        setContentView(R.layout.activity_main);
        mRecyclerView = (WearableRecyclerView) findViewById(R.id.list_view);

        mRecyclerView.setEdgeItemsCenteringEnabled(true);

        spinner = (ProgressBar) findViewById(R.id.progressBar);
        spinner.setVisibility(View.GONE);

        mRecyclerView.setLayoutManager(new WearableLinearLayoutManager(this));
        CustomScrollingLayoutCallback customScrollingLayoutCallback = new CustomScrollingLayoutCallback();
        mRecyclerView.setLayoutManager(new WearableLinearLayoutManager(this, customScrollingLayoutCallback));

        List<LifxBulb> bulbs = LifxBulb.getAllBulbs(this);
        updateUI(bulbs);
        mMainRefresh = (RelativeLayout)findViewById(R.id.main_refresh);
        if(bulbs.size() == 0){
            ImageView mainRefresh = (ImageView)findViewById(R.id.main_refresh_button);
            int color = Color.parseColor("#FFFFFF"); //The color you want
            mainRefresh.setColorFilter(color);
            mMainRefresh.setVisibility(View.VISIBLE);
        }else
            mMainRefresh.setVisibility(View.INVISIBLE);


        WearableDrawerView drawerView = (WearableDrawerView) findViewById(R.id.draw_view);
        drawerView.setDrawerContent((FrameLayout) findViewById(R.id.drawer_content));
        drawerView.setPeekContent((LinearLayout) findViewById(R.id.peek_view));

        setRefreshBulbListener();
        setAmbientEnabled();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event){
        if(keyCode == KeyEvent.KEYCODE_STEM_1){

            new BulbTask().execute(new LightInfo().changePower(on, ""));
            on = !on;
        }

        return super.onKeyDown(keyCode, event);
    }

    public void updateUI(List<LifxBulb> bulbs){
        mAdapter = new SmartBulbListAdapter(bulbs, mActivity);
        mRecyclerView.setAdapter(mAdapter);
    }

    public void setColor(String hex) {
        ImageView lineColorCode = (ImageView)findViewById(R.id.imageView5);
        int color = Color.parseColor(hex); //The color you want
        lineColorCode.setColorFilter(color);
    }


    private void setRefreshBulbListener(){
        ImageView.OnClickListener listener = new ImageView.OnClickListener(){
            @Override
            public void onClick(View v) {
                spinner.setVisibility(View.VISIBLE);
                new BulbTask().execute(new LightInfo().getService(mAdapter, mActivity));
                mMainRefresh.setVisibility(View.INVISIBLE);
            }
        };
        ImageView refresh = (ImageView) findViewById(R.id.refresh_bulbs);
        refresh.setOnClickListener(listener);
        ImageView main_refresh = (ImageView) findViewById(R.id.main_refresh_button);
        main_refresh.setOnClickListener(listener);

    }
}