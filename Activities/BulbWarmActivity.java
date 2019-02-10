package com.augment.golden.bulbcontrol.Activities;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PagerSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.support.wearable.activity.WearableActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.augment.golden.bulbcontrol.Adapters.BulbActionAdapter;
import com.augment.golden.bulbcontrol.Beans.LifxApi.LifxBulb;
import com.augment.golden.bulbcontrol.Beans.SmartBulb;
import com.augment.golden.bulbcontrol.BulbPagerIndicatorDecoration;
import com.augment.golden.bulbcontrol.R;

public class BulbWarmActivity extends WearableActivity {

    private TextView mTextView;
    private SmartBulb bulb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bulb_warm);
        Bundle extras = getIntent().getExtras();
        if(extras != null){
            String value1 = extras.getString("mac");
            if(value1 != null){
                String bulbHex = value1;
                bulb = LifxBulb.findBulb(bulbHex);
            }
        }

        RecyclerView recyclerView = findViewById(R.id.horizonal_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(new BulbPagerIndicatorDecoration());
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerView.setAdapter(new BulbActionAdapter(bulb, this));


        new PagerSnapHelper().attachToRecyclerView(recyclerView);

        // Enables Always-on
        setAmbientEnabled();
    }


}
