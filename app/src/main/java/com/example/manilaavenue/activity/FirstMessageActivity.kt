package com.example.manilaavenue.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.Html
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.example.manilaavenue.R
import com.example.manilaavenue.adapter.ViewPagerAdapter

class FirstMessageActivity : AppCompatActivity() {
    private lateinit var slideViewPager: ViewPager
    private lateinit var dotIndicator: LinearLayout
    private lateinit var nextButton: ImageButton
    private lateinit var dots: Array<TextView?>
    private lateinit var viewPagerAdapter: ViewPagerAdapter

    private val viewPagerListener = object : ViewPager.OnPageChangeListener {
        override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}

        override fun onPageSelected(position: Int) {
            setDotIndicator(position)
            if (position == 2) {
                nextButton.setImageResource(R.drawable.ic_rightarrow)  // Replace with your "done" icon resource
            } else {
                nextButton.setImageResource(R.drawable.baseline_chevron_right_24)  // Replace with your "next" icon resource
            }
        }

        override fun onPageScrollStateChanged(state: Int) {}
    }

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_first_message)

        nextButton = findViewById(R.id.nextButton)
        slideViewPager = findViewById(R.id.slideViewPager)
        dotIndicator = findViewById(R.id.dotIndicator)

        nextButton.setOnClickListener {
            if (getItem(0) < 2) {
                slideViewPager.setCurrentItem(getItem(1), true)
            } else {
                val intent = Intent(this, GetStartedActivity::class.java)
                startActivity(intent)
                finish()
            }
        }

        viewPagerAdapter = ViewPagerAdapter(this)
        slideViewPager.adapter = viewPagerAdapter
        setDotIndicator(0)
        slideViewPager.addOnPageChangeListener(viewPagerListener)
    }

    private fun setDotIndicator(position: Int) {
        dots = arrayOfNulls(3)
        dotIndicator.removeAllViews()
        for (i in dots.indices) {
            dots[i] = TextView(this).apply {
                text = Html.fromHtml("&#8226;", Html.FROM_HTML_MODE_LEGACY)
                textSize = 35f
                setTextColor(resources.getColor(R.color.Khaki, applicationContext.theme))
            }
            dotIndicator.addView(dots[i])
        }
        dots[position]?.setTextColor(resources.getColor(R.color.Yellow, applicationContext.theme))
    }

    private fun getItem(i: Int): Int {
        return slideViewPager.currentItem + i
    }
}
