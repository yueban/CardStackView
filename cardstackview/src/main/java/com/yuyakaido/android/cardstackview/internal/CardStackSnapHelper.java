package com.yuyakaido.android.cardstackview.internal;

import android.view.View;

import com.yuyakaido.android.cardstackview.CardAnimationSetting;
import com.yuyakaido.android.cardstackview.CardStackLayoutManager;
import com.yuyakaido.android.cardstackview.Direction;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

public class CardStackSnapHelper extends SnapHelper {

    private int velocityX = 0;
    private int velocityY = 0;

    @Nullable
    @Override
    public int[] calculateDistanceToFinalSnap(
            @NonNull RecyclerView.LayoutManager layoutManager,
            @NonNull View targetView
    ) {
        if (layoutManager instanceof CardStackLayoutManager) {
            CardStackLayoutManager manager = (CardStackLayoutManager) layoutManager;
            if (manager.findViewByPosition(manager.getTopPosition()) != null) {
                int x = (int) targetView.getTranslationX();
                int y = (int) targetView.getTranslationY();
                if (x != 0 || y != 0) {
                    CardStackSetting setting = manager.getCardStackSetting();
                    float horizontal = Math.abs(x) / (float) targetView.getWidth();
                    float vertical = Math.abs(y) / (float) targetView.getHeight();
                    int velocity = Math.max(velocityX, velocityY);
                    boolean exceedVelocityLimit = setting.velocityLimitForCancel > 0 && velocity > setting.velocityLimitForCancel;
                    if (!exceedVelocityLimit
                            && (setting.swipeThresholdRatioX < horizontal || setting.swipeThresholdRatioY < vertical
                            || setting.swipeThresholdX < Math.abs(x) || setting.swipeThresholdY < Math.abs(y))) {
                        CardStackState state = manager.getCardStackState();
                        Direction direction = state.getDirection();
                        if (setting.directions.contains(direction)) {
                            state.targetPosition = state.topPosition + 1;

                            this.velocityX = 0;
                            this.velocityY = 0;

                            CardAnimationSetting oldSetting = manager.getCardStackSetting().manualSwipeAnimationSetting;
                            manager.getCardStackSetting().manualSwipeAnimationSetting = new CardAnimationSetting.Builder()
                                    .setDirection(direction)
                                    .setInterpolator(oldSetting.getInterpolator())
                                    .setDuration(oldSetting.getDuration())
                                    .build();

                            CardStackSmoothScroller scroller = new CardStackSmoothScroller(CardStackSmoothScroller.ScrollType.ManualSwipe, manager);
                            scroller.setTargetPosition(manager.getTopPosition());
                            manager.startSmoothScroll(scroller);
                        } else {
                            CardStackSmoothScroller scroller = new CardStackSmoothScroller(CardStackSmoothScroller.ScrollType.ManualCancel, manager);
                            scroller.setTargetPosition(manager.getTopPosition());
                            manager.startSmoothScroll(scroller);
                        }
                    } else {
                        CardStackSmoothScroller scroller = new CardStackSmoothScroller(CardStackSmoothScroller.ScrollType.ManualCancel, manager);
                        scroller.setTargetPosition(manager.getTopPosition());
                        manager.startSmoothScroll(scroller);
                    }
                }
            }
        }
        return new int[2];
    }

    @Nullable
    @Override
    public View findSnapView(RecyclerView.LayoutManager layoutManager) {
        if (layoutManager instanceof CardStackLayoutManager) {
            CardStackLayoutManager manager = (CardStackLayoutManager) layoutManager;
            View view = manager.findViewByPosition(manager.getTopPosition());
            if (view != null) {
                int x = (int) view.getTranslationX();
                int y = (int) view.getTranslationY();
                if (x == 0 && y == 0) {
                    return null;
                }
                return view;
            }
        }
        return null;
    }

    @Override
    public int findTargetSnapPosition(
            RecyclerView.LayoutManager layoutManager,
            int velocityX,
            int velocityY
    ) {
        this.velocityX = Math.abs(velocityX);
        this.velocityY = Math.abs(velocityY);
        if (layoutManager instanceof CardStackLayoutManager) {
            CardStackLayoutManager manager = (CardStackLayoutManager) layoutManager;
            return manager.getTopPosition();
        }
        return RecyclerView.NO_POSITION;
    }

}
