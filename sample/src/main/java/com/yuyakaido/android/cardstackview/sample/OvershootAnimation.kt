package com.yuyakaido.android.cardstackview.sample

import android.graphics.Camera
import android.view.animation.Animation
import android.view.animation.Transformation

class OvershootAnimation(
        private val centerX: Float,
        private val centerY: Float,
        private val direction: Int
) : Animation() {

    companion object {
        private const val FROM_DEGREES = 0.0f
        private const val TO_DEGREES = 1.0f
        private const val DURATION = 100L
        const val DIRECTION_LEFT = -1
        const val DIRECTION_RIGHT = 1
    }

    override fun initialize(width: Int, height: Int, parentWidth: Int, parentHeight: Int) {
        super.initialize(width, height, parentWidth, parentHeight)
        duration = DURATION
        repeatMode = Animation.REVERSE
        repeatCount = 1
    }

    override fun applyTransformation(interpolatedTime: Float, t: Transformation) {
        val degrees = FROM_DEGREES + (direction * TO_DEGREES - FROM_DEGREES) * interpolatedTime

        val camera = Camera()
        camera.save()
        camera.rotateY(degrees)
        camera.getMatrix(t.matrix)
        camera.restore()

        t.matrix.preTranslate(-centerX, -centerY)
        t.matrix.postTranslate(centerX, centerY)
    }

}