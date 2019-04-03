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
import com.augment.golden.bulbcontrol.Beans.SmartBulb;
import com.augment.golden.bulbcontrol.BulbPagerIndicatorDecoration;
import com.augment.golden.bulbcontrol.Changeable;
import com.augment.golden.bulbcontrol.R;

public class BulbActivity extends WearableActivity {

    private SmartBulb m_bulb;
    private Changeable changeable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bulb_warm);
        Bundle extras = getIntent().getExtras();
        if(extras != null){
            String id = extras.getString("id");
            String type = extras.getString("type");
            if(type.equals("lifx")) changeable = LifxBulb.findBulb(id);
            if(type.equals("hue")) changeable = HueBulb.findBulb(id);
            if(type.equals("hueGroup")) changeable = HueBulbGroup.retrieveGroup(id);
            if(type.equals("lifxGroup")) changeable = LifxBulbGroup.retrieveGroup(id);
        }

        RecyclerView recyclerView = findViewById(R.id.horizonal_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(new BulbPagerIndicatorDecoration());
        LinearLayoutManager manager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        manager.setReverseLayout(true);
        manager.setStackFromEnd(true);
        recyclerView.setLayoutManager(manager);
//        recyclerView.setAdapter(new BulbActionAdapter(m_bulb, this));
        recyclerView.setAdapter(new ChangeableActionAdapter(changeable, this));

        new PagerSnapHelper().attachToRecyclerView(recyclerView);

        // Enables Always-on
        setAmbientEnabled();
    }

}
