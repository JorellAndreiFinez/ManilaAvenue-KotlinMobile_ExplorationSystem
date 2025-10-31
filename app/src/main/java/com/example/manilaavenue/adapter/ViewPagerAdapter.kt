package com.example.manilaavenue.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.viewpager.widget.PagerAdapter
import com.example.manilaavenue.R


class ViewPagerAdapter(var context: Context) : PagerAdapter() {
    var sliderAllImages = intArrayOf(
        R.drawable.onboarding1,
        R.drawable.onboarding2,
        R.drawable.onboarding3
    )
    var sliderAllTitle = intArrayOf(
        R.string.message1,
        R.string.message2,
        R.string.message3
    )
    var sliderAllDesc = intArrayOf(
        R.string.submessage1,
        R.string.submessage2,
        R.string.submessage3
    )

    override fun getCount(): Int {
        return sliderAllTitle.size
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object` as LinearLayout
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val layoutInflater =
            context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view: View = layoutInflater.inflate(R.layout.slide, container, false)
        val sliderImage = view.findViewById<View>(R.id.sliderImage) as LinearLayout
//        val sliderTitle = view.findViewById<View>(R.id.sliderTitle) as TextView
//        val sliderDesc = view.findViewById<View>(R.id.sliderDesc) as TextView
        sliderImage.setBackgroundResource(sliderAllImages[position])
//        sliderImage.setImageResource()
//        sliderTitle.setText(sliderAllTitle[position])
//        sliderDesc.setText(sliderAllDesc[position])
        container.addView(view)
        return view
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as LinearLayout)
    }
}