package com.example.manilaavenue.activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.animation.AnimationUtils
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.example.manilaavenue.R
import com.example.manilaavenue.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()

        val splashLogo = findViewById<ImageView>(R.id.splashLogo)
        val loadingBar = findViewById<ImageView>(R.id.loadingBar)
        val jeepney = findViewById<ImageView>(R.id.jeepney)

        // Start the rotation animation on the loading bar
        val rotateAnimation = AnimationUtils.loadAnimation(this, R.anim.rotate_animation)
        loadingBar.startAnimation(rotateAnimation)

        // Show the jeepney after a delay
        Handler().postDelayed({
            loadingBar.visibility = ImageView.GONE
            splashLogo.visibility = ImageView.GONE
            jeepney.visibility = ImageView.VISIBLE
            val animation = AnimationUtils.loadAnimation(this, R.anim.jeepney_animation)
            jeepney.startAnimation(animation)
        }, 15000) // 15 seconds delay

        // Check user authentication status after animation
        Handler().postDelayed({
            checkUserLoginStatus()
        }, 16000) // 16 seconds delay
    }

    private fun checkUserLoginStatus() {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            // User is logged in, navigate to DashboardActivity
            startActivity(Intent(this, DashboardActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK
            })
        } else {
            // User is not logged in, navigate to GetStartedActivity
            startActivity(Intent(this, GetStartedActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK
            })
        }
        finish()
    }
}
