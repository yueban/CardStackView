package com.yuyakaido.android.cardstackview;

import android.content.Context;
import android.graphics.PointF;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Interpolator;

import com.yuyakaido.android.cardstackview.internal.CardStackSetting;
import com.yuyakaido.android.cardstackview.internal.CardStackSmoothScroller;
import com.yuyakaido.android.cardstackview.internal.CardStackState;
import com.yuyakaido.android.cardstackview.internal.DisplayUtil;

import java.util.List;

import androidx.annotation.FloatRange;
import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class CardStackLayoutManager
        extends RecyclerView.LayoutManager
        implements RecyclerView.SmoothScroller.ScrollVectorProvider {

    private final Context context;

    private CardStackListener listener;
    private CardStackSetting setting = new CardStackSetting();
    private CardStackState state = new CardStackState();

    public CardStackLayoutManager(Context context) {
        this(context, CardStackListener.DEFAULT);
    }

    public CardStackLayoutManager(Context context, CardStackListener listener) {
        this.context = context;
        this.listener = listener;
    }

    @Override
    public RecyclerView.LayoutParams generateDefaultLayoutParams() {
        return new RecyclerView.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        );
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State s) {
        update(recycler);
        if (s.didStructureChange()) {
            View topView = getTopView();
            if (topView != null) {
                listener.onCardAppeared(getTopView(), state.topPosition);
            }
        }
    }

    @Override
    public boolean canScrollHorizontally() {
        return setting.swipeableMethod.canSwipe() && setting.canScrollHorizontal;
    }

    @Override
    public boolean canScrollVertically() {
        return setting.swipeableMethod.canSwipe() && setting.canScrollVertical;
    }

    @Override
    public int scrollHorizontallyBy(int dx, RecyclerView.Recycler recycler, RecyclerView.State s) {
        if (state.topPosition == getItemCount()) {
            return 0;
        }

        switch (state.status) {
            case Idle:
                if (setting.swipeableMethod.canSwipeManually()) {
                    state.dx -= dx;
                    update(recycler);
                    return dx;
                }
                break;
            case Dragging:
                if (setting.swipeableMethod.canSwipeManually()) {
                    state.dx -= dx;
                    update(recycler);
                    return dx;
                }
                break;
            case RewindAnimating:
            case CancelAnimating:
                state.dx -= dx;
                update(recycler);
                return dx;
            case AutomaticSwipeAnimating:
                if (setting.swipeableMethod.canSwipeAutomatically()) {
                    state.dx -= dx;
                    update(recycler);
                    return dx;
                }
                break;
            case AutomaticSwipeAnimated:
                break;
            case ManualSwipeAnimating:
                if (setting.swipeableMethod.canSwipeManually()) {
                    state.dx -= dx;
                    update(recycler);
                    return dx;
                }
                break;
            case ManualSwipeAnimated:
                break;
        }

        return 0;
    }

    @Override
    public int scrollVerticallyBy(int dy, RecyclerView.Recycler recycler, RecyclerView.State s) {
        if (state.topPosition == getItemCount()) {
            return 0;
        }

        switch (state.status) {
            case Idle:
                if (setting.swipeableMethod.canSwipeManually()) {
                    state.dy -= dy;
                    update(recycler);
                    return dy;
                }
                break;
            case Dragging:
                if (setting.swipeableMethod.canSwipeManually()) {
                    state.dy -= dy;
                    update(recycler);
                    return dy;
                }
                break;
            case RewindAnimating:
            case CancelAnimating:
                state.dy -= dy;
                update(recycler);
                return dy;
            case AutomaticSwipeAnimating:
                if (setting.swipeableMethod.canSwipeAutomatically()) {
                    state.dy -= dy;
                    update(recycler);
                    return dy;
                }
                break;
            case AutomaticSwipeAnimated:
                break;
            case ManualSwipeAnimating:
                if (setting.swipeableMethod.canSwipeManually()) {
                    state.dy -= dy;
                    update(recycler);
                    return dy;
                }
                break;
            case ManualSwipeAnimated:
                break;
        }
        return 0;
    }

    @Override
    public void onScrollStateChanged(int s) {
        switch (s) {
            // スクロールが止まったタイミング
            case RecyclerView.SCROLL_STATE_IDLE:
                if (state.targetPosition == RecyclerView.NO_POSITION) {
                    // Swipeが完了した場合の処理
                    state.next(CardStackState.Status.Idle);
                    state.targetPosition = RecyclerView.NO_POSITION;
                } else if (state.topPosition == state.targetPosition) {
                    // Rewindが完了した場合の処理
                    state.next(CardStackState.Status.Idle);
                    state.targetPosition = RecyclerView.NO_POSITION;
                } else {
                    // 2枚以上のカードを同時にスワイプする場合の処理
                    if (state.topPosition < state.targetPosition) {
                        // 1枚目のカードをスワイプすると一旦SCROLL_STATE_IDLEが流れる
                        // そのタイミングで次のアニメーションを走らせることで連続でスワイプしているように見せる
                        smoothScrollToNext(state.targetPosition);
                    } else {
                        // Nextの場合と同様に、1枚目の処理が完了したタイミングで次のアニメーションを走らせる
                        smoothScrollToPrevious(state.targetPosition);
                    }
                }
                break;
            // カードをドラッグしている最中
            case RecyclerView.SCROLL_STATE_DRAGGING:
                if (setting.swipeableMethod.canSwipeManually()) {
                    state.next(CardStackState.Status.Dragging);
                }
                break;
            // カードが指から離れたタイミング
            case RecyclerView.SCROLL_STATE_SETTLING:
                break;
        }
    }

    @Override
    public PointF computeScrollVectorForPosition(int targetPosition) {
        return null;
    }

    @Override
    public void scrollToPosition(int position) {
        if (setting.swipeableMethod.canSwipeAutomatically()) {
            if (state.canScrollToPosition(position, getItemCount())) {
                state.topPosition = position;
                requestLayout();
            }
        }
    }

    @Override
    public void smoothScrollToPosition(RecyclerView recyclerView, RecyclerView.State s, int position) {
        if (setting.swipeableMethod.canSwipeAutomatically()) {
            if (state.canScrollToPosition(position, getItemCount())) {
                smoothScrollToPosition(position);
            }
        }
    }

    @NonNull
    public CardStackSetting getCardStackSetting() {
        return setting;
    }

    @NonNull
    public CardStackState getCardStackState() {
        return state;
    }

    @NonNull
    public CardStackListener getCardStackListener() {
        return listener;
    }

    void updateProportion(float x, float y) {
        if (getTopPosition() < getItemCount()) {
            View view = findViewByPosition(getTopPosition());
            if (view != null) {
                float half = getHeight() / 2.0f;
                state.proportion = -(y - half - view.getTop()) / half;
            }
        }
    }

    private void update(RecyclerView.Recycler recycler) {
        state.width = getWidth();
        state.height = getHeight();

        if (state.isSwipeCompleted(setting)) {
            final Direction direction = state.getDirection();
            // determine if card should be removed
            if (listener.onCardSwipeCompleted(direction)) {
                removeAndRecycleView(getTopView(), recycler);
                state.next(state.status.toAnimatedStatus());
                state.topPosition++;
                state.dx = 0;
                state.dy = 0;
                if (state.topPosition == state.targetPosition) {
                    state.targetPosition = RecyclerView.NO_POSITION;
                }
                new Handler().post(new Runnable() {
                    @Override
                    public void run() {
                        listener.onCardSwiped(direction);
                        View topView = getTopView();
                        if (topView != null) {
                            listener.onCardAppeared(getTopView(), state.topPosition);
                        }
                    }
                });
            } else {
                state.next(state.status.toAnimatedStatus());
                state.dx = 0;
                state.dy = 0;
                state.targetPosition = RecyclerView.NO_POSITION;
            }
        }

        detachAndScrapAttachedViews(recycler);

        final int parentTop = getPaddingTop();
        final int parentLeft = getPaddingLeft();
        final int parentRight = getWidth() - getPaddingLeft();
        final int parentBottom = getHeight() - getPaddingBottom();
        for (int i = state.topPosition; i < state.topPosition + setting.visibleCount && i < getItemCount(); i++) {
            View child = recycler.getViewForPosition(i);
            addView(child, 0);
            measureChildWithMargins(child, 0, 0);
            layoutDecoratedWithMargins(child, parentLeft, parentTop, parentRight, parentBottom);

            int currentIndex = i - state.topPosition;
            if (setting.updateCardUIManually) {
                listener.onUpdateCardUIManually(currentIndex, child);
            } else {
                resetTranslation(child);
                resetScale(child);
                resetOverlay(child);

                if (currentIndex == 0) {
                    updateTranslation(child);
                    resetScale(child);
                    updateOverlay(child);
                } else {
                    updateTranslation(child, currentIndex);
                    updateScale(child, currentIndex);
                    resetOverlay(child);
                }
            }
        }

        if (state.status.isDragging()) {
            listener.onCardDragging(state.getDirection());
        }

        listener.onCardTranslation(state.dx, state.dy);
    }

    private void updateTranslation(View view) {
        view.setTranslationX(state.dx);
        view.setTranslationY(state.dy);
    }

    private void updateTranslation(View view, int index) {
        int nextIndex = index - 1;
        int translationPx = DisplayUtil.dpToPx(context, setting.translationInterval);
        float currentTranslation = index * translationPx;
        float nextTranslation = nextIndex * translationPx;
        float targetTranslation = currentTranslation - (currentTranslation - nextTranslation) * state.getRatio();
        switch (setting.stackFrom) {
            case None:
                // Do nothing
                break;
            case Top:
                view.setTranslationY(-targetTranslation);
                break;
            case TopAndLeft:
                view.setTranslationY(-targetTranslation);
                view.setTranslationX(-targetTranslation);
                break;
            case TopAndRight:
                view.setTranslationY(-targetTranslation);
                view.setTranslationX(targetTranslation);
                break;
            case Bottom:
                view.setTranslationY(targetTranslation);
                break;
            case BottomAndLeft:
                view.setTranslationY(targetTranslation);
                view.setTranslationX(-targetTranslation);
                break;
            case BottomAndRight:
                view.setTranslationY(targetTranslation);
                view.setTranslationX(targetTranslation);
                break;
            case Left:
                view.setTranslationX(-targetTranslation);
                break;
            case Right:
                view.setTranslationX(targetTranslation);
                break;
        }
    }

    private void resetTranslation(View view) {
        view.setTranslationX(0.0f);
        view.setTranslationY(0.0f);
    }

    private void updateScale(View view, int index) {
        int nextIndex = index - 1;
        float currentScale = 1.0f - index * (1.0f - setting.scaleInterval);
        float nextScale = 1.0f - nextIndex * (1.0f - setting.scaleInterval);
        float targetScale = currentScale + (nextScale - currentScale) * state.getRatio();
        switch (setting.stackFrom) {
            case None:
                view.setScaleX(targetScale);
                view.setScaleY(targetScale);
                break;
            case Top:
                view.setScaleX(targetScale);
                // TODO Should handle ScaleY
                break;
            case TopAndLeft:
                view.setScaleX(targetScale);
                // TODO Should handle ScaleY
                break;
            case TopAndRight:
                view.setScaleX(targetScale);
                // TODO Should handle ScaleY
                break;
            case Bottom:
                view.setScaleX(targetScale);
                // TODO Should handle ScaleY
                break;
            case BottomAndLeft:
                view.setScaleX(targetScale);
                // TODO Should handle ScaleY
                break;
            case BottomAndRight:
                view.setScaleX(targetScale);
                // TODO Should handle ScaleY
                break;
            case Left:
                // TODO Should handle ScaleX
                view.setScaleY(targetScale);
                break;
            case Right:
                // TODO Should handle ScaleX
                view.setScaleY(targetScale);
                break;
        }
    }

    private void resetScale(View view) {
        view.setScaleX(1.0f);
        view.setScaleY(1.0f);
    }

    private void updateOverlay(View view) {
        View leftOverlay = view.findViewById(R.id.left_overlay);
        if (leftOverlay != null) {
            leftOverlay.setAlpha(0.0f);
        }
        View rightOverlay = view.findViewById(R.id.right_overlay);
        if (rightOverlay != null) {
            rightOverlay.setAlpha(0.0f);
        }
        View topOverlay = view.findViewById(R.id.top_overlay);
        if (topOverlay != null) {
            topOverlay.setAlpha(0.0f);
        }
        View bottomOverlay = view.findViewById(R.id.bottom_overlay);
        if (bottomOverlay != null) {
            bottomOverlay.setAlpha(0.0f);
        }
        Direction direction = state.getDirection();
        float alpha = setting.overlayInterpolator.getInterpolation(state.getRatio());
        switch (direction) {
            case Left:
                if (leftOverlay != null) {
                    leftOverlay.setAlpha(alpha);
                }
                break;
            case Right:
                if (rightOverlay != null) {
                    rightOverlay.setAlpha(alpha);
                }
                break;
            case Top:
                if (topOverlay != null) {
                    topOverlay.setAlpha(alpha);
                }
                break;
            case Bottom:
                if (bottomOverlay != null) {
                    bottomOverlay.setAlpha(alpha);
                }
                break;
        }
    }

    private void resetOverlay(View view) {
        View leftOverlay = view.findViewById(R.id.left_overlay);
        if (leftOverlay != null) {
            leftOverlay.setAlpha(0.0f);
        }
        View rightOverlay = view.findViewById(R.id.right_overlay);
        if (rightOverlay != null) {
            rightOverlay.setAlpha(0.0f);
        }
        View topOverlay = view.findViewById(R.id.top_overlay);
        if (topOverlay != null) {
            topOverlay.setAlpha(0.0f);
        }
        View bottomOverlay = view.findViewById(R.id.bottom_overlay);
        if (bottomOverlay != null) {
            bottomOverlay.setAlpha(0.0f);
        }
    }

    private void smoothScrollToPosition(int position) {
        if (state.topPosition < position) {
            smoothScrollToNext(position);
        } else {
            smoothScrollToPrevious(position);
        }
    }

    private void smoothScrollToNext(int position) {
        state.proportion = 0.0f;
        state.targetPosition = position;
        CardStackSmoothScroller scroller = new CardStackSmoothScroller(CardStackSmoothScroller.ScrollType.AutomaticSwipe, this);
        scroller.setTargetPosition(state.topPosition);
        startSmoothScroll(scroller);
    }

    private void smoothScrollToPrevious(int position) {
        View topView = getTopView();
        if (topView != null) {
            listener.onCardDisappeared(getTopView(), state.topPosition);
        }

        state.proportion = 0.0f;
        state.targetPosition = position;
        state.topPosition--;
        CardStackSmoothScroller scroller = new CardStackSmoothScroller(CardStackSmoothScroller.ScrollType.AutomaticRewind, this);
        scroller.setTargetPosition(state.topPosition);
        startSmoothScroll(scroller);
    }

    public View getTopView() {
        return findViewByPosition(state.topPosition);
    }

    public int getTopPosition() {
        return state.topPosition;
    }

    public void setTopPosition(int topPosition) {
        state.topPosition = topPosition;
    }

    public void setStackFrom(@NonNull StackFrom stackFrom) {
        setting.stackFrom = stackFrom;
    }

    public void setVisibleCount(@IntRange(from = 1) int visibleCount) {
        if (visibleCount < 1) {
            throw new IllegalArgumentException("VisibleCount must be greater than 0.");
        }
        setting.visibleCount = visibleCount;
    }

    public void setTranslationInterval(@FloatRange(from = 0.0f) float translationInterval) {
        if (translationInterval < 0.0f) {
            throw new IllegalArgumentException("TranslationInterval must be greater than or equal 0.0f");
        }
        setting.translationInterval = translationInterval;
    }

    public void setUpdateCardUIManually(boolean updateCardUIManually) {
        setting.updateCardUIManually = updateCardUIManually;
    }

    public void setScaleInterval(@FloatRange(from = 0.0f) float scaleInterval) {
        if (scaleInterval < 0.0f) {
            throw new IllegalArgumentException("ScaleInterval must be greater than or equal 0.0f.");
        }
        setting.scaleInterval = scaleInterval;
    }

    public void setSwipeThresholdRatioX(@FloatRange(from = 0.0f, to = 1.0f) float swipeThresholdRatioX) {
        if (swipeThresholdRatioX < 0.0f || 1.0f < swipeThresholdRatioX) {
            throw new IllegalArgumentException("swipeThresholdRatioX must be 0.0f to 1.0f.");
        }
        setting.swipeThresholdRatioX = swipeThresholdRatioX;
    }

    public void setSwipeThresholdX(@FloatRange(from = 0.0f) float swipeThresholdX) {
        if (swipeThresholdX < 0.0f) {
            throw new IllegalArgumentException("swipeThresholdX must be greater than 0.0f.");
        }
        setting.swipeThresholdX = swipeThresholdX;
    }

    public void setSwipeThresholdRatioY(@FloatRange(from = 0.0f, to = 1.0f) float swipeThresholdRatioY) {
        if (swipeThresholdRatioY < 0.0f || 1.0f < swipeThresholdRatioY) {
            throw new IllegalArgumentException("swipeThresholdRatioY must be 0.0f to 1.0f.");
        }
        setting.swipeThresholdRatioY = swipeThresholdRatioY;
    }

    public void setSwipeThresholdY(@FloatRange(from = 0.0f) float swipeThresholdY) {
        if (swipeThresholdY < 0.0f) {
            throw new IllegalArgumentException("swipeThresholdY must be greater than 0.0f.");
        }
        setting.swipeThresholdY = swipeThresholdY;
    }

    public void setIsSwipedThresholdX(@FloatRange(from = 0.0f) float isSwipedThresholdX) {
        if (isSwipedThresholdX <= 0.0f) {
            throw new IllegalArgumentException("isSwipedThresholdX must be greater than 0.0f.");
        }
        setting.isSwipedThresholdX = isSwipedThresholdX;
    }

    public void setIsSwipedThresholdMultiplierX(@FloatRange(from = 0.0f) float isSwipedThresholdMultiplierX) {
        if (isSwipedThresholdMultiplierX <= 0.0f) {
            throw new IllegalArgumentException("isSwipedThresholdMultiplierX must be greater than 0.0f.");
        }
        setting.isSwipedThresholdMultiplierX = isSwipedThresholdMultiplierX;
    }

    public void setIsSwipedThresholdY(@FloatRange(from = 0.0f) float isSwipedThresholdY) {
        if (isSwipedThresholdY <= 0.0f) {
            throw new IllegalArgumentException("isSwipedThresholdY must be greater than 0.0f.");
        }
        setting.isSwipedThresholdY = isSwipedThresholdY;
    }

    public void setIsSwipedThresholdMultiplierY(@FloatRange(from = 0.0f) float isSwipedThresholdMultiplierY) {
        if (isSwipedThresholdMultiplierY <= 0.0f) {
            throw new IllegalArgumentException("isSwipedThresholdMultiplierY must be greater than 0.0f.");
        }
        setting.isSwipedThresholdMultiplierY = isSwipedThresholdMultiplierY;
    }

    public void setVelocityLimitForCancel(@IntRange(from = 0) int velocityLimitForCancel) {
        if (velocityLimitForCancel < 0) {
            throw new IllegalArgumentException("velocityLimitForCancel must be greater than 0.0f.");
        }
        setting.velocityLimitForCancel = velocityLimitForCancel;
    }

    public void setDirections(@NonNull List<Direction> directions) {
        setting.directions = directions;
    }

    public void setCanScrollHorizontal(boolean canScrollHorizontal) {
        setting.canScrollHorizontal = canScrollHorizontal;
    }

    public void setCanScrollVertical(boolean canScrollVertical) {
        setting.canScrollVertical = canScrollVertical;
    }

    public void setSwipeableMethod(SwipeableMethod swipeableMethod) {
        setting.swipeableMethod = swipeableMethod;
    }

    public void setSwipeAnimationSetting(@NonNull CardAnimationSetting swipeAnimationSetting) {
        setting.swipeAnimationSetting = swipeAnimationSetting;
    }

    public void setManualSwipeAnimationSetting(@NonNull CardAnimationSetting manualSwipeAnimationSetting) {
        setting.manualSwipeAnimationSetting = manualSwipeAnimationSetting;
    }

    public void setRewindAnimationSetting(@NonNull CardAnimationSetting rewindAnimationSetting) {
        setting.rewindAnimationSetting = rewindAnimationSetting;
    }

    public void setCancelAnimationSetting(@NonNull CardAnimationSetting cancelAnimationSetting) {
        setting.cancelAnimationSetting = cancelAnimationSetting;
    }

    public void setOverlayInterpolator(@NonNull Interpolator overlayInterpolator) {
        setting.overlayInterpolator = overlayInterpolator;
    }

}
