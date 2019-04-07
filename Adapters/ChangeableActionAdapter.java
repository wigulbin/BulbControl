package com.augment.golden.bulbcontrol.Adapters;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.support.annotation.NonNull;
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChangeableActionAdapter extends RecyclerView.Adapter {
    private SparseArray<RecyclerView.ViewHolder> m_holderMap;
    private Activity m_activity;
    private Changeable m_changeable;
    private ChangeableActionListeners m_listeners;

    public ChangeableActionAdapter(Changeable changeable, Activity activity){
        m_holderMap = new SparseArray<>();
        m_changeable = changeable;
        m_activity = activity;
        m_listeners = new ChangeableActionListeners(changeable);
    }

    public static class BulbColorActionViewHolder extends RecyclerView.ViewHolder{
        ConstraintLayout m_constraintLayout;
        ColorPicker m_colorPicker;
        BulbColorActionViewHolder(ConstraintLayout v){
            super(v);
            m_constraintLayout = v;
            m_colorPicker = (ColorPicker) v.getViewById(R.id.bulb_color_picker);
        }
    }
    public static class BulbSaturationActionViewHolder extends RecyclerView.ViewHolder{
        ConstraintLayout m_constraintLayout;
        SaturationBar m_saturationBar;
        BulbSaturationActionViewHolder(ConstraintLayout v){
            super(v);
            m_constraintLayout = v;
            m_saturationBar = (SaturationBar) v.findViewById(R.id.bulb_saturation_bar);
        }
    }
    public static class BulbBrightActionViewHolder extends RecyclerView.ViewHolder{
        ConstraintLayout m_constraintLayout;
        ImageView m_bulbPower;
        SeekBar m_bulbBright;
        BulbBrightActionViewHolder(ConstraintLayout v){
            super(v);
            m_constraintLayout = v;
            m_bulbPower = (ImageView) v.getViewById(R.id.bulb_power);
            m_bulbBright = (SeekBar) v.getViewById(R.id.bulb_bright);
        }
    }
    public static class BulbWarmthActionViewHolder extends RecyclerView.ViewHolder{
        ConstraintLayout m_constraintLayout;
        SeekBar m_bulbSeek;
        BulbWarmthActionViewHolder(ConstraintLayout v){
            super(v);
            m_constraintLayout = v;
            m_bulbSeek = (SeekBar) v.getViewById(R.id.bulb_warm);
        }
    }

    public static class BulbViewHolder extends RecyclerView.ViewHolder{
        ConstraintLayout m_constraintLayout;
        RecyclerView m_recyclerView;
        BulbViewHolder(ConstraintLayout v){
            super(v);
            m_constraintLayout = v;
            m_recyclerView = (RecyclerView) v.getViewById(R.id.recycler_view);
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
            m_holderMap.put(viewType, holder);
            return holder;
        }
        if(viewType == 1) {
            RecyclerView.ViewHolder holder = new ChangeableActionAdapter.BulbSaturationActionViewHolder((ConstraintLayout) LayoutInflater.from(parent.getContext()).inflate(R.layout.bulb_saturation, parent, false));
            m_holderMap.put(viewType, holder);
            return holder;
        }
        if(viewType == 0) {
            RecyclerView.ViewHolder holder = new ChangeableActionAdapter.BulbWarmthActionViewHolder((ConstraintLayout) LayoutInflater.from(parent.getContext()).inflate(R.layout.bulb_warmth, parent, false));
            m_holderMap.put(viewType, holder);
            return holder;
        }
        if(viewType == 4) {
            ConstraintLayout view = (ConstraintLayout) LayoutInflater.from(parent.getContext()).inflate(R.layout.bulb_recycler, parent, false);
            ChangeableActionAdapter.BulbViewHolder holder = new ChangeableActionAdapter.BulbViewHolder(view);
            return holder;
        }

        RecyclerView.ViewHolder holder = new ChangeableActionAdapter.BulbBrightActionViewHolder((ConstraintLayout) LayoutInflater.from(parent.getContext()).inflate(R.layout.bulb_bright, parent, false));
        m_holderMap.put(viewType, holder);
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
        if(m_changeable instanceof BulbGroup)
            return 5;
        return 4;
    }

    private void handleWarmthItem(RecyclerView.ViewHolder holder){
        ChangeableActionAdapter.BulbWarmthActionViewHolder warmthViewHolder = (ChangeableActionAdapter.BulbWarmthActionViewHolder) holder;
        SeekBar seekBar = warmthViewHolder.m_bulbSeek;
        if(m_changeable instanceof HueBulb || m_changeable instanceof HueBulbGroup){
            seekBar.setMax(347);
        }
        seekBar.getThumb().setColorFilter(Color.parseColor("#ffa148"), PorterDuff.Mode.SRC_IN);
        seekBar.getProgressDrawable().setColorFilter(Color.parseColor("#ffa148"), PorterDuff.Mode.SRC_IN);
        seekBar.setOnTouchListener(m_listeners.getSeekBarTouchListener(warmthViewHolder.m_constraintLayout));
        seekBar.setOnSeekBarChangeListener(m_listeners.getWarmthSeekBarChangeListener());
    }
    private void handleSaturationItem(RecyclerView.ViewHolder holder){
        ChangeableActionAdapter.BulbSaturationActionViewHolder satViewHolder = (ChangeableActionAdapter.BulbSaturationActionViewHolder) holder;
        ChangeableActionAdapter.BulbColorActionViewHolder colorViewHolder = (ChangeableActionAdapter.BulbColorActionViewHolder) m_holderMap.get(2);
        satViewHolder.m_saturationBar.setOnSaturationChangedListener(m_listeners.createSaturationChangeListener());

        if(colorViewHolder != null && colorViewHolder.m_colorPicker != null && satViewHolder.m_saturationBar != null)
            colorViewHolder.m_colorPicker.addSaturationBar(satViewHolder.m_saturationBar);
    }
    private void handleColorItem(RecyclerView.ViewHolder holder){
        ChangeableActionAdapter.BulbColorActionViewHolder colorViewHolder = (ChangeableActionAdapter.BulbColorActionViewHolder) holder;
        colorViewHolder.m_colorPicker.setShowOldCenterColor(false);
        colorViewHolder.m_colorPicker.setOnColorChangedListener(m_listeners.createColorChangeListener());
        ChangeableActionAdapter.BulbSaturationActionViewHolder satViewHolder = (ChangeableActionAdapter.BulbSaturationActionViewHolder) m_holderMap.get(1);
        if(satViewHolder != null && satViewHolder.m_saturationBar != null &&  colorViewHolder.m_colorPicker != null)
            colorViewHolder.m_colorPicker.addSaturationBar(satViewHolder.m_saturationBar);
    }
    private void handleBrightnessItem(RecyclerView.ViewHolder holder){
        ChangeableActionAdapter.BulbBrightActionViewHolder brightViewHolder = (ChangeableActionAdapter.BulbBrightActionViewHolder) holder;
        SeekBar seekBar = brightViewHolder.m_bulbBright;
        seekBar.setOnTouchListener(m_listeners.getSeekBarTouchListener(brightViewHolder.m_constraintLayout));
        seekBar.setOnSeekBarChangeListener(m_listeners.getBrightnessSeekBarChangeListener(brightViewHolder.m_bulbPower));

        ImageView image = brightViewHolder.m_bulbPower;
        image.setOnClickListener(m_listeners.getBulbImageListener());
    }
    private void handleBulbListItem(RecyclerView.ViewHolder holder){
        WearableRecyclerView recyclerView = ((WearableRecyclerView) ((BulbViewHolder) holder).m_recyclerView);
        BulbGroup group = (BulbGroup) m_changeable;
        recyclerView.setLayoutManager(new LinearLayoutManager(m_activity));
        SmartBulbListAdapter adapter = new SmartBulbListAdapter(group.getBulbs(), m_activity);
        CustomScrollingLayoutCallback customScrollingLayoutCallback = new CustomScrollingLayoutCallback();
        recyclerView.setLayoutManager(new WearableLinearLayoutManager(m_activity, customScrollingLayoutCallback));
        recyclerView.setEdgeItemsCenteringEnabled(true);
        recyclerView.setAdapter(adapter);
    }
}
