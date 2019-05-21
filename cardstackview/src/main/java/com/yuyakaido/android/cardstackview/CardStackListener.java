package com.yuyakaido.android.cardstackview;

import android.view.View;

public interface CardStackListener {
    CardStackListener DEFAULT = new CardStackListener() {
        @Override
        public void onCardDragging(Direction direction) {
        }

        @Override
        public void onCardTranslation(float dx, float dy) {
        }

        @Override
        public void onCardSwipeCompleted(Direction direction) {

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
        public void onUpdateCardTranslation(int currentIndex, View child) {
        }
    };

    void onCardDragging(Direction direction);

    void onCardTranslation(float dx, float dy);

    void onCardSwipeCompleted(Direction direction);

    void onCardSwiped(Direction direction);

    void onCardRewoundStart();

    void onCardRewound();

    void onCardCanceled();

    void onCardAppeared(View view, int position);

    void onCardDisappeared(View view, int position);

    void onUpdateCardTranslation(int currentIndex, View child);
}
