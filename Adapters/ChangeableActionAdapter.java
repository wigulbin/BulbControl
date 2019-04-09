package com.augment.golden.bulbcontrol.Adapters;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.wear.widget.WearableLinearLayoutManager;
import android.support.wear.widget.WearableRecyclerView;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;

import com.augment.golden.bulbcontrol.Beans.HueApi.HueBulb;
import com.augment.golden.bulbcontrol.Beans.HueApi.HueBulbGroup;
import com.augment.golden.bulbcontrol.BulbGroup;
import com.augment.golden.bulbcontrol.Changeable;
import com.augment.golden.bulbcontrol.CustomScrollingLayoutCallback;
import com.augment.golden.bulbcontrol.OnChangeListeners.ChangeableActionListeners;
import com.augment.golden.bulbcontrol.R;
import com.larswerkman.holocolorpicker.ColorPicker;
import com.larswerkman.holocolorpicker.SaturationBar;

public class ChangeableActionAdapter extends RecyclerView.Adapter {
    private SparseArray<RecyclerView.ViewHolder> holderMap;
    private Activity activity;
    private Changeable changeable;
    private ChangeableActionListeners listeners;

    public ChangeableActionAdapter(Changeable changeable, Activity activity){
        holderMap = new SparseArray<>();
        this.changeable = changeable;
        this.activity = activity;
        listeners = new ChangeableActionListeners(changeable);
    }

    public static class BulbColorActionViewHolder extends RecyclerView.ViewHolder{
        ConstraintLayout constraintLayout;
        ColorPicker colorPicker;
        BulbColorActionViewHolder(ConstraintLayout v){
            super(v);
            constraintLayout = v;
            colorPicker = (ColorPicker) v.getViewById(R.id.bulb_color_picker);
        }
    }
    public static class BulbSaturationActionViewHolder extends RecyclerView.ViewHolder{
        ConstraintLayout constraintLayout;
        SaturationBar saturationBar;
        BulbSaturationActionViewHolder(ConstraintLayout v){
            super(v);
            constraintLayout = v;
            saturationBar = v.findViewById(R.id.bulb_saturation_bar);
        }
    }
    public static class BulbBrightActionViewHolder extends RecyclerView.ViewHolder{
        ConstraintLayout constraintLayout;
        ImageView bulbPower;
        SeekBar bulbBright;
        BulbBrightActionViewHolder(ConstraintLayout v){
            super(v);
            constraintLayout = v;
            bulbPower = (ImageView) v.getViewById(R.id.bulb_power);
            bulbBright = (SeekBar) v.getViewById(R.id.bulb_bright);
        }
    }
    public static class BulbWarmthActionViewHolder extends RecyclerView.ViewHolder{
        ConstraintLayout constraintLayout;
        SeekBar bulbSeek;
        BulbWarmthActionViewHolder(ConstraintLayout v){
            super(v);
            constraintLayout = v;
            bulbSeek = (SeekBar) v.getViewById(R.id.bulb_warm);
        }
    }

    public static class BulbViewHolder extends RecyclerView.ViewHolder{
        ConstraintLayout constraintLayout;
        RecyclerView recyclerView;
        BulbViewHolder(ConstraintLayout v){
            super(v);
            constraintLayout = v;
            recyclerView = (RecyclerView) v.getViewById(R.id.recycler_view);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
        if(viewType == 2) {
            RecyclerView.ViewHolder holder = new ChangeableActionAdapter.BulbColorActionViewHolder((ConstraintLayout) LayoutInflater.from(parent.getContext()).inflate(R.layout.bulb_color, parent, false));
            holderMap.put(viewType, holder);
            return holder;
        }
        if(viewType == 1) {
            RecyclerView.ViewHolder holder = new ChangeableActionAdapter.BulbSaturationActionViewHolder((ConstraintLayout) LayoutInflater.from(parent.getContext()).inflate(R.layout.bulb_saturation, parent, false));
            holderMap.put(viewType, holder);
            return holder;
        }
        if(viewType == 0) {
            RecyclerView.ViewHolder holder = new ChangeableActionAdapter.BulbWarmthActionViewHolder((ConstraintLayout) LayoutInflater.from(parent.getContext()).inflate(R.layout.bulb_warmth, parent, false));
            holderMap.put(viewType, holder);
            return holder;
        }
        if(viewType == 4) {
            ConstraintLayout view = (ConstraintLayout) LayoutInflater.from(parent.getContext()).inflate(R.layout.bulb_recycler, parent, false);
            ChangeableActionAdapter.BulbViewHolder holder = new ChangeableActionAdapter.BulbViewHolder(view);
            return holder;
        }

        RecyclerView.ViewHolder holder = new ChangeableActionAdapter.BulbBrightActionViewHolder((ConstraintLayout) LayoutInflater.from(parent.getContext()).inflate(R.layout.bulb_bright, parent, false));
        holderMap.put(viewType, holder);
        return holder;
    }
    @Override public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {

        if(holder.getItemViewType() == 4) handleBulbListItem(holder);
        if(holder.getItemViewType() == 0) handleWarmthItem(holder);
        if(holder.getItemViewType() == 1) handleSaturationItem(holder);
        if(holder.getItemViewType() == 2) handleColorItem(holder);
        if(holder.getItemViewType() == 3) handleBrightnessItem(holder);
    }

