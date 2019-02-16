package com.augment.golden.bulbcontrol.Adapters;

import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
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
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.augment.golden.bulbcontrol.Activities.BulbActivity;
import com.augment.golden.bulbcontrol.Beans.LifxApi.LifxBulb;
import com.augment.golden.bulbcontrol.Beans.SmartBulb;
import com.augment.golden.bulbcontrol.BulbActionListners;
import com.augment.golden.bulbcontrol.R;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SmartBulbListAdapter extends RecyclerView.Adapter<SmartBulbListAdapter.BulbViewHolder> {
    List<SmartBulb> mBulbs;
    Map<String, SmartBulb> m_bulbMap;
    Context context;

    public static class BulbViewHolder extends RecyclerView.ViewHolder{
        public ConstraintLayout mConstraintLayout;
        public TextView mTextView;
        public ImageView mImageView;
        public BulbViewHolder(ConstraintLayout v){
            super(v);
            mConstraintLayout = v;
            mTextView = (TextView) v.getViewById(R.id.textView);
            mImageView = (ImageView) v.getViewById(R.id.imageView);
        }
    }

    public SmartBulbListAdapter(List<SmartBulb> bulbs, Context context){
        mBulbs = bulbs;
        this.context = context;
        m_bulbMap = new ConcurrentHashMap<>();
    }

    @Override
    public SmartBulbListAdapter.BulbViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        ConstraintLayout v = (ConstraintLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.new_bulb, parent, false);

        BulbViewHolder vh = new BulbViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(BulbViewHolder holder, final int position){
        holder.mTextView.setText(mBulbs.get(position).getLabel());

        holder.mImageView.setOnClickListener(new BulbActionListners(mBulbs.get(position)).getBulbImageListener());

        holder.itemView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                int colorFrom = Color.BLACK;
                int colorTo = Color.DKGRAY;
                ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo, colorFrom);
                colorAnimation.setDuration(500); // milliseconds
                colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animator) {
                        holder.itemView.setBackgroundColor((int) animator.getAnimatedValue());
                    }
                });
                colorAnimation.start();
                SmartBulb bulb = mBulbs.get(position);

                Intent intent = new Intent(context, BulbActivity.class);
                intent.putExtra("mac", bulb.getId());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount(){
        return mBulbs.size();
    }

    public void remove(SmartBulb bulb){
        mBulbs.remove(bulb);
        m_bulbMap.remove(bulb.getId());
        this.notifyDataSetChanged();
    }

    public void add(SmartBulb bulb){
        mBulbs.add(bulb);
        m_bulbMap.put(bulb.getId(), bulb);
        this.notifyDataSetChanged();
    }

    public SmartBulb getBulb(String id){
        return m_bulbMap.get(id);
    }
}
