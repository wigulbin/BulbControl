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

    private final int indicatorHeight = (int) (DP * 16);
    private final float indicatorStrokeWidth = DP * 2;
    private final float indicatorItemLength = DP * 2;
    private final float indicatorItemPadding = DP * 4;

    private final Interpolator mInterpolator = new AccelerateDecelerateInterpolator();

    private final Paint paint = new Paint();

    public BulbPagerIndicatorDecoration() {
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeWidth(indicatorStrokeWidth);
        paint.setStyle(Paint.Style.STROKE);
        paint.setAntiAlias(true);
    }

    @Override
    public void onDrawOver(Canvas canvas, RecyclerView parent, RecyclerView.State state) {
        super.onDrawOver(canvas, parent, state);

        int itemCount = parent.getAdapter().getItemCount();

        // center horizontally, calculate width and subtract half from center
        float totalLength = indicatorItemLength * itemCount;
        float paddingBetweenItems = Math.max(0, itemCount - 1) * indicatorItemPadding;
        float indicatorTotalWidth = totalLength + paddingBetweenItems;
        float indicatorStartX = (parent.getWidth() - indicatorTotalWidth) / 2F;

        // center vertically in the allotted space
        float indicatorPosY = parent.getHeight() - indicatorHeight / 2F;

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
        paint.setColor(colorInactive);

        // width of item indicator including padding
        final float itemWidth = indicatorItemLength + indicatorItemPadding;

        float start = indicatorStartX;
//        float end = (itemWidth) * (itemCount);
        for (int i = 0; i < itemCount; i++) {
            // draw the line for every item
            canvas.drawCircle(start, indicatorPosY, indicatorItemLength, paint);
//            canvas.drawCircle(end, indicatorPosY, indicatorItemLength, paint);
            start += itemWidth;
//            end -= itemWidth;
        }
    }

    private void drawHighlights(Canvas canvas, float indicatorStartX, float indicatorPosY, int highlightPosition, float progress, int itemCount) {
        paint.setColor(colorActive);

        // width of item indicator including padding
        final float itemWidth = indicatorItemLength + indicatorItemPadding;

        highlightPosition = Math.abs(highlightPosition - itemCount + 1);

        if (progress == 0F) {
            // no swipe, draw a normal indicator
            float highlightStart = indicatorStartX + itemWidth * highlightPosition;
            canvas.drawCircle(highlightStart, indicatorPosY, indicatorItemLength, paint);
        } else {
            float highlightStart = indicatorStartX + itemWidth * highlightPosition;
            // calculate partial highlight
            float partialLength = indicatorItemLength * progress;
            // draw the cut off highlight
            canvas.drawCircle(highlightStart + partialLength, indicatorPosY, indicatorItemLength, paint);
        }
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        outRect.bottom = indicatorHeight;
    }
}
