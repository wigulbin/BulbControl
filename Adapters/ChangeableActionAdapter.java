package com.augment.golden.bulbcontrol.Adapters;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;

import com.augment.golden.bulbcontrol.Beans.HueApi.HueBulb;
import com.augment.golden.bulbcontrol.Beans.HueApi.HueBulbGroup;
import com.augment.golden.bulbcontrol.Beans.SmartBulb;
import com.augment.golden.bulbcontrol.Changeable;
import com.augment.golden.bulbcontrol.OnChangeListeners.BulbActionListeners;
import com.augment.golden.bulbcontrol.OnChangeListeners.ChangeableActionListeners;
import com.augment.golden.bulbcontrol.R;
import com.larswerkman.holocolorpicker.ColorPicker;
import com.larswerkman.holocolorpicker.SaturationBar;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChangeableActionAdapter extends RecyclerView.Adapter {
    private Map<Integer, RecyclerView.ViewHolder> holderMap;
    private Activity activity;
    private Changeable changeable;
    private ChangeableActionListeners listeners;

    public ChangeableActionAdapter(Changeable changeable, Activity activity){
        holderMap = new HashMap<>();
        this.changeable = changeable;
        this.activity = activity;
        listeners = new ChangeableActionListeners(changeable);
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

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
        System.out.println("View Type " + viewType);
        if(viewType == 1)
        {
            RecyclerView.ViewHolder holder = new BulbActionAdapter.BulbColorActionViewHolder((ConstraintLayout) LayoutInflater.from(parent.getContext()).inflate(R.layout.bulb_color, parent, false));
            holderMap.put(viewType, holder);
            return holder;
        }
        if(viewType == 2)
        {
            RecyclerView.ViewHolder holder = new BulbActionAdapter.BulbSaturationActionViewHolder((ConstraintLayout) LayoutInflater.from(parent.getContext()).inflate(R.layout.bulb_saturation, parent, false));
            holderMap.put(viewType, holder);
            return holder;
        }
        if(viewType == 3)
        {
            RecyclerView.ViewHolder holder = new BulbActionAdapter.BulbWarmthActionViewHolder((ConstraintLayout) LayoutInflater.from(parent.getContext()).inflate(R.layout.bulb_warmth, parent, false));
            holderMap.put(viewType, holder);
            return holder;
        }

        RecyclerView.ViewHolder holder = new BulbActionAdapter.BulbBrightActionViewHolder((ConstraintLayout) LayoutInflater.from(parent.getContext()).inflate(R.layout.bulb_bright, parent, false));
        holderMap.put(viewType, holder);
        return holder;
    }
    @Override public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {

        if(holder.getItemViewType() == 3) handleWarmthItem(holder);
        if(holder.getItemViewType() == 2) handleSaturationItem(holder);
        if(holder.getItemViewType() == 1) handleColorItem(holder);
        if(holder.getItemViewType() == 0) handleBrightnessItem(holder);
    }

    @Override
    public int getItemCount() {
        return 4;
    }
    public void setItems(@NonNull final List items) {
    }


    private void handleWarmthItem(RecyclerView.ViewHolder holder){
        BulbActionAdapter.BulbWarmthActionViewHolder warmthViewHolder = (BulbActionAdapter.BulbWarmthActionViewHolder) holder;
        SeekBar seekBar = warmthViewHolder.m_bulbSeek;
        if(changeable instanceof HueBulb || changeable instanceof HueBulbGroup){
            seekBar.setMax(347);
        }
        seekBar.getThumb().setColorFilter(Color.parseColor("#ffa148"), PorterDuff.Mode.SRC_IN);
        seekBar.getProgressDrawable().setColorFilter(Color.parseColor("#ffa148"), PorterDuff.Mode.SRC_IN);
        seekBar.setOnTouchListener(listeners.getSeekBarTouchListener(warmthViewHolder.m_constraintLayout));
        seekBar.setOnSeekBarChangeListener(listeners.getWarmthSeekBarChangeListener());
    }
    private void handleSaturationItem(RecyclerView.ViewHolder holder){
        BulbActionAdapter.BulbSaturationActionViewHolder satViewHolder = (BulbActionAdapter.BulbSaturationActionViewHolder) holder;
        BulbActionAdapter.BulbColorActionViewHolder colorViewHolder = (BulbActionAdapter.BulbColorActionViewHolder) holderMap.get(1);
        satViewHolder.m_saturationBar.setOnSaturationChangedListener(listeners.createSaturationChangeListener());

        if(colorViewHolder != null && colorViewHolder.m_colorPicker != null && satViewHolder.m_saturationBar != null)
            colorViewHolder.m_colorPicker.addSaturationBar(satViewHolder.m_saturationBar);
    }
    private void handleColorItem(RecyclerView.ViewHolder holder){
        BulbActionAdapter.BulbColorActionViewHolder colorViewHolder = (BulbActionAdapter.BulbColorActionViewHolder) holder;
        colorViewHolder.m_colorPicker.setShowOldCenterColor(false);
        colorViewHolder.m_colorPicker.setOnColorChangedListener(listeners.createColorChangeListener());
        BulbActionAdapter.BulbSaturationActionViewHolder satViewHolder = (BulbActionAdapter.BulbSaturationActionViewHolder) holderMap.get(2);
        if(satViewHolder != null && satViewHolder.m_saturationBar != null &&  colorViewHolder.m_colorPicker != null)
            colorViewHolder.m_colorPicker.addSaturationBar(satViewHolder.m_saturationBar);
    }
    private void handleBrightnessItem(RecyclerView.ViewHolder holder){
        BulbActionAdapter.BulbBrightActionViewHolder brightViewHolder = (BulbActionAdapter.BulbBrightActionViewHolder) holder;
        SeekBar seekBar = brightViewHolder.m_bulbBright;
        seekBar.setOnTouchListener(listeners.getSeekBarTouchListener(brightViewHolder.m_constraintLayout));
        seekBar.setOnSeekBarChangeListener(listeners.getBrightnessSeekBarChangeListener(brightViewHolder.m_bulbPower));

        ImageView image = brightViewHolder.m_bulbPower;
        image.setOnClickListener(listeners.getBulbImageListener());
    }
}
