package com.augment.golden.bulbcontrol.Activities;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.wear.widget.WearableLinearLayoutManager;
import android.support.wear.widget.WearableRecyclerView;
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
import com.augment.golden.bulbcontrol.Beans.HueApi.RequestManager;
import com.augment.golden.bulbcontrol.Beans.LifxApi.LifxBulbGroup;
import com.augment.golden.bulbcontrol.Beans.LightInfo;
import com.augment.golden.bulbcontrol.Beans.LifxApi.LifxBulb;
import com.augment.golden.bulbcontrol.Beans.SmartBulb;
import com.augment.golden.bulbcontrol.Beans.TaskInfo;
import com.augment.golden.bulbcontrol.BulbGroup;
import com.augment.golden.bulbcontrol.CustomScrollingLayoutCallback;
import com.augment.golden.bulbcontrol.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class MainActivity extends WearableActivity {
    Activity m_activity;
    private SmartBulbListAdapter m_bulbAdapter;
    private BulbGroupListAdapter m_groupAdapter;
    private WearableRecyclerView m_recycler;

    private RelativeLayout m_mainRefresh;
    private ProgressBar m_spinner;

    private boolean m_on;
    AtomicInteger m_totalBridges = new AtomicInteger(0);
    private boolean m_search = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        m_activity = this;
        setContentView(R.layout.activity_main);

        setViews();
        new FindBulbs().execute(new TaskInfo(getApplicationContext(), this, m_bulbAdapter, m_groupAdapter));
        updateHueBridges();
        setRefreshBulbListener();
        setAmbientEnabled();
    }

    private void setViews(){
        List<SmartBulb> bulbs = SmartBulb.findSmartBulbs(getApplicationContext());
        List<BulbGroup> groups = BulbGroup.getAllGroups(getApplicationContext());

        setRecyclerView(bulbs, groups);
        setRefreshForSearch(bulbs, groups);
        setClickListeners();
        toggleGroupText();
    }

    private void setRefreshForSearch(List<SmartBulb> bulbs, List<BulbGroup> groups){
        m_mainRefresh = findViewById(R.id.main_refresh);
        m_spinner = findViewById(R.id.progressBar);
        m_spinner.setVisibility(View.GONE);

        if((bulbs.size() == 0 && SmartBulb.singleView) || (groups.size() == 0 && !SmartBulb.singleView)){
            ImageView mainRefresh = findViewById(R.id.main_refresh_button);
            int color = Color.parseColor("#FFFFFF"); //The color you want
            mainRefresh.setColorFilter(color);
            m_mainRefresh.setVisibility(View.VISIBLE);
        }
        else
            m_mainRefresh.setVisibility(View.INVISIBLE);
    }

    private void setRecyclerView(List<SmartBulb> bulbs, List<BulbGroup> groups){
        m_recycler =  findViewById(R.id.list_view);
        m_recycler.setEdgeItemsCenteringEnabled(true);

        CustomScrollingLayoutCallback customScrollingLayoutCallback = new CustomScrollingLayoutCallback();
        WearableLinearLayoutManager manager = new WearableLinearLayoutManager(getApplicationContext(), customScrollingLayoutCallback);
        m_recycler.setLayoutManager(manager);

        if(!SmartBulb.singleView)
            m_recycler.setAdapter(new BulbGroupListAdapter(groups, this));

        updateRecyclerViewAdapter(bulbs, groups);
    }

    public void updateRecyclerViewAdapter(List<SmartBulb> bulbs, List<BulbGroup> groups){
        m_groupAdapter = new BulbGroupListAdapter(groups, m_activity);
        m_bulbAdapter = new SmartBulbListAdapter(bulbs, m_activity);
        if(SmartBulb.singleView)
            m_recycler.setAdapter(m_bulbAdapter);
        else
            m_recycler.setAdapter(m_groupAdapter);
        m_groupAdapter.notifyDataSetChanged();
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
            m_totalBridges = new AtomicInteger(bridges.size());
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
    private void updateHueBridges(){
        HueBridge.retrieveBridges(getApplicationContext());
        if(m_search){
            HueBridge.findBridges(this);
            m_search = false;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event){
        if(keyCode == KeyEvent.KEYCODE_STEM_1){

            new BulbTask().execute(new LightInfo().changePower(m_on, ""));
            m_on = !m_on;
        }

        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }


    private void setRefreshBulbListener(){
        ImageView.OnClickListener listener = (test) -> {
            m_spinner.setVisibility(View.VISIBLE);
            new FindBulbs().execute(new TaskInfo(getApplicationContext(), this, m_bulbAdapter, m_groupAdapter));
            m_mainRefresh.setVisibility(View.INVISIBLE);
        };

        ImageView refresh = findViewById(R.id.refresh_bulbs);
        refresh.setOnClickListener(listener);

        ImageView main_refresh = findViewById(R.id.main_refresh_button);
        main_refresh.setOnClickListener(listener);

    }


    private static class FindBulbs extends AsyncTask<TaskInfo, Void, List<SmartBulb>> {
        private WeakReference<Context> m_context;
        private WeakReference<Activity> m_activity;
        private SmartBulbListAdapter m_adapter;
        private BulbGroupListAdapter m_groupAdapter;


        @Override
        protected List<SmartBulb> doInBackground(TaskInfo... params) {
            m_context = new WeakReference<>(params[0].getContext());
            m_activity = new WeakReference<>(params[0].getActivity());
            m_adapter = params[0].getAdapter();
            m_groupAdapter = params[0].getGroupAdapter();

            List<SmartBulb> bulbs = new ArrayList<>();
            List<LifxBulb> lifxBulbs = LifxBulb.findAllBulbs(m_context.get());
            bulbs.addAll(lifxBulbs);
            bulbs.addAll(HueBridge.findAllBulbs(m_context.get()));

            List<HueBridge> bridges = HueBridge.retrieveBridges(m_context.get());
            bridges.forEach(bridge ->  bridge.findAndSaveGroups(m_context.get()));
            LifxBulbGroup.findAndSaveLifxBulbGroups(lifxBulbs, m_context.get());

            return bulbs;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(List<SmartBulb> bulbs) {
            ProgressBar spinner = m_activity.get().findViewById(R.id.progressBar);
            if(spinner != null)
                spinner.setVisibility(View.GONE);
            m_adapter.addAll(bulbs);
            m_groupAdapter.notifyDataSetChanged();
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
            m_totalBridges.getAndDecrement();
            if(message.length() > 0 && m_totalBridges.get() == 0){
                Toast toast = Toast.makeText(getApplicationContext(),
                        message,
                        Toast.LENGTH_SHORT);

                toast.show();
            }
        }
    }

}
