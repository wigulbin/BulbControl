package com.augment.golden.bulbcontrol.Activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.wear.widget.WearableLinearLayoutManager;
import android.support.wear.widget.WearableRecyclerView;
import android.support.wear.widget.drawer.WearableDrawerView;
import android.support.wearable.activity.WearableActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.augment.golden.bulbcontrol.Adapters.BulbGroupListAdapter;
import com.augment.golden.bulbcontrol.Adapters.SmartBulbListAdapter;
import com.augment.golden.bulbcontrol.AsyncTasks.BulbTask;
import com.augment.golden.bulbcontrol.Beans.HueApi.HueBridge;
import com.augment.golden.bulbcontrol.Beans.HueApi.HueBulb;
import com.augment.golden.bulbcontrol.Beans.HueApi.HueBulbGroup;
import com.augment.golden.bulbcontrol.Beans.HueApi.RequestManager;
import com.augment.golden.bulbcontrol.Beans.LightInfo;
import com.augment.golden.bulbcontrol.Beans.LifxApi.LifxBulb;
import com.augment.golden.bulbcontrol.Beans.SmartBulb;
import com.augment.golden.bulbcontrol.Beans.TaskInfo;
import com.augment.golden.bulbcontrol.BulbGroup;
import com.augment.golden.bulbcontrol.Common;
import com.augment.golden.bulbcontrol.CustomScrollingLayoutCallback;
import com.augment.golden.bulbcontrol.R;
import com.augment.golden.bulbcontrol.Services.UpdateBulbsService;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class MainActivity extends WearableActivity {
    Activity mActivity;
    private SmartBulbListAdapter mAdapter;
    private BulbGroupListAdapter mGroupAdapter;
    private ProgressBar spinner;
    private boolean on;
    private WearableRecyclerView mRecyclerView;
    private RelativeLayout mMainRefresh;
    AtomicInteger mBridgesLeft;
    private boolean search = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LifxBulb.clearBulbs(this);
        Common.clearAll(this);

        mActivity = this;
        setContentView(R.layout.activity_main);
        mRecyclerView =  findViewById(R.id.list_view);
        toggleGroupText();

        mRecyclerView.setEdgeItemsCenteringEnabled(true);

        spinner = findViewById(R.id.progressBar);
        spinner.setVisibility(View.GONE);
        mBridgesLeft = new AtomicInteger(0);
        HueBridge.retrieveBridges(getApplicationContext());
        mRecyclerView.setLayoutManager(new WearableLinearLayoutManager(getApplicationContext()));
        CustomScrollingLayoutCallback customScrollingLayoutCallback = new CustomScrollingLayoutCallback();
        mRecyclerView.setLayoutManager(new WearableLinearLayoutManager(getApplicationContext(), customScrollingLayoutCallback));

        mMainRefresh = findViewById(R.id.main_refresh);
        List<LifxBulb> lifxBulbs = LifxBulb.getLifxBulbs(getApplicationContext());
        List<HueBulb> hueBulbs = HueBulb.getHueBulbs(getApplicationContext());

        List<SmartBulb> bulbs = new ArrayList<>();
        bulbs.addAll(lifxBulbs);
        bulbs.addAll(hueBulbs);

        List<BulbGroup> groups = HueBridge.retrieveGroups(getApplicationContext());
        if(bulbs.size() == 0 && SmartBulb.singleView){
            ImageView mainRefresh = findViewById(R.id.main_refresh_button);
            int color = Color.parseColor("#FFFFFF"); //The color you want
            mainRefresh.setColorFilter(color);
            mMainRefresh.setVisibility(View.VISIBLE);
        }
        else
            mMainRefresh.setVisibility(View.INVISIBLE);

        updateUI(bulbs, groups);
        if(!SmartBulb.singleView)
            mRecyclerView.setAdapter(new BulbGroupListAdapter(groups, this));

        new FindBulbs().execute(new TaskInfo(getApplicationContext(), this, mAdapter, mGroupAdapter));

        setClickListeners();

        if(search){
            HueBridge.findBridges(this);
            search = false;
        }

        WearableDrawerView drawerView = findViewById(R.id.draw_view);

        setRefreshBulbListener();
        setAmbientEnabled();
    }
    private void setClickListeners(){

        LinearLayout groupLayout = findViewById(R.id.group_linear);
        groupLayout.setOnClickListener(v -> {
            TextView text = (TextView) findViewById(R.id.group_linear_text);
            SmartBulb.singleView = !text.getText().toString().equals("Group: Single");
            toggleGroupText();
            finish();
            startActivity(getIntent());
        });

        ImageView connectBridge = findViewById(R.id.connect_bridge);
        connectBridge.setOnClickListener((v) -> {
            List<HueBridge> bridges = HueBridge.retrieveBridges(getApplicationContext());
            mBridgesLeft = new AtomicInteger(bridges.size());
            for (HueBridge bridge : bridges)
                new HueApiTask().execute(bridge.getInternalIpAddress() + "/api", "POST", bridge.getId());
        });
    }

    private void toggleGroupText(){
        TextView text = (TextView) findViewById(R.id.group_linear_text);
        if(!SmartBulb.singleView)
            text.setText("Group: Multi");
        else
            text.setText("Group: Single");

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event){
        if(keyCode == KeyEvent.KEYCODE_STEM_1){

            new BulbTask().execute(new LightInfo().changePower(on, ""));
            on = !on;
        }

        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public void updateUI(List<SmartBulb> bulbs, List<BulbGroup> groups){
        mGroupAdapter = new BulbGroupListAdapter(groups, mActivity);
        mAdapter = new SmartBulbListAdapter(bulbs, mActivity);
        if(SmartBulb.singleView)
            mRecyclerView.setAdapter(mAdapter);
        else
            mRecyclerView.setAdapter(mGroupAdapter);
        mGroupAdapter.notifyDataSetChanged();
    }

    public void setColor(String hex) {
//        ImageView lineColorCode = findViewById(R.id.imageView5);
//        int color = Color.parseColor(hex); //The color you want
//        lineColorCode.setColorFilter(color);
    }


    private void setRefreshBulbListener(){
        ImageView.OnClickListener listener = (test) -> {
            spinner.setVisibility(View.VISIBLE);
            new FindBulbs().execute(new TaskInfo(getApplicationContext(), this, mAdapter, mGroupAdapter));
            mMainRefresh.setVisibility(View.INVISIBLE);
        };

        ImageView refresh = findViewById(R.id.refresh_bulbs);
        refresh.setOnClickListener(listener);

        ImageView main_refresh = findViewById(R.id.main_refresh_button);
        main_refresh.setOnClickListener(listener);

    }


    private static class FindBulbs extends AsyncTask<TaskInfo, Void, List<SmartBulb>> {
        private WeakReference<Context> mContext;
        private WeakReference<Activity> mActivity;
        private SmartBulbListAdapter mAdapter;
        private BulbGroupListAdapter mGroupAdapter;


        @Override
        protected List<SmartBulb> doInBackground(TaskInfo... params) {
            mContext = new WeakReference<>(params[0].getContext());
            mActivity = new WeakReference<>(params[0].getActivity());
            mAdapter = params[0].getAdapter();
            mGroupAdapter = params[0].getGroupAdapter();
            List<SmartBulb> bulbs = new ArrayList<>();
            bulbs.addAll(LifxBulb.findAllBulbs(mAdapter, mContext.get()));
            bulbs.addAll(HueBridge.findAllBulbs(mContext.get()));
            List<HueBridge> bridges = HueBridge.retrieveBridges(mContext.get());
            bridges.forEach(bridge ->  bridge.findAndSaveGroups(mContext.get()));
            mGroupAdapter.addAll(HueBridge.retrieveGroups(mContext.get()));

            return bulbs;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(List<SmartBulb> bulbs) {
            ProgressBar spinner = mActivity.get().findViewById(R.id.progressBar);
            if(spinner != null)
                spinner.setVisibility(View.GONE);
//            mAdapter.removeAll(bulbs);
            mAdapter.addAll(bulbs);
        }
    }

    public class HueApiTask extends AsyncTask<String, String, String> {

        public HueApiTask(){
            //set context variables if required
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            if(params.length >= 3) {
                RequestManager manager = new RequestManager(params[0], params[1]);
                manager.addData("devicetype", "my_hue_app#android will");
                try{
                    JSONObject json = new JSONArray(manager.sendData()).getJSONObject(0);
                    if(json != null){
                        if(json.has("error")) {
                            JSONObject error = json.getJSONObject("error");
                            return error.getString("description");
                        }
                        if(json.has("success")){
                            String username = json.getJSONObject("success").getString("username");
                            HueBridge bridge = HueBridge.retrieveBridge(params[2], getApplicationContext());
                            if(bridge != null)
                            {
                                bridge.setUsername(username);
                                bridge.save(getApplicationContext());
                            }
                            return "Bridge Linked";
                        }
                    }
                } catch (Exception e){
                    e.printStackTrace();
                }
            }

            return "";
        }

        @Override
        protected void onPostExecute(String message) {
            mBridgesLeft.getAndDecrement();
            if(message.length() > 0 && mBridgesLeft.get() == 0){
                Toast toast = Toast.makeText(getApplicationContext(),
                        message,
                        Toast.LENGTH_SHORT);

                toast.show();
            }
        }
    }

}
