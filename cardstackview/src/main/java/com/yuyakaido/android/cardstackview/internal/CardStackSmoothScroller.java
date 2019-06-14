package com.yuyakaido.android.cardstackview.internal;

import android.view.View;

import com.yuyakaido.android.cardstackview.CardAnimationSetting;
import com.yuyakaido.android.cardstackview.CardStackLayoutManager;
import com.yuyakaido.android.cardstackview.CardStackListener;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class CardStackSmoothScroller extends RecyclerView.SmoothScroller {

    private ScrollType type;
    private CardStackLayoutManager manager;

    public CardStackSmoothScroller(
            ScrollType type,
            CardStackLayoutManager manager
    ) {
        this.type = type;
        this.manager = manager;
    }

    @Override
    protected void onSeekTargetStep(
            int dx,
            int dy,
            @NonNull RecyclerView.State state,
            @NonNull Action action
    ) {
        if (type == ScrollType.AutomaticRewind) {
            CardAnimationSetting setting = manager.getCardStackSetting().rewindAnimationSetting;
            action.update(
                    -getDx(setting),
                    -getDy(setting),
                    1,
                    setting.getInterpolator()
            );
        }
    }

    @Override
    protected void onTargetFound(
            @NonNull View targetView,
            @NonNull RecyclerView.State state,
            @NonNull Action action
    ) {
        int x = (int) targetView.getTranslationX();
        int y = (int) targetView.getTranslationY();
        AnimationSetting setting;
        switch (type) {
            case AutomaticSwipe:
                setting = manager.getCardStackSetting().swipeAnimationSetting;
                action.update(
                        -getDx(setting),
                        -getDy(setting),
                        setting.getDuration(),
                        setting.getInterpolator()
                );
                break;
            case AutomaticRewind:
                setting = manager.getCardStackSetting().rewindAnimationSetting;
                action.update(
                        x,
                        y,
                        setting.getDuration(),
                        setting.getInterpolator()
                );
                break;
            case ManualSwipe:
                setting = manager.getCardStackSetting().manualSwipeAnimationSetting;
                int dx = x == 0 ? 0 : -x / Math.abs(x) * Math.abs(getDx(setting));
                int dy = y == 0 ? 0 : -y / Math.abs(y) * Math.abs(getDy(setting));
                action.update(
                        dx,
                        dy,
                        setting.getDuration(),
                        setting.getInterpolator()
                );
                break;
            case ManualCancel:
                setting = manager.getCardStackSetting().cancelAnimationSetting;
                action.update(
                        x,
                        y,
                        setting.getDuration(),
                        setting.getInterpolator()
                );
                break;
        }
    }

    @Override
    protected void onStart() {
        CardStackListener listener = manager.getCardStackListener();
        CardStackState state = manager.getCardStackState();
        CardStackState.Status oldStatus = state.status;
        switch (type) {
            case AutomaticSwipe:
                state.next(CardStackState.Status.AutomaticSwipeAnimating);
                if (!oldStatus.isSwipeAnimating()) {
                    listener.onCardSwipeStart(state.getDirection());
                    listener.onCardDisappeared(manager.getTopView(), manager.getTopPosition());
                }
                break;
            case AutomaticRewind:
                state.next(CardStackState.Status.RewindAnimating);
                if (!oldStatus.isRewindAnimating()) {
                    listener.onCardRewoundStart();
                }
                break;
            case ManualSwipe:
                state.next(CardStackState.Status.ManualSwipeAnimating);
                if (!oldStatus.isSwipeAnimating()) {
                    listener.onCardSwipeStart(state.getDirection());
                    listener.onCardDisappeared(manager.getTopView(), manager.getTopPosition());
                }
                break;
            case ManualCancel:
                state.next(CardStackState.Status.CancelAnimating);
                break;
        }
    }

    @Override
    protected void onStop() {
        CardStackListener listener = manager.getCardStackListener();
        switch (type) {
            case AutomaticSwipe:
                // Notify callback from CardStackLayoutManager
                break;
            case AutomaticRewind:
                listener.onCardRewound();
                listener.onCardAppeared(manager.getTopView(), manager.getTopPosition());
                break;
            case ManualSwipe:
                // Notify callback from CardStackLayoutManager
                break;
            case ManualCancel:
                listener.onCardCanceled();
                break;
        }
    }

    private int getDx(AnimationSetting setting) {
        CardStackState state = manager.getCardStackState();
        int dx = 0;
        switch (setting.getDirection()) {
            case Left:
                dx = (int) (-state.width * 1.5f);
                break;
            case Right:
                dx = (int) (state.width * 1.5f);
                break;
            case Top:
            case Bottom:
                dx = 0;
                break;
        }
        return dx;
    }

    private int getDy(AnimationSetting setting) {
        CardStackState state = manager.getCardStackState();
        int dy = 0;
        switch (setting.getDirection()) {
            case Left:
            case Right:
                dy = 0;
                break;
            case Top:
                dy = -state.height * 2;
                break;
            case Bottom:
                dy = state.height * 2;
                break;
        }
        return dy;
    }

    public enum ScrollType {
        AutomaticSwipe,
        AutomaticRewind,
        ManualSwipe,
        ManualCancel
    }

}
