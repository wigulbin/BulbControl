package com.augment.golden.bulbcontrol.Beans;

import android.app.Activity;

import com.augment.golden.bulbcontrol.Adapters.BulbGroupListAdapter;
import com.augment.golden.bulbcontrol.Adapters.SmartBulbListAdapter;

public class TaskInfo {
    private Activity activity;
    private SmartBulbListAdapter adapter;
    private BulbGroupListAdapter groupAdapter;

    public TaskInfo(Activity activity, SmartBulbListAdapter adapter, BulbGroupListAdapter groupAdapter) {
        this.activity = activity;
        this.adapter = adapter;
        this.groupAdapter = groupAdapter;
    }

    public Activity getActivity() {
        return activity;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    public SmartBulbListAdapter getAdapter() {
        return adapter;
    }
    public BulbGroupListAdapter getGroupAdapter() {
        return groupAdapter;
    }

    public void setGroupAdapter(BulbGroupListAdapter groupAdapter) {
        this.groupAdapter = groupAdapter;
    }

    public void setAdapter(SmartBulbListAdapter adapter) {
        this.adapter = adapter;
    }
}
