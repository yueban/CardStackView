package com.yuyakaido.android.cardstackview.sample

import android.support.v4.view.PagerAdapter
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import com.bumptech.glide.Glide

class SpotAdapter(
        private val spots: List<Spot>,
        private val listener: Listener
) : PagerAdapter() {

    interface Listener {
        fun onClick(area: TouchArea)
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val spot = spots[position]

        val inflater = LayoutInflater.from(container.context)
        val root = inflater.inflate(R.layout.item_spot, container, false)
        val image = root.findViewById<ImageView>(R.id.item_image)

        Glide.with(image)
                .load(spot.url)
                .into(image)
        root.setOnClickListener { v ->
            Toast.makeText(v.context, spot.name, Toast.LENGTH_SHORT).show()
        }
        root.setOnTouchListener { view, event ->
            when (event.action) {
                MotionEvent.ACTION_UP -> {
                    val area = TouchArea.fromCoordinate(event.x, event.y, view.width, view.height)
                    listener.onClick(area)
                    true
                }
                else -> false
            }
        }

        container.addView(root)

        return root
    }

    override fun isViewFromObject(view: View, obj: Any): Boolean {
        return view == obj
    }

    override fun getCount(): Int {
        return spots.size
    }

    override fun destroyItem(container: ViewGroup, position: Int, obj: Any) {
        container.removeView(obj as View)
    }

}
