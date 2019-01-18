package com.augment.golden.bulbcontrol.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.augment.golden.bulbcontrol.Beans.SmartBulb;
import com.augment.golden.bulbcontrol.R;

import java.util.List;

public class SmartBulbListAdapter extends ArrayAdapter<SmartBulb> {
    public SmartBulbListAdapter(Context context, int resource, List<SmartBulb> objects) {
        super(context, resource, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(position == 0){
            return LayoutInflater.from(this.getContext()).inflate(R.layout.new_bulb, parent, false);
        }
        convertView = LayoutInflater.from(this.getContext()).inflate(android.R.layout.simple_list_item_1, parent, false);
        SmartBulb bulb = getItem(position);

        TextView title = (TextView) convertView.findViewById(android.R.id.text1);
        title.setText(bulb.getLabel());
        return convertView;
    }
}
