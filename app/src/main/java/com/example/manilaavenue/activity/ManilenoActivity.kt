package com.example.manilaavenue.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.viewpager2.widget.ViewPager2
import com.example.manilaavenue.R
import com.example.manilaavenue.adapter.GifAdapter

class ManilenoActivity : AppCompatActivity() {

    private lateinit var viewPagerGif: ViewPager2
    private lateinit var gifAdapter: GifAdapter
    private lateinit var actionButton: Button

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manileno)

        // Ensure that the root view is correctly referenced
        val rootView: View = findViewById(R.id.main_layout)

        ViewCompat.setOnApplyWindowInsetsListener(rootView) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        viewPagerGif = findViewById(R.id.viewPagerGif)
        actionButton = findViewById(R.id.actionButton)

        // Initially hide the button
        actionButton.visibility = View.GONE
        actionButton.setOnClickListener{
            finish()
        }

        // List of GIF URLs
        val gifUrls = listOf(
            "https://firebasestorage.googleapis.com/v0/b/manilaavenue-87263.appspot.com/o/person_1.gif?alt=media&token=04e895ea-0da0-428d-a2f8-f3139eb682af",
            "https://firebasestorage.googleapis.com/v0/b/manilaavenue-87263.appspot.com/o/person_2.gif?alt=media&token=04e895ea-0da0-428d-a2f8-f3139eb682af",
            "https://firebasestorage.googleapis.com/v0/b/manilaavenue-87263.appspot.com/o/person_3.gif?alt=media&token=04e895ea-0da0-428d-a2f8-f3139eb682af",
            "https://firebasestorage.googleapis.com/v0/b/manilaavenue-87263.appspot.com/o/person_4.gif?alt=media&token=04e895ea-0da0-428d-a2f8-f3139eb682af",
            "https://firebasestorage.googleapis.com/v0/b/manilaavenue-87263.appspot.com/o/person_5.gif?alt=media&token=04e895ea-0da0-428d-a2f8-f3139eb682af",
            "https://firebasestorage.googleapis.com/v0/b/manilaavenue-87263.appspot.com/o/person_6.gif?alt=media&token=04e895ea-0da0-428d-a2f8-f3139eb682af"
        )

        gifAdapter = GifAdapter(this, gifUrls)
        viewPagerGif.adapter = gifAdapter

        // Set up a page change callback to monitor the current page
        viewPagerGif.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                if (position == gifUrls.size - 1) {
                    actionButton.visibility = View.VISIBLE
                } else {
                    // Hide the button otherwise
                    actionButton.visibility = View.GONE
                }
            }
        })
    }
}
