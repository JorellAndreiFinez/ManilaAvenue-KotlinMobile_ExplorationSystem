package com.example.manilaavenue.activity

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import com.example.manilaavenue.R
import com.example.manilaavenue.adapter.AllPlaceAdapter
import com.example.manilaavenue.databinding.ActivityPlaceBinding
import com.example.manilaavenue.viewmodel.MainViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class PlaceActivity : AppCompatActivity() {

    private val viewModel: MainViewModel by viewModels()
    private lateinit var binding: ActivityPlaceBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var adapter: AllPlaceAdapter
    private var searchBy: String = "Title"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityPlaceBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()

        // Initialize Firestore
        db = FirebaseFirestore.getInstance()

        initAllPlace()
        setupSearch()
        setupSpinner()

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

        binding.backBtn.setOnClickListener { finish() }

    }

    private fun navigateToAccount() {
        val intent = Intent(this@PlaceActivity, AccountActivity::class.java)
        startActivity(intent)
    }

    private fun navigateToBlog() {
        val intent = Intent(this@PlaceActivity, BlogActivity::class.java)
        startActivity(intent)
    }

    private fun navigateToFavorites() {
        val intent = Intent(this@PlaceActivity, LocationActivity::class.java)
        startActivity(intent)
    }

    private fun navigateToPlace() {
        Toast.makeText(this@PlaceActivity, "You are now in the All Place", Toast.LENGTH_LONG).show()
    }

    private fun navigateToExplore() {
        val intent = Intent(this@PlaceActivity, DashboardActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun initAllPlace() {
        binding.progressBarAllPlace.visibility = View.VISIBLE
        viewModel.allplace.observe(this, Observer {
            binding.viewAllplace.layoutManager = GridLayoutManager(this@PlaceActivity, 1)
            adapter = AllPlaceAdapter(it.toMutableList())
            binding.viewAllplace.adapter = adapter
            binding.progressBarAllPlace.visibility = View.GONE
        })
        viewModel.loadAllPlace()
    }

    private fun setupSearch() {
        binding.searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filterPlaces(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun setupSpinner() {
        val adapter = ArrayAdapter.createFromResource(
            this,
            R.array.search_options,
            android.R.layout.simple_spinner_item
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.searchSpinner.adapter = adapter
        binding.searchSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                searchBy = parent?.getItemAtPosition(position).toString()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun filterPlaces(query: String) {
        viewModel.allplace.value?.let { places ->
            val filteredPlaces = when (searchBy) {
                "Title" -> places.filter { it.title.contains(query, ignoreCase = true) }
                "Location" -> places.filter { it.location.contains(query, ignoreCase = true) }
                "Category" -> places.filter { it.category.contains(query, ignoreCase = true) }
                else -> places
            }
            adapter.updateList(filteredPlaces)
        }
    }
}
