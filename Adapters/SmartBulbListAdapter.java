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
import com.augment.golden.bulbcontrol.Beans.SmartBulb;
import com.augment.golden.bulbcontrol.BulbAnimations;
import com.augment.golden.bulbcontrol.Changeable;
import com.augment.golden.bulbcontrol.OnChangeListeners.ChangeableActionListeners;
import com.augment.golden.bulbcontrol.R;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SmartBulbListAdapter extends RecyclerView.Adapter<SmartBulbListAdapter.BulbViewHolder> {
    private List<SmartBulb> m_bulbs;
    private Map<String, SmartBulb> m_bulbMap;
    private Context m_context;

    static class BulbViewHolder extends RecyclerView.ViewHolder{
        ConstraintLayout mConstraintLayout;
        TextView mTextView;
        ImageView mImageView;
        BulbViewHolder(ConstraintLayout v){
            super(v);
            mConstraintLayout = v;
            mTextView = (TextView) v.getViewById(R.id.textView);
            mImageView = (ImageView) v.getViewById(R.id.imageView);
        }
    }

    public SmartBulbListAdapter(List<SmartBulb> bulbs, Context context){
        m_bulbs = bulbs;
        m_context = context;
        m_bulbMap = new ConcurrentHashMap<>();
        bulbs.forEach(bulb -> m_bulbMap.put(bulb.getId(), bulb));
    }

    @Override
    public SmartBulbListAdapter.BulbViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        ConstraintLayout v = (ConstraintLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.new_bulb, parent, false);

        return new BulbViewHolder(v);
    }

    @Override
    public void onBindViewHolder(BulbViewHolder holder, final int position){
        holder.mTextView.setText(m_bulbs.get(position).getLabel());

        Changeable changeable = (Changeable) m_bulbs.get(position);
        holder.mImageView.setOnClickListener(new ChangeableActionListeners(changeable).getBulbImageListener());
        BulbAnimations.bulbPowerAnimation(holder.mImageView, changeable);

        holder.itemView.setOnClickListener((v) -> {
                int colorFrom = Color.BLACK;
                int colorTo = Color.DKGRAY;
                ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo, colorFrom);
                colorAnimation.setDuration(500); // milliseconds
                colorAnimation.addUpdateListener(animator ->  holder.itemView.setBackgroundColor((int) animator.getAnimatedValue()));
                colorAnimation.start();
                SmartBulb bulb = m_bulbs.get(position);

                Intent intent = new Intent(m_context, BulbActivity.class);
                intent.putExtra("id", bulb.getId());
                String type = bulb instanceof LifxBulb ? "lifx" : "hue";
                intent.putExtra("type", type);
                m_context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount(){
        return m_bulbs.size();
    }

    void remove(SmartBulb bulb){
        m_bulbs.remove(bulb);
        m_bulbMap.remove(bulb.getId());
        this.notifyDataSetChanged();
    }
    public void removeAll(List<SmartBulb> bulbs){
        bulbs.forEach(this::remove);
    }

    public void add(SmartBulb bulb){
        if(!m_bulbMap.containsKey(bulb.getId())){
            m_bulbs.add(bulb);
            this.notifyDataSetChanged();
        }
        m_bulbMap.put(bulb.getId(), bulb);
    }
    public void addAll(List<SmartBulb> bulbs){
        bulbs.forEach(this::add);
    }

    public SmartBulb getBulb(String id){
        return m_bulbMap.get(id);
    }
}
