package com.augment.golden.bulbcontrol.Adapters;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.augment.golden.bulbcontrol.Activities.BulbActivity;
import com.augment.golden.bulbcontrol.Beans.LifxApi.LifxBulb;
import com.augment.golden.bulbcontrol.Beans.LifxApi.LifxBulbGroup;
import com.augment.golden.bulbcontrol.Beans.SmartBulb;
import com.augment.golden.bulbcontrol.BulbAnimations;
import com.augment.golden.bulbcontrol.BulbGroup;
import com.augment.golden.bulbcontrol.Changeable;
import com.augment.golden.bulbcontrol.OnChangeListeners.ChangeableActionListeners;
import com.augment.golden.bulbcontrol.R;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class BulbGroupListAdapter extends RecyclerView.Adapter<BulbGroupListAdapter.GroupViewHolder> {
    List<BulbGroup> mBulbGroups;
    Context context;
    Map<String, BulbGroup> groupMap;

    public static class GroupViewHolder extends RecyclerView.ViewHolder{
        public ConstraintLayout mConstraintLayout;
        public TextView mTextView;
        public ImageView mImageView;
        public GroupViewHolder(ConstraintLayout v){
            super(v);
            mConstraintLayout = v;
            mTextView = (TextView) v.getViewById(R.id.textView);
            mImageView = (ImageView) v.getViewById(R.id.imageView);
        }
    }


    public BulbGroupListAdapter(List<BulbGroup> groups, Context context){
        mBulbGroups = groups;
        this.context = context;
        groupMap = new ConcurrentHashMap<>();
        groups.forEach(group -> groupMap.put(group.getId(), group));
    }

    @Override
    public GroupViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        ConstraintLayout v = (ConstraintLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.new_bulb, parent, false);

        GroupViewHolder vh = new GroupViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(GroupViewHolder holder, final int position){
        BulbGroup group = mBulbGroups.get(position);

        holder.mTextView.setText(group.getName());
        Changeable changeable = (Changeable) group;
        holder.mImageView.setOnClickListener(new ChangeableActionListeners(changeable).getBulbImageListener());
        BulbAnimations.bulbPowerAnimation(holder.mImageView, changeable);

        holder.itemView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                int colorFrom = Color.BLACK;
                int colorTo = Color.DKGRAY;
                ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo, colorFrom);
                colorAnimation.setDuration(500); // milliseconds
                colorAnimation.addUpdateListener(animator -> holder.itemView.setBackgroundColor((int) animator.getAnimatedValue()));
                colorAnimation.start();
                BulbGroup bulb = mBulbGroups.get(position);

                Intent intent = new Intent(context, BulbActivity.class);
                intent.putExtra("id", bulb.getId());
                intent.putExtra("type", group instanceof LifxBulbGroup ? "lifxGroup" : "hueGroup");
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount(){
        return mBulbGroups.size();
    }


    public void remove(SmartBulb bulb){
        mBulbGroups.remove(bulb);
        this.notifyDataSetChanged();
    }
    public void removeAll(List<SmartBulb> bulbs){
        bulbs.forEach(this::remove);
    }

    public void add(BulbGroup group){
        if(!groupMap.containsKey(group.getId())){
            mBulbGroups.add(group);
            this.notifyDataSetChanged();
        }
        groupMap.put(group.getId(), group);
    }
    public void addAll(List<BulbGroup> groups){
        groups.forEach(this::add);
    }
}
