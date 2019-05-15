package com.yuyakaido.android.cardstackview;

import android.view.View;

public interface CardStackListener {
    CardStackListener DEFAULT = new CardStackListener() {
        @Override
        public void onCardDragging(Direction direction, float ratio) {
        }

        @Override
        public void onCardTranslation(float dx, float dy) {
        }

        @Override
        public void onCardSwiped(Direction direction) {
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
    };

    void onCardDragging(Direction direction, float ratio);

    void onCardTranslation(float dx, float dy);

    void onCardSwiped(Direction direction);

    void onCardRewound();

    void onCardCanceled();

    void onCardAppeared(View view, int position);

    void onCardDisappeared(View view, int position);
}
