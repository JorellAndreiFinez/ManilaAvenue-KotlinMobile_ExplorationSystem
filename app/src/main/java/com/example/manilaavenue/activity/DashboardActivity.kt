package com.example.manilaavenue.activity

import android.content.Intent
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import com.example.manilaavenue.R
import com.example.manilaavenue.adapter.BestPlaceAdapter
import com.example.manilaavenue.adapter.FeatureSliderAdapter
import com.example.manilaavenue.adapter.SliderAdapter
import com.example.manilaavenue.databinding.ActivityDashboardBinding
import com.example.manilaavenue.model.SliderModel
import com.example.manilaavenue.viewmodel.MainViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlin.math.abs

class DashboardActivity : AppCompatActivity() {

    private val viewModel: MainViewModel by viewModels()
    private lateinit var binding: ActivityDashboardBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private var initialXValue: Float = 0.0f
    private var initialYValue: Float = 0.0f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()

        // Initialize Firestore
        db = FirebaseFirestore.getInstance()

        initBanners()
        initFeatureToday()
        initBestPlace()
        initManilaFeature()

        // Set click listeners for bottom navigation items
        findViewById<View>(R.id.optionExplore).setOnClickListener {
            navigateToExplore()
        }

        findViewById<View>(R.id.optionPlace).setOnClickListener {
            navigateToPlace()
        }

        findViewById<View>(R.id.optionFavorites).setOnClickListener {
            navigateToFavorites()
        }

        findViewById<View>(R.id.optionBlog).setOnClickListener {
            navigateToBlog()
        }

        findViewById<View>(R.id.optionAccount).setOnClickListener {
            navigateToAccount()
        }

        findViewById<Button>(R.id.seeMoreBtn).setOnClickListener {
            val intent = Intent(this@DashboardActivity, ManilenoActivity::class.java)
            startActivity(intent)
        }

        // Add touch listener to detect horizontal swipes
        binding.viewPagerSlider2.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    initialXValue = event.x
                    initialYValue = event.y
                }
                MotionEvent.ACTION_MOVE -> {
                    val diffX = event.x - initialXValue
                    val diffY = event.y - initialYValue
                    if (abs(diffX) > abs(diffY)) {
                        // Detected horizontal swipe
                        Toast.makeText(this, "Please slide down to up", Toast.LENGTH_SHORT).show()
                        return@setOnTouchListener true
                    }
                }
            }
            false
        }
    }

    private fun initBestPlace() {
        binding.progressBarPlace.visibility = View.VISIBLE
        viewModel.bestPlace.observe(this, Observer {
            binding.viewBestPlace.layoutManager =
                GridLayoutManager(this@DashboardActivity, 2)
            binding.viewBestPlace.adapter = BestPlaceAdapter(it)
            binding.progressBarPlace.visibility = View.GONE
        })
        viewModel.loadBestPlace()
    }

    private fun navigateToAccount() {
        val intent = Intent(this@DashboardActivity, AccountActivity::class.java)
        startActivity(intent)
    }

    private fun navigateToBlog() {
        val intent = Intent(this@DashboardActivity, CategoryPlaceActivity::class.java)
        startActivity(intent)
    }

    private fun navigateToFavorites() {
        val intent = Intent(this@DashboardActivity, LocationActivity::class.java)
        startActivity(intent)
    }

    private fun navigateToPlace() {
        val intent = Intent(this@DashboardActivity, PlaceActivity::class.java)
        startActivity(intent)
    }

    private fun navigateToExplore() {
        Toast.makeText(this@DashboardActivity, "You are now in the Dashboard", Toast.LENGTH_LONG).show()
    }

    private fun initBanners() {
        binding.progressBarBanner.visibility = View.VISIBLE
        viewModel.banners.observe(this, Observer { banners ->
            updateBanners(banners)
            binding.progressBarBanner.visibility = View.GONE
        })
        viewModel.loadBanners()
    }

    private fun initFeatureToday() {
        binding.progressBarFeature.visibility = View.VISIBLE
        viewModel.features.observe(this, Observer { featuress ->
            updateBanners2(featuress)
            binding.progressBarFeature.visibility = View.GONE
        })
        viewModel.loadFeature()
    }

    private fun initManilaFeature() {
        binding.progressBarIntra.visibility = View.VISIBLE
        viewModel.manilafeature.observe(this, Observer {
            binding.viewIntramuros.layoutManager =
                LinearLayoutManager(this@DashboardActivity, LinearLayoutManager.HORIZONTAL, false)
            binding.viewIntramuros.adapter = BestPlaceAdapter(it)
            binding.progressBarIntra.visibility = View.GONE
        })
        viewModel.loadManilaFeature()
    }

    private fun updateBanners(images: List<SliderModel>) {
        binding.viewPagerSlider.apply {
            adapter = SliderAdapter(images, this)
            clipToPadding = false
            clipChildren = false
            offscreenPageLimit = 3
            getChildAt(0).overScrollMode = RecyclerView.OVER_SCROLL_NEVER

            val compositePageTransformer = CompositePageTransformer().apply {
                addTransformer(MarginPageTransformer(40))
                addTransformer { page, position ->
                    val r = 1 - abs(position)
                    page.scaleY = 0.85f + r * 0.15f
                }
            }
            setPageTransformer(compositePageTransformer)
        }

        if (images.size > 1) {
            binding.dotIndicator.apply {
                visibility = View.VISIBLE
                attachTo(binding.viewPagerSlider)
            }
        } else {
            binding.dotIndicator.visibility = View.GONE
        }
    }

    private fun updateBanners2(images: List<SliderModel>) {
        binding.viewPagerSlider2.apply {
            adapter = FeatureSliderAdapter(images, this)
            clipToPadding = false
            clipChildren = false
            offscreenPageLimit = 3
            getChildAt(0).overScrollMode = RecyclerView.OVER_SCROLL_NEVER
            setPageTransformer(VerticalPageTransformer())
        }

        if (images.size > 1) {
            binding.dotIndicator2.apply {
                visibility = View.VISIBLE
                attachTo(binding.viewPagerSlider2)
            }
        } else {
            binding.dotIndicator2.visibility = View.GONE
        }
    }

    class VerticalPageTransformer : ViewPager2.PageTransformer {
        override fun transformPage(view: View, position: Float) {
            view.apply {
                translationY = position * height
                alpha = 1 - Math.abs(position)
            }
        }
    }
}
