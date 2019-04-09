package com.yuyakaido.android.cardstackview.sample

import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CardStackViewTest {

    @Test
    fun contextTest() {
        val appContext = InstrumentationRegistry.getTargetContext()
        Assert.assertEquals("com.yuyakaido.android.cardstackview.sample", appContext.packageName)
    }

}