    @Override
    public int getItemCount() {
        if(changeable instanceof BulbGroup)
            return 5;
        return 4;
    }

    private void handleWarmthItem(RecyclerView.ViewHolder holder){
        ChangeableActionAdapter.BulbWarmthActionViewHolder warmthViewHolder = (ChangeableActionAdapter.BulbWarmthActionViewHolder) holder;
        SeekBar seekBar = warmthViewHolder.bulbSeek;
        if(changeable instanceof HueBulb || changeable instanceof HueBulbGroup){
            seekBar.setMax(347);
        }
        seekBar.getThumb().setColorFilter(Color.parseColor("#ffa148"), PorterDuff.Mode.SRC_IN);
        seekBar.getProgressDrawable().setColorFilter(Color.parseColor("#ffa148"), PorterDuff.Mode.SRC_IN);
        seekBar.setOnTouchListener(listeners.getSeekBarTouchListener(warmthViewHolder.constraintLayout));
        seekBar.setOnSeekBarChangeListener(listeners.getWarmthSeekBarChangeListener());
    }
    private void handleSaturationItem(RecyclerView.ViewHolder holder){
        ChangeableActionAdapter.BulbSaturationActionViewHolder satViewHolder = (ChangeableActionAdapter.BulbSaturationActionViewHolder) holder;
        ChangeableActionAdapter.BulbColorActionViewHolder colorViewHolder = (ChangeableActionAdapter.BulbColorActionViewHolder) holderMap.get(2);
        satViewHolder.saturationBar.setOnSaturationChangedListener(listeners.createSaturationChangeListener());

        if(colorViewHolder != null && colorViewHolder.colorPicker != null && satViewHolder.saturationBar != null)
            colorViewHolder.colorPicker.addSaturationBar(satViewHolder.saturationBar);
    }
    private void handleColorItem(RecyclerView.ViewHolder holder){
        ChangeableActionAdapter.BulbColorActionViewHolder colorViewHolder = (ChangeableActionAdapter.BulbColorActionViewHolder) holder;
        colorViewHolder.colorPicker.setShowOldCenterColor(false);
        colorViewHolder.colorPicker.setOnColorChangedListener(listeners.createColorChangeListener());
        ChangeableActionAdapter.BulbSaturationActionViewHolder satViewHolder = (ChangeableActionAdapter.BulbSaturationActionViewHolder) holderMap.get(1);
        if(satViewHolder != null && satViewHolder.saturationBar != null &&  colorViewHolder.colorPicker != null)
            colorViewHolder.colorPicker.addSaturationBar(satViewHolder.saturationBar);
    }
    private void handleBrightnessItem(RecyclerView.ViewHolder holder){
        ChangeableActionAdapter.BulbBrightActionViewHolder brightViewHolder = (ChangeableActionAdapter.BulbBrightActionViewHolder) holder;
        SeekBar seekBar = brightViewHolder.bulbBright;
        seekBar.setOnTouchListener(listeners.getSeekBarTouchListener(brightViewHolder.constraintLayout));
        seekBar.setOnSeekBarChangeListener(listeners.getBrightnessSeekBarChangeListener(brightViewHolder.bulbPower));

        ImageView image = brightViewHolder.bulbPower;
        image.setOnClickListener(listeners.getBulbImageListener());
    }
    private void handleBulbListItem(RecyclerView.ViewHolder holder){
        WearableRecyclerView recyclerView = ((WearableRecyclerView) ((BulbViewHolder) holder).recyclerView);
        BulbGroup group = (BulbGroup) changeable;
        recyclerView.setLayoutManager(new LinearLayoutManager(activity));
        SmartBulbListAdapter adapter = new SmartBulbListAdapter(group.getBulbs(), activity);
        CustomScrollingLayoutCallback customScrollingLayoutCallback = new CustomScrollingLayoutCallback();
        recyclerView.setLayoutManager(new WearableLinearLayoutManager(activity, customScrollingLayoutCallback));
        recyclerView.setEdgeItemsCenteringEnabled(true);
        recyclerView.setAdapter(adapter);
    }
}
