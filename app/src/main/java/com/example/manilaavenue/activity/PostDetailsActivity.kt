package com.example.manilaavenue.activity

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.manilaavenue.R
import com.example.manilaavenue.adapter.PostImageListAdapter
import com.example.manilaavenue.databinding.ActivityPostDetailsBinding
import com.example.manilaavenue.model.Post
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore

class PostDetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPostDetailsBinding
    private lateinit var item: Post
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var favoritesCollectionRef: CollectionReference // Reference to user's favorites collection

    private lateinit var picListAdapter: PostImageListAdapter
    private lateinit var postId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPostDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        // Retrieve item from intent extras
        item = intent.getParcelableExtra("object")!!
        postId = intent.getStringExtra("postId")!!

        // Initialize Firestore reference for current user's favorite item
        initFirestore()

        // Initialize UI components
        initUI()

        // Set up RecyclerView for picList
        setUpPicListRecyclerView()

        binding.editPostBtn.setOnClickListener {
            val userId = FirebaseAuth.getInstance().currentUser?.uid
            val postRef = FirebaseDatabase.getInstance().getReference("users").child(userId!!).child("Posts")

            postRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (postSnapshot in snapshot.children) {
                        val postId = postSnapshot.key // Fetch the post ID from your database reference
                        val intent = Intent(this@PostDetailsActivity, EditPostActivity::class.java)
                        intent.putExtra("postId", postId) // Pass the post ID
                        startActivity(intent)
                        break // Assuming you only need the first post ID found
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle error
                }
            })
        }

        val deleteBtn: ImageButton = findViewById(R.id.deleteBtn)
        deleteBtn.setOnClickListener {
            showDeleteConfirmationDialog()
        }

        findViewById<ImageView>(R.id.backBtn).setOnClickListener{
            finish()
        }
    }

    private fun showDeleteConfirmationDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Delete Post")
        builder.setMessage("Are you sure you want to delete this post?")
        builder.setPositiveButton("Delete") { dialog, which ->
            deletePost(postId)
        }
        builder.setNegativeButton("Cancel") { dialog, which ->
            dialog.dismiss()
        }
        val dialog = builder.create()
        dialog.show()
    }

    private fun deletePost(postId: String) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return

        FirebaseDatabase.getInstance().getReference("users")
            .child(userId)
            .child("Posts")
            .child(postId)
            .removeValue()
            .addOnSuccessListener {
                Toast.makeText(this@PostDetailsActivity, "Post deleted successfully", Toast.LENGTH_SHORT).show()
                finish() // Close the activity after deletion
            }
            .addOnFailureListener {
                Toast.makeText(this@PostDetailsActivity, "Failed to delete post", Toast.LENGTH_SHORT).show()
            }
    }

    private fun initFirestore() {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val userId = currentUser.uid
            favoritesCollectionRef = firestore.collection("users")
                .document(userId)
                .collection("Posts") // Assuming "Posts" is the collection containing user's posts
        }
    }

    private fun initUI() {
        binding.titleTxt.text = item.title
        binding.descriptionTxt.text = item.description

        binding.backBtn.setOnClickListener { finish() }

        // Check if item.imageUrls is not empty before loading main image
        if (item.imageUrls.isNotEmpty()) {
            // Load main image into picMain ImageView
            Glide.with(this)
                .load(item.imageUrls[0]) // Load first image from imageUrls list
                .into(binding.picMain)
        } else {
            // Handle case where there are no images in item.imageUrls
            // For example, set a placeholder image or hide picMain
            Glide.with(this)
                .load(R.drawable.ic_testicon) // Placeholder image resource
                .into(binding.picMain)
        }
    }

    private fun setUpPicListRecyclerView() {
        picListAdapter = PostImageListAdapter(item.imageUrls, binding.picMain)
        binding.picList.apply {
            layoutManager = LinearLayoutManager(this@PostDetailsActivity, LinearLayoutManager.HORIZONTAL, false)
            adapter = picListAdapter
        }
    }
}
