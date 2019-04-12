package com.yuyakaido.android.cardstackview.sample

enum class TouchArea {
    Left,
    Right;

    companion object {
        fun fromCoordinate(x: Float, y: Float, width: Int, height: Int): TouchArea {
            return when {
                x < width / 2 -> Left
                else -> Right
            }
        }
    }
}