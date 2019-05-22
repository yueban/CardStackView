package com.yuyakaido.android.cardstackview.internal;

import android.support.annotation.IntDef;
import android.support.v7.widget.RecyclerView;

import com.yuyakaido.android.cardstackview.Direction;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class CardStackState {
    public Status status = Status.Idle;
    @ScrollerStatus
    public int scrollerStatus = ScrollerStatus.onStop;
    public int width = 0;
    public int height = 0;
    public int dx = 0;
    public int dy = 0;
    public int topPosition = 0;
    public int targetPosition = RecyclerView.NO_POSITION;
    public float proportion = 0.0f;

    public void next(Status state) {
        this.status = state;
    }

    public Direction getDirection() {
        if (Math.abs(dy) < Math.abs(dx)) {
            if (dx < 0.0f) {
                return Direction.Left;
            } else {
                return Direction.Right;
            }
        } else {
            if (dy < 0.0f) {
                return Direction.Top;
            } else {
                return Direction.Bottom;
            }
        }
    }

    public float getRatio() {
        if (isBeforeRewindMove()) {
            return 1.0f;
        }

        int absDx = Math.abs(dx);
        int absDy = Math.abs(dy);
        float ratio;
        if (absDx < absDy) {
            ratio = absDy / (height / 2.0f);
        } else {
            ratio = absDx / (width / 2.0f);
        }
        return Math.min(ratio, 1.0f);
    }

    public boolean isBeforeRewindMove() {
        return status == CardStackState.Status.RewindAnimating
                && scrollerStatus <= CardStackState.ScrollerStatus.onTargetFound
                && dx == 0 && dy == 0;
    }

    public boolean isSwipeCompleted() {
        if (status.isSwipeAnimating()) {
            if (topPosition < targetPosition) {
                if (width < Math.abs(dx) || height < Math.abs(dy)) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean canScrollToPosition(int position, int itemCount) {
        if (position == topPosition) {
            return false;
        }
        if (position < 0) {
            return false;
        }
        if (itemCount < position) {
            return false;
        }
        if (status.isBusy()) {
            return false;
        }
        return true;
    }

    public enum Status {
        Idle,
        Dragging,
        RewindAnimating,
        CancelAnimating,
        AutomaticSwipeAnimating,
        AutomaticSwipeAnimated,
        ManualSwipeAnimating,
        ManualSwipeAnimated;

        public boolean isBusy() {
            return this != Idle;
        }

        public boolean isDragging() {
            return this == Dragging;
        }

        public boolean isSwipeAnimating() {
            return this == ManualSwipeAnimating || this == AutomaticSwipeAnimating;
        }

        public Status toAnimatedStatus() {
            switch (this) {
                case ManualSwipeAnimating:
                    return ManualSwipeAnimated;
                case AutomaticSwipeAnimating:
                    return AutomaticSwipeAnimated;
                default:
                    return Idle;
            }
        }
    }

    @IntDef({
            ScrollerStatus.onStart,
            ScrollerStatus.onSeekTargetStep,
            ScrollerStatus.onTargetFound,
            ScrollerStatus.onStop,
    })
    @Retention(RetentionPolicy.SOURCE)
    public @interface ScrollerStatus {
        int onStart = 1;
        int onSeekTargetStep = 2;
        int onTargetFound = 3;
        int onStop = 4;
    }
}
