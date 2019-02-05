package com.augment.golden.bulbcontrol;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;

import com.augment.golden.bulbcontrol.Activities.BulbActivity;
import com.augment.golden.bulbcontrol.Activities.BulbColorActivity;
import com.augment.golden.bulbcontrol.Activities.BulbWarmActivity;


public class SectionFragment extends Fragment {

    public enum Section{
        Brightness(R.string.brightness, R.drawable.ic_outline_brightness_5_24px),
        Color(R.string.color, R.drawable.ic_outline_color_lens_24px),
        Warmness(R.string.warmness, R.drawable.ic_outline_whatshot_24px);

        public final int titleRes;
        public final int drawableRes;

        Section(final int titleRes, final int drawableRes){
            this.titleRes = titleRes;
            this.drawableRes = drawableRes;
        }
    }


    public static void handleClick(int position, Context context, String mac){
        System.out.println(position);
        SectionFragment.Section selectedSection = SectionFragment.Section.values()[position];

        if(selectedSection.titleRes == R.string.brightness){
            Intent intent = new Intent(context, BulbActivity.class);
            intent.putExtra("mac", mac);
            context.startActivity(intent);
        }

        if(selectedSection.titleRes == R.string.color){
            Intent intent = new Intent(context, BulbColorActivity.class);
            intent.putExtra("mac", mac);
            context.startActivity(intent);
        }
        if(selectedSection.titleRes == R.string.warmness){
            Intent intent = new Intent(context, BulbWarmActivity.class);
            intent.putExtra("mac", mac);
            context.startActivity(intent);
        }
        System.out.println(selectedSection);
    }
}
