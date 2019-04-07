package com.augment.golden.bulbcontrol.Adapters;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.augment.golden.bulbcontrol.Activities.BulbActivity;
import com.augment.golden.bulbcontrol.Beans.LifxApi.LifxBulbGroup;
import com.augment.golden.bulbcontrol.BulbAnimations;
import com.augment.golden.bulbcontrol.BulbGroup;
import com.augment.golden.bulbcontrol.Changeable;
import com.augment.golden.bulbcontrol.OnChangeListeners.ChangeableActionListeners;
import com.augment.golden.bulbcontrol.R;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class BulbGroupListAdapter extends RecyclerView.Adapter<BulbGroupListAdapter.GroupViewHolder> {
    private List<BulbGroup> m_bulbGroups;
    private Context m_context;
    private Map<String, BulbGroup> m_groupMap;

    static class GroupViewHolder extends RecyclerView.ViewHolder{
        ConstraintLayout mConstraintLayout;
        TextView mTextView;
        ImageView mImageView;
        GroupViewHolder(ConstraintLayout v){
            super(v);
            mConstraintLayout = v;
            mTextView = (TextView) v.getViewById(R.id.textView);
            mImageView = (ImageView) v.getViewById(R.id.imageView);
        }
    }


    public BulbGroupListAdapter(List<BulbGroup> groups, Context context){
        m_bulbGroups = groups;
        m_context = context;
        m_groupMap = new ConcurrentHashMap<>();
        groups.forEach(group -> m_groupMap.put(group.getId(), group));
    }

    @Override
    public GroupViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        ConstraintLayout v = (ConstraintLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.new_bulb, parent, false);
        return new GroupViewHolder(v);
    }

    @Override
    public void onBindViewHolder(GroupViewHolder holder, final int position){
        BulbGroup group = m_bulbGroups.get(position);

        holder.mTextView.setText(group.getName());
        Changeable changeable = (Changeable) group;
        holder.mImageView.setOnClickListener(new ChangeableActionListeners(changeable).getBulbImageListener());
        BulbAnimations.bulbPowerAnimation(holder.mImageView, changeable);

        holder.itemView.setOnClickListener((v) -> {
                int colorFrom = Color.BLACK;
                int colorTo = Color.DKGRAY;
                ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo, colorFrom);
                colorAnimation.setDuration(500); // milliseconds
                colorAnimation.addUpdateListener(animator -> holder.itemView.setBackgroundColor((int) animator.getAnimatedValue()));
                colorAnimation.start();
                BulbGroup bulb = m_bulbGroups.get(position);

                Intent intent = new Intent(m_context, BulbActivity.class);
                intent.putExtra("id", bulb.getId());
                intent.putExtra("type", group instanceof LifxBulbGroup ? "lifxGroup" : "hueGroup");
                m_context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount(){
        return m_bulbGroups.size();
    }


    public void remove(BulbGroup group){
        m_bulbGroups.remove(group);
        this.notifyDataSetChanged();
    }
    public void removeAll(List<BulbGroup> group){
        group.forEach(this::remove);
    }

    public void add(BulbGroup group){
        if(!m_groupMap.containsKey(group.getId())){
            m_bulbGroups.add(group);
            this.notifyDataSetChanged();
        }
        m_groupMap.put(group.getId(), group);
    }
    public void addAll(List<BulbGroup> groups){
        groups.forEach(this::add);
    }
}
