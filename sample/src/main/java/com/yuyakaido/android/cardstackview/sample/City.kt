package com.yuyakaido.android.cardstackview.sample

data class City(
        val id: Long = counter++,
        val name: String,
        val spots: List<Spot>
) {
    companion object {
        private var counter = 0L
    }
}