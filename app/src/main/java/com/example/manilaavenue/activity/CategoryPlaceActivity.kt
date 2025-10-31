package com.example.manilaavenue.activity

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import com.example.manilaavenue.R
import com.example.manilaavenue.adapter.AllPlaceAdapter
import com.example.manilaavenue.databinding.ActivityCategoryPlaceBinding
import com.example.manilaavenue.databinding.ActivityPlaceBinding
import com.example.manilaavenue.viewmodel.MainViewModel
import com.google.firebase.auth.FirebaseAuth

class CategoryPlaceActivity : AppCompatActivity() {

    private val viewModel: MainViewModel by viewModels()
    private lateinit var binding: ActivityCategoryPlaceBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var adapter: AllPlaceAdapter
    private var searchBy: String = "Title"
    private var currentCategory: String? = null  // Track current selected category

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityCategoryPlaceBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()

        initAllPlace()
        setupCategoryButtons()

        binding.backBtn.setOnClickListener { finish() }
    }

    private fun showFancyToast(message: String) {
        val inflater = layoutInflater
        val layout = inflater.inflate(R.layout.toast_hover, null)

        val textToast = layout.findViewById<TextView>(R.id.textToast)
        textToast.text = message

        val toast = Toast(applicationContext)
        toast.duration = Toast.LENGTH_SHORT
        toast.view = layout
        toast.show()
    }



    // Initialize RecyclerView and observe data from ViewModel
    private fun initAllPlace() {
        binding.progressBarAllPlace.visibility = View.VISIBLE
        viewModel.allplace.observe(this, Observer {
            binding.viewAllplace.layoutManager = GridLayoutManager(this@CategoryPlaceActivity, 1)
            adapter = AllPlaceAdapter(it.toMutableList())
            binding.viewAllplace.adapter = adapter
            binding.progressBarAllPlace.visibility = View.GONE
        })
        viewModel.loadAllPlace()
    }


    // Setup click listeners for category image buttons
    private fun setupCategoryButtons() {
        binding.categoryResto.setOnClickListener {
            binding.categoryLabel.text = "Restaurant".toUpperCase()
            filterByCategory("Restaurant")
            showFancyToast("Restaurants and Dining")
            true
        }
        binding.categoryPark.setOnClickListener {
            binding.categoryLabel.text = "Park".toUpperCase()

            filterByCategory("Park")
            showFancyToast("Parks and Outdoor Spaces")
            true
        }
        binding.categoryHiddenGems.setOnClickListener {
            binding.categoryLabel.text = "Hidden Gem".toUpperCase()

            filterByCategory("Hidden Gem")
            showFancyToast("Hidden Gems and Unique Spots")
            true
        }
        binding.categoryMarket.setOnClickListener {
            binding.categoryLabel.text = "Market".toUpperCase()

            filterByCategory("Market")
            showFancyToast("Markets and Shopping Areas")
            true
        }
        binding.categoryMuseum.setOnClickListener {
            binding.categoryLabel.text = "Museum".toUpperCase()
            filterByCategory("Museum")
            showFancyToast("Museums and Cultural Sites")
            true
        }
        binding.categoryOthers.setOnClickListener {
            binding.categoryLabel.text = "Others".toUpperCase()
            filterByCategory("Others")
            showFancyToast("Other Places of Interest")
            true
        }
    }

    // Function to filter places by category
    private fun filterByCategory(category: String) {
        currentCategory = category
        viewModel.allplace.value?.let { places ->
            val filteredPlaces = places.filter { it.category.equals(category, ignoreCase = true) }
            adapter.updateList(filteredPlaces)
        }
    }

    // Function to filter places by search query
    private fun filterPlaces(query: String) {
        viewModel.allplace.value?.let { places ->
            val filteredPlaces = when (searchBy) {
                "Category" -> places.filter { it.category.contains(query, ignoreCase = true) }
                else -> places
            }
            currentCategory?.let { category ->
                if (category.isNotEmpty()) {
                    adapter.updateList(filteredPlaces.filter { it.category.equals(category, ignoreCase = true) })
                } else {
                    adapter.updateList(filteredPlaces)
                }
            } ?: run {
                adapter.updateList(filteredPlaces)
            }
        }
    }

    // Navigation functions for bottom navigation items
    private fun navigateToAccount() {
        val intent = Intent(this@CategoryPlaceActivity, AccountActivity::class.java)
        startActivity(intent)
    }

    private fun navigateToFavorites() {
        val intent = Intent(this@CategoryPlaceActivity, LocationActivity::class.java)
        startActivity(intent)
    }

    private fun navigateToPlace() {
        // No need to navigate to the same activity
    }

    private fun navigateToExplore() {
        val intent = Intent(this@CategoryPlaceActivity, DashboardActivity::class.java)
        startActivity(intent)
        finish()
    }
}
