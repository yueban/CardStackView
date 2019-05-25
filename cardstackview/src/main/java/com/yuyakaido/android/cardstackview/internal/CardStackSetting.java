package com.yuyakaido.android.cardstackview.internal;

import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;

import com.yuyakaido.android.cardstackview.CardAnimationSetting;
import com.yuyakaido.android.cardstackview.Direction;
import com.yuyakaido.android.cardstackview.StackFrom;
import com.yuyakaido.android.cardstackview.SwipeableMethod;

import java.util.List;

public class CardStackSetting {
    public StackFrom stackFrom = StackFrom.None;
    public int visibleCount = 3;
    public float translationInterval = 8.0f;
    public boolean updateCardUIManually = false;
    public float scaleInterval = 0.95f; // 0.0f - 1.0f
    public float swipeThresholdRatio = 0.3f; // 0.0f - 1.0f
    public float swipeThreshold = 100f; // px
    public int velocityLimitForCancel = 0;
    public List<Direction> directions = Direction.HORIZONTAL;
    public boolean canScrollHorizontal = true;
    public boolean canScrollVertical = true;
    public SwipeableMethod swipeableMethod = SwipeableMethod.AutomaticAndManual;
    public CardAnimationSetting swipeAnimationSetting = new CardAnimationSetting.Builder().build();
    public CardAnimationSetting manualSwipeAnimationSetting = new CardAnimationSetting.Builder().build();
    public CardAnimationSetting rewindAnimationSetting = new CardAnimationSetting.Builder().build();
    public CardAnimationSetting cancelAnimationSetting = new CardAnimationSetting.Builder().build();
    public Interpolator overlayInterpolator = new LinearInterpolator();
}
