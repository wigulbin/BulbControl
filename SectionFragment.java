package com.augment.golden.bulbcontrol;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class SectionFragment extends Fragment {

    public enum Section{
        Search(R.string.search, R.drawable.round_cached_black_18dp_2x);

        public final int titleRes;
        public final int drawableRes;

        Section(final int titleRes, final int drawableRes){
            this.titleRes = titleRes;
            this.drawableRes = drawableRes;
        }
    }

    public static SectionFragment getSection(final Section section) {
        final SectionFragment newSection = new SectionFragment();
        final Bundle arguments = new Bundle();
        arguments.putSerializable(EXTRA_SECTION, section);
        newSection.setArguments(arguments);
        return newSection;
    }

    public static final String EXTRA_SECTION =
            "com.example.android.wearable.navaction.EXTRA_SECTION";


    private Section mSection;
    private ImageView mEmojiView;
    private TextView mTitleView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState){
        final View view = inflater.inflate(R.layout.fragment_sections, container, false);
        mEmojiView = (ImageView) view.findViewById(R.id.emoji);
        mTitleView = (TextView) view.findViewById(R.id.title);

        if(getArguments() != null){
            mSection = (Section) getArguments().getSerializable(EXTRA_SECTION);
            final Drawable imageDrawable = ContextCompat.getDrawable(getContext(), mSection.drawableRes);
            mEmojiView.setImageDrawable(imageDrawable);
            mTitleView.setText(getResources().getText(mSection.titleRes));


            mTitleView.setOnClickListener(new TextView.OnClickListener(){
                @Override
                public void onClick(View v) {
                    System.out.println(v);
                }
            });
        }

        return view;
    }
}
