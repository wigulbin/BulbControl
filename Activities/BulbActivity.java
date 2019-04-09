package com.augment.golden.bulbcontrol.Activities;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PagerSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.support.wearable.activity.WearableActivity;

import com.augment.golden.bulbcontrol.Adapters.ChangeableActionAdapter;
import com.augment.golden.bulbcontrol.Beans.HueApi.HueBulb;
import com.augment.golden.bulbcontrol.Beans.HueApi.HueBulbGroup;
import com.augment.golden.bulbcontrol.Beans.LifxApi.LifxBulb;
import com.augment.golden.bulbcontrol.Beans.LifxApi.LifxBulbGroup;
import com.augment.golden.bulbcontrol.BulbPagerIndicatorDecoration;
import com.augment.golden.bulbcontrol.Changeable;
import com.augment.golden.bulbcontrol.Common;
import com.augment.golden.bulbcontrol.R;

public class BulbActivity extends WearableActivity {
    private Changeable changeable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bulb_warm);
        Bundle extras = getIntent().getExtras();
        if(extras != null){
            String id = Common.getSafeString(extras.getString("id"));
            String type = Common.getSafeString(extras.getString("type"));
            getChangeable(id, type);
        }

        // Enables Always-on
        setRecyclerView();
        setAmbientEnabled();
    }

    private Changeable getChangeable(String id, String type){
        if(type.equals("lifx")) changeable = LifxBulb.findBulb(id);
        if(type.equals("hue")) changeable = HueBulb.findBulb(id);
        if(type.equals("hueGroup")) changeable = HueBulbGroup.retrieveGroup(id);
        if(type.equals("lifxGroup")) changeable = LifxBulbGroup.retrieveGroup(id);

        return changeable;
    }

    private void setRecyclerView(){
        RecyclerView recyclerView = findViewById(R.id.horizonal_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(new BulbPagerIndicatorDecoration());
        recyclerView.setLayoutManager(createLayoutManger());
        recyclerView.setAdapter(new ChangeableActionAdapter(changeable, this));

        new PagerSnapHelper().attachToRecyclerView(recyclerView);
    }

    private LinearLayoutManager createLayoutManger(){
        LinearLayoutManager manager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        manager.setReverseLayout(true);
        manager.setStackFromEnd(true);

        return manager;
    }

}
