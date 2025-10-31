package com.example.manilaavenue.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.marginBottom
import androidx.core.view.setMargins
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.manilaavenue.R
import com.example.manilaavenue.activity.AccountSettingsActivity
import com.example.manilaavenue.activity.GetStartedActivity
import com.example.manilaavenue.adapter.BestPlaceAdapter
import com.example.manilaavenue.adapter.ImagesAdapter
import com.example.manilaavenue.databinding.ActivityAccountBinding
import com.example.manilaavenue.databinding.ActivityDashboardBinding
import com.example.manilaavenue.model.Post
import com.example.manilaavenue.viewmodel.MainViewModel
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.util.*
import androidx.lifecycle.Observer


class AccountActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private lateinit var userRef: DatabaseReference
    private var userListener: ValueEventListener? = null

    private lateinit var storage: FirebaseStorage
    private val viewModel: MainViewModel by viewModels()
    private lateinit var binding: ActivityAccountBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityAccountBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference
        storage = FirebaseStorage.getInstance()

        // Fetch user data and display
        val user = auth.currentUser
        if (user != null) {
            userRef = database.child("users").child(user.uid)
            setupUserListener()
        }

        // Set onClickListener for Logout button
        binding.backBtn.setOnClickListener {
            finish()
        }

        // Set onClickListener for Edit button
        binding.editBtn.setOnClickListener {
            // Navigate to AccountSettingsActivity to edit profile
            val intent = Intent(this, AccountSettingsActivity::class.java)
            startActivity(intent)
        }

        // Set onClickListener for Add Post button
        binding.addPostBtn.setOnClickListener {
            val intent = Intent(this, AddPostActivity::class.java)
            startActivity(intent)
        }

        binding.logoutBtn.setOnClickListener{
            showLogoutConfirmationDialog()
        }


        initPostOfUsers()
    }

    private fun showLogoutConfirmationDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Confirm Logout")
        builder.setMessage("Are you sure you want to log out?")
        builder.setPositiveButton("Yes") { dialog, which ->
            logoutUser()
        }
        builder.setNegativeButton("No") { dialog, which ->
            dialog.dismiss()
        }
        builder.show()
    }

    private fun initPostOfUsers() {

        val backToNormal = binding.viewPosts.layoutParams as ViewGroup.MarginLayoutParams
        backToNormal.setMargins(0, 0, 0,0)

        binding.progressBarPosts.visibility = View.VISIBLE
        viewModel.posts.observe(this, Observer { posts ->
            if (posts.isNullOrEmpty()) {
                binding.viewPosts.visibility = View.VISIBLE
                (binding.viewPosts.layoutParams as ViewGroup.MarginLayoutParams).setMargins(70)
            } else {
                binding.viewPosts.layoutParams = backToNormal
                binding.viewPosts.setBackgroundResource(R.color.white)
                (binding.viewPosts.layoutParams as ViewGroup.MarginLayoutParams).setMargins(0)
                binding.viewPosts.visibility = View.VISIBLE
                binding.viewPosts.layoutManager = GridLayoutManager(this@AccountActivity, 2)
                binding.viewPosts.adapter = ImagesAdapter(posts)
            }
            binding.progressBarPosts.visibility = View.GONE
        })

        viewModel.loadPostItems()
    }


    private fun setupUserListener() {
        userListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val username = snapshot.child("username").getValue(String::class.java) ?: "N/A"
                    val fullName = snapshot.child("fullName").getValue(String::class.java) ?: "N/A"
                    val email = snapshot.child("email").getValue(String::class.java) ?: "N/A"
                    val bio = snapshot.child("bio").getValue(String::class.java) ?: "N/A"
                    val imageUrl = snapshot.child("image").getValue(String::class.java) ?: ""
                    val coverImageUrl = snapshot.child("cover").getValue(String::class.java) ?: ""


                    binding.usernameLabel.text = "@$username"
                    binding.fullNameLabel.text = fullName
                    binding.emailLabel.text = email
                    binding.bioLabel.text = bio
                    if (imageUrl.isNotEmpty()) {
                        Glide.with(this@AccountActivity).load(imageUrl).into(binding.profileImageView)

                    }

                    if (coverImageUrl.isNotEmpty()) {
                        Glide.with(this@AccountActivity).load(coverImageUrl).into(binding.coverImage)

                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle the error
            }
        }
        userRef.addValueEventListener(userListener as ValueEventListener)
    }

    override fun onDestroy() {
        super.onDestroy()
        // Remove the listener when the activity is destroyed to avoid memory leaks
        userListener?.let { userRef.removeEventListener(it) }
    }

    private fun logoutUser() {
        auth.signOut()
        // Close the current activity
        finish()
        // Re-launch the activity after a short delay
        val restartIntent = Intent(this, GetStartedActivity::class.java)
        restartIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(restartIntent)
    }

}

