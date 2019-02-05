package com.augment.golden.bulbcontrol.Adapters;

import android.content.Context;
import android.content.Intent;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.augment.golden.bulbcontrol.Activities.BulbActivity;
import com.augment.golden.bulbcontrol.Beans.LifxApi.LifxBulb;
import com.augment.golden.bulbcontrol.R;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SmartBulbListAdapter extends RecyclerView.Adapter<SmartBulbListAdapter.BulbViewHolder> {
    List<LifxBulb> mBulbs;
    Map<String, LifxBulb> m_bulbMap;
    Context context;

    public static class BulbViewHolder extends RecyclerView.ViewHolder{
        public ConstraintLayout mConstraintLayout;
        public TextView mTextView;
        public BulbViewHolder(ConstraintLayout v){
            super(v);
            mConstraintLayout = v;
            mTextView = (TextView) v.getViewById(R.id.textView);
        }
    }

    public SmartBulbListAdapter(List<LifxBulb> bulbs, Context context){
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

        holder.itemView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                LifxBulb bulb = mBulbs.get(position);

                Intent intent = new Intent(context, BulbActivity.class);
                intent.putExtra("mac", bulb.getMac());

                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount(){
        return mBulbs.size();
    }

    public void remove(LifxBulb bulb){
        mBulbs.remove(bulb);
        m_bulbMap.remove(bulb.getMac());
        this.notifyDataSetChanged();
    }

    public void add(LifxBulb bulb){
        mBulbs.add(bulb);
        m_bulbMap.put(bulb.getMac(), bulb);
        this.notifyDataSetChanged();
    }

    public LifxBulb getBulb(String mac){
        return m_bulbMap.get(mac);
    }
}
