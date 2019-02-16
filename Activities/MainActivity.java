package com.augment.golden.bulbcontrol.Activities;

import android.app.Activity;
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
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.augment.golden.bulbcontrol.Adapters.SmartBulbListAdapter;
import com.augment.golden.bulbcontrol.AsyncTasks.BulbTask;
import com.augment.golden.bulbcontrol.Beans.HueApi.HueBridge;
import com.augment.golden.bulbcontrol.Beans.HueApi.HueBulb;
import com.augment.golden.bulbcontrol.Beans.HueApi.RequestManager;
import com.augment.golden.bulbcontrol.Beans.LightInfo;
import com.augment.golden.bulbcontrol.Beans.LifxApi.LifxBulb;
import com.augment.golden.bulbcontrol.Beans.SmartBulb;
import com.augment.golden.bulbcontrol.Beans.TaskInfo;
import com.augment.golden.bulbcontrol.CustomScrollingLayoutCallback;
import com.augment.golden.bulbcontrol.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class MainActivity extends WearableActivity {
    Activity mActivity;
    private SmartBulbListAdapter mAdapter;
    private ProgressBar spinner;
    private boolean on;
    private WearableRecyclerView mRecyclerView;
    private RelativeLayout mMainRefresh;
    AtomicInteger mBridgesLeft;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        LifxBulb.clearBulbs(this);

        mActivity = this;
        setContentView(R.layout.activity_main);
        mRecyclerView =  findViewById(R.id.list_view);

        mRecyclerView.setEdgeItemsCenteringEnabled(true);

        spinner = findViewById(R.id.progressBar);
        spinner.setVisibility(View.GONE);
        mBridgesLeft = new AtomicInteger(0);

        mRecyclerView.setLayoutManager(new WearableLinearLayoutManager(this));
        CustomScrollingLayoutCallback customScrollingLayoutCallback = new CustomScrollingLayoutCallback();
        mRecyclerView.setLayoutManager(new WearableLinearLayoutManager(this, customScrollingLayoutCallback));

        List<SmartBulb> bulbs = LifxBulb.getAllBulbsAsSmartBulbs(this);
        updateUI(bulbs);
        mMainRefresh = findViewById(R.id.main_refresh);
        if(bulbs.size() == 0){
            ImageView mainRefresh = findViewById(R.id.main_refresh_button);
            int color = Color.parseColor("#FFFFFF"); //The color you want
            mainRefresh.setColorFilter(color);
            mMainRefresh.setVisibility(View.VISIBLE);
        }
        else
            mMainRefresh.setVisibility(View.INVISIBLE);


        ImageView connectBridge = findViewById(R.id.connect_bridge);
        HueBridge.findBridges(this);
        connectBridge.setOnClickListener((v) -> {
            List<HueBridge> bridges = HueBridge.retrieveBridges(getApplicationContext());
            mBridgesLeft = new AtomicInteger(bridges.size());
            for (HueBridge bridge : bridges)
                new HueApiTask().execute(bridge.getInternalIpAddress() + "/api", "POST", bridge.getId());
        });

        WearableDrawerView drawerView =  findViewById(R.id.draw_view);

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

    @Override
    protected void onResume() {
        super.onResume();
    }

    public void updateUI(List<SmartBulb> bulbs){
        mAdapter = new SmartBulbListAdapter(bulbs, mActivity);
        mRecyclerView.setAdapter(mAdapter);
    }

    public void setColor(String hex) {
//        ImageView lineColorCode = findViewById(R.id.imageView5);
//        int color = Color.parseColor(hex); //The color you want
//        lineColorCode.setColorFilter(color);
    }


    private void setRefreshBulbListener(){
        ImageView.OnClickListener listener = (test) -> {
            spinner.setVisibility(View.VISIBLE);
            new FindBulbs().execute(new TaskInfo(mActivity, mAdapter));
            mMainRefresh.setVisibility(View.INVISIBLE);
        };

        ImageView refresh = findViewById(R.id.refresh_bulbs);
        refresh.setOnClickListener(listener);

        ImageView main_refresh = findViewById(R.id.main_refresh_button);
        main_refresh.setOnClickListener(listener);

    }



    private static class FindBulbs extends AsyncTask<TaskInfo, Void, List<SmartBulb>> {
        private WeakReference<Activity> mActivity;
        private SmartBulbListAdapter mAdapter;


        @Override
        protected List<SmartBulb> doInBackground(TaskInfo... params) {
            mActivity = new WeakReference<>(params[0].getActivity());
            mAdapter = params[0].getAdapter();
            List<SmartBulb> bulbs = new ArrayList<>();
            bulbs.addAll(LifxBulb.findAllBulbs(mAdapter, mActivity.get()));
            bulbs.addAll(HueBridge.findAllBulbs(mActivity.get()));
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
            for (SmartBulb bulb : bulbs) {
                mAdapter.remove(bulb);
                mAdapter.add(bulb);
            }
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
                            HueBridge.findAllBulbs(getApplicationContext());
                            return error.getString("description");
                        }
                        if(json.has("success")){
                            String username = json.getJSONObject("success").getString("username");
                            HueBridge brige = HueBridge.retrieveBridge(params[2], getApplicationContext());
                            brige.setUsername(username);
                            brige.save(getApplicationContext());
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
