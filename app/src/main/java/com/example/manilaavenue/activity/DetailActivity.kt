package com.example.manilaavenue.activity

import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.manilaavenue.R
import com.example.manilaavenue.adapter.PicListAdapter
import com.example.manilaavenue.databinding.ActivityDetailBinding
import com.example.manilaavenue.model.ItemsModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore

class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding
    private lateinit var item: ItemsModel
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var favoritesCollectionRef: CollectionReference // Reference to user's favorites collection

    private lateinit var picListAdapter: PicListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        // Retrieve item from intent extras
        item = intent.getParcelableExtra("object")!!

        // Initialize Firestore reference for current user's favorite item
        initFirestore()

        // Initialize UI components
        initUI()

        // Check if item is already favorited by the user
        checkFavoriteStatus()

//        // Handle favorite icon click
//        binding.favoriteicon.setOnClickListener {
//            toggleFavoriteStatus()
//        }

        // Set up RecyclerView for picList
        setUpPicListRecyclerView()
    }

    private fun initFirestore() {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val userId = currentUser.uid
            favoritesCollectionRef = firestore.collection("users")
                .document(userId)
                .collection("Goal") // New collection for user's favorites
        }
    }

    private fun initUI() {
        binding.titleTxt.text = item.title
        binding.descriptionTxt.text = item.description
        binding.ratingTxt.text = "${item.rating} "
        binding.locationTxt.text = item.location

//        // Set favorite icon based on item's favoriteIconId
//        val favoriteIconResourceId = getFavoriteIconResourceId(item.favoriteIconId)
//        binding.favoriteicon.setImageResource(favoriteIconResourceId)

        binding.backBtn.setOnClickListener { finish() }

        // Load main image into picMain ImageView
        Glide.with(this)
            .load(item.picUrl[0]) // Load first image from picUrl list
            .into(binding.picMain)
    }

    private fun getFavoriteIconResourceId(favoriteIconId: String): Int {
        return when (favoriteIconId) {
            "fav_icon_1" -> R.drawable.fav_icon_1 // Replace with your actual drawable resource IDs
            "fav_icon_2" -> R.drawable.fav_icon_2
            // Add more cases as needed
            else -> R.drawable.fav_icon_unselected // Default to unselected icon
        }
    }

    private fun checkFavoriteStatus() {
        if (::favoritesCollectionRef.isInitialized) {
            favoritesCollectionRef
                .whereEqualTo("itemID", item.itemID.toString())
                .get()
                .addOnSuccessListener { documentsSnapshot ->
                    if (documentsSnapshot.isEmpty) {
                        // Item is not favorited
//                        binding.favoriteicon.setImageResource(R.drawable.fav_icon_unselected)
                    } else {
                        // Item is already favorited
//                        binding.favoriteicon.setImageResource(R.drawable.fav_icon)
                    }
                }
                .addOnFailureListener { exception ->
                    Toast.makeText(this@DetailActivity, "Failed to check favorite status", Toast.LENGTH_SHORT).show()
                    Log.e("DetailActivity", "Error checking favorite status", exception)
                }
        } else {
            Toast.makeText(this@DetailActivity, "User ID or item ID is invalid", Toast.LENGTH_SHORT).show()
        }
    }

    private fun toggleFavoriteStatus() {
        if (::favoritesCollectionRef.isInitialized) {
            val favoriteQuery = favoritesCollectionRef.whereEqualTo("itemID", item.itemID.toString())
            favoriteQuery.get()
                .addOnSuccessListener { documentsSnapshot ->
                    if (documentsSnapshot.isEmpty) {
                        // Item is not favorited, so save it
                        saveToFavorites()
                    } else {
                        // Item is already favorited, so remove it
                        val favoriteDoc = documentsSnapshot.documents[0] // Get the first document (should be the only one)
                        favoriteDoc.reference.delete()
                            .addOnSuccessListener {
//                                binding.favoriteicon.setImageResource(R.drawable.fav_icon_unselected)
                                Toast.makeText(this@DetailActivity, "Removed from favorites", Toast.LENGTH_SHORT).show()
                            }
                            .addOnFailureListener { exception ->
                                Toast.makeText(this@DetailActivity, "Failed to remove from favorites", Toast.LENGTH_SHORT).show()
                                Log.e("DetailActivity", "Error removing from favorites", exception)
                            }
                    }
                }
                .addOnFailureListener { exception ->
                    Toast.makeText(this@DetailActivity, "Failed to toggle favorite status", Toast.LENGTH_SHORT).show()
                    Log.e("DetailActivity", "Error toggling favorite status", exception)
                }
        } else {
            Toast.makeText(this@DetailActivity, "User ID or item ID is invalid", Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveToFavorites() {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val data = HashMap<String, Any>()
            data["itemID"] = item.itemID.toString()
            data["title"] = item.title.toUpperCase()
            data["description"] = item.description
            data["favoriteIconId"] = item.favoriteIconId // Include favoriteIconId in data to be saved

            val newFavoriteRef = favoritesCollectionRef.document() // Generate unique ID
            newFavoriteRef.set(data)
                .addOnSuccessListener {
//                    binding.favoriteicon.setImageResource(R.drawable.fav_icon)
                    Toast.makeText(this@DetailActivity, "Added to favorites", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { exception ->
                    Toast.makeText(this@DetailActivity, "Failed to add to favorites", Toast.LENGTH_SHORT).show()
                    Log.e("DetailActivity", "Error adding to favorites", exception)
                }
        } else {
            Toast.makeText(this@DetailActivity, "User ID is invalid", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setUpPicListRecyclerView() {
        picListAdapter = PicListAdapter(item.picUrl, binding.picMain)
        binding.picList.apply {
            layoutManager = LinearLayoutManager(this@DetailActivity, LinearLayoutManager.HORIZONTAL, false)
            adapter = picListAdapter
        }
    }
}
