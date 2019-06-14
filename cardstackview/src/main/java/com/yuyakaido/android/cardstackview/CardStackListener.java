package com.yuyakaido.android.cardstackview;

import android.view.View;

public interface CardStackListener {
    CardStackListener DEFAULT = new DefaultCardStackListener();

    void onCardDragging(Direction direction);

    void onCardTranslation(float dx, float dy);

    /**
     * @return 'true' to remove swiped card, otherwise 'false'.
     */
    boolean onCardSwipeCompleted(Direction direction);

    void onCardSwiped(Direction direction);

    void onCardRewoundStart();

    void onCardRewound();

    void onCardCanceled();

    void onCardAppeared(View view, int position);

    void onCardDisappeared(View view, int position);

    void onUpdateCardUIManually(int currentIndex, View child);

    public class DefaultCardStackListener implements CardStackListener {
        @Override
        public void onCardDragging(Direction direction) {
        }

        @Override
        public void onCardTranslation(float dx, float dy) {
        }

        @Override
        public boolean onCardSwipeCompleted(Direction direction) {
            return true;
        }

        @Override
        public void onCardSwiped(Direction direction) {
        }

        @Override
        public void onCardRewoundStart() {

        }

        @Override
        public void onCardRewound() {
        }

        @Override
        public void onCardCanceled() {
        }

        @Override
        public void onCardAppeared(View view, int position) {
        }

        @Override
        public void onCardDisappeared(View view, int position) {
        }

        @Override
        public void onUpdateCardUIManually(int currentIndex, View child) {
        }
    }
}
