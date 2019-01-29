package com.augment.golden.bulbcontrol.Adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.wear.widget.drawer.WearableDrawerView;
import android.support.wear.widget.drawer.WearableNavigationDrawerView;
import android.support.wearable.view.drawer.WearableNavigationDrawer;
import android.support.wear.widget.drawer.WearableDrawerLayout;
import com.augment.golden.bulbcontrol.SectionFragment;

public class NavigationAdapter extends WearableNavigationDrawerView.WearableNavigationDrawerAdapter {
//public class NavigationAdapter extends WearableDrawerView {
    private final Context mContext;
//    private SectionFragment.Section mCurrentSection = SectionFragment.Section.Search;

    public NavigationAdapter(final Context context) {
        mContext = context;
    }

    @Override
    public Drawable getItemDrawable(int index) {
        return mContext.getDrawable(SectionFragment.Section.values()[index].drawableRes);
    }

    @Override
    public int getCount() {
        return SectionFragment.Section.values().length;
    }

    @Override
    public String getItemText(int index) {
        return mContext.getString(SectionFragment.Section.values()[index].titleRes);
    }

//    @Override
    public void onItemSelected (int pos) {
        SectionFragment.Section selectedSection = SectionFragment.Section.values()[pos];

        System.out.println("Selected");
    }
}