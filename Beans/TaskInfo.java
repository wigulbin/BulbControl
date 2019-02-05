package com.augment.golden.bulbcontrol.Beans;

import android.app.Activity;

import com.augment.golden.bulbcontrol.Adapters.SmartBulbListAdapter;

public class TaskInfo {
    private Activity activity;
    private SmartBulbListAdapter adapter;

    public TaskInfo(Activity activity, SmartBulbListAdapter adapter) {
        this.activity = activity;
        this.adapter = adapter;
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

    public void setAdapter(SmartBulbListAdapter adapter) {
        this.adapter = adapter;
    }
}
