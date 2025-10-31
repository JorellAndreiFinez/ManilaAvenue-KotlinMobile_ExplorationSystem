package com.example.manilaavenue.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.manilaavenue.R
import com.example.manilaavenue.GifImageView

class GetStartedActivity : AppCompatActivity() {

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_get_started)

        val gifBackground = findViewById<GifImageView>(R.id.gifBackground)
        gifBackground.setGifResource(R.drawable.welcome)

        val getstarted = findViewById<Button>(R.id.getsrated)
        getstarted.setOnClickListener {
            val intent = Intent(this@GetStartedActivity, SignupActivity::class.java)
            startActivity(intent)
        }

        val logininstead = findViewById<TextView>(R.id.loginTxt)
        logininstead.setOnClickListener {
            val intent = Intent(this@GetStartedActivity, LoginActivity::class.java)
            startActivity(intent)
        }
    }
}
