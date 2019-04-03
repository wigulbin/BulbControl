package com.augment.golden.bulbcontrol;

import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;

public class BulbPagerIndicatorDecoration extends RecyclerView.ItemDecoration {
    private int colorActive = 0xFFFFFFFF;
    private int colorInactive = 0x66FFFFFF;

    private static final float DP = Resources.getSystem().getDisplayMetrics().density;

    private final int mIndicatorHeight = (int) (DP * 16);
    private final float mIndicatorStrokeWidth = DP * 2;
    private final float mIndicatorItemLength = DP * 2;
    private final float mIndicatorItemPadding = DP * 4;

    private final Interpolator mInterpolator = new AccelerateDecelerateInterpolator();

    private final Paint mPaint = new Paint();

    public BulbPagerIndicatorDecoration() {
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(mIndicatorStrokeWidth);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setAntiAlias(true);
    }

    @Override
    public void onDrawOver(Canvas canvas, RecyclerView parent, RecyclerView.State state) {
        super.onDrawOver(canvas, parent, state);

        int itemCount = parent.getAdapter().getItemCount();

        // center horizontally, calculate width and subtract half from center
        float totalLength = mIndicatorItemLength * itemCount;
        float paddingBetweenItems = Math.max(0, itemCount - 1) * mIndicatorItemPadding;
        float indicatorTotalWidth = totalLength + paddingBetweenItems;
        float indicatorStartX = (parent.getWidth() - indicatorTotalWidth) / 2F;

        // center vertically in the allotted space
        float indicatorPosY = parent.getHeight() - mIndicatorHeight / 2F;

        drawInactiveIndicators(canvas, indicatorStartX, indicatorPosY, itemCount);


        // find active page (which should be highlighted)
        LinearLayoutManager layoutManager = (LinearLayoutManager) parent.getLayoutManager();
        int activePosition = layoutManager.findLastVisibleItemPosition();
        if (activePosition == RecyclerView.NO_POSITION) {
            return;
        }

        // find offset of active page (if the user is scrolling)
        final View activeChild = layoutManager.findViewByPosition(activePosition);
        int left = activeChild.getLeft();
        int width = activeChild.getWidth();

        // on swipe the active item will be positioned from [-width, 0]
        // interpolate offset for smooth animation
        float progress = mInterpolator.getInterpolation(left / (float) width);

        drawHighlights(canvas, indicatorStartX, indicatorPosY, activePosition, progress, itemCount);
    }

    private void drawInactiveIndicators(Canvas canvas, float indicatorStartX, float indicatorPosY, int itemCount) {
        mPaint.setColor(colorInactive);

        // width of item indicator including padding
        final float itemWidth = mIndicatorItemLength + mIndicatorItemPadding;

        float start = indicatorStartX;
//        float end = (itemWidth) * (itemCount);
        for (int i = 0; i < itemCount; i++) {
            // draw the line for every item
            canvas.drawCircle(start, indicatorPosY, mIndicatorItemLength, mPaint);
//            canvas.drawCircle(end, indicatorPosY, mIndicatorItemLength, mPaint);
            start += itemWidth;
//            end -= itemWidth;
        }
    }

    private void drawHighlights(Canvas canvas, float indicatorStartX, float indicatorPosY, int highlightPosition, float progress, int itemCount) {
        mPaint.setColor(colorActive);

        // width of item indicator including padding
        final float itemWidth = mIndicatorItemLength + mIndicatorItemPadding;

        highlightPosition = Math.abs(highlightPosition - itemCount + 1);

        if (progress == 0F) {
            // no swipe, draw a normal indicator
            float highlightStart = indicatorStartX + itemWidth * highlightPosition;
            canvas.drawCircle(highlightStart, indicatorPosY, mIndicatorItemLength, mPaint);
        } else {
            float highlightStart = indicatorStartX + itemWidth * highlightPosition;
            // calculate partial highlight
            float partialLength = mIndicatorItemLength * progress;
            // draw the cut off highlight
            canvas.drawCircle(highlightStart + partialLength, indicatorPosY, mIndicatorItemLength, mPaint);
        }
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        outRect.bottom = mIndicatorHeight;
    }
}
