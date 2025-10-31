package com.example.manilaavenue.activity

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.manilaavenue.R
import com.example.manilaavenue.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class LoginActivity : AppCompatActivity() {

    private lateinit var _binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private var lastResetTime: Long = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        _binding = ActivityLoginBinding.inflate(LayoutInflater.from(this))
        setContentView(_binding.root)

        _binding.gifBackground.setGifResource(R.drawable.login)

        _binding.returnBack.setOnClickListener {
            finish()
        }

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()

        _binding.loginBtn.setOnClickListener {
            val email = _binding.emailEditTxts.text.toString()
            val password = _binding.passwordEditTxts.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                loginUser(email, password)
            } else {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            }
        }

        _binding.forgotpass.setOnClickListener {
            showForgotPasswordDialog()
        }

        // Check if user is already logged in
        val currentUser = auth.currentUser
        if (currentUser != null) {
            // User is already logged in, navigate to DashboardActivity
            startActivity(Intent(this, DashboardActivity::class.java))
            finish() // Finish LoginActivity to prevent going back to it
        }


    }

    private fun showForgotPasswordDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_forgot_password, null)
        val dialogBuilder = AlertDialog.Builder(this)
            .setView(dialogView)
            .create()

        dialogBuilder.show()

        val closeButton = dialogView.findViewById<ImageButton>(R.id.closeBtn)
        closeButton.setOnClickListener {
            dialogBuilder.dismiss()
        }

        val emailEditText = dialogView.findViewById<EditText>(R.id.forgot_password_email)
        val sendButton = dialogView.findViewById<Button>(R.id.send_reset_email_button)

        sendButton.setOnClickListener {
            val email = emailEditText.text.toString().trim()
            if (email.isNotEmpty()) {
                if (System.currentTimeMillis() - lastResetTime >= 30000) {
                    sendPasswordResetEmail(email)
                    updateSendButtonState(sendButton)
                } else {
                    Toast.makeText(this, "Please wait 30 seconds before trying again", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Please enter your email", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateSendButtonState(button: Button) {
        button.isEnabled = false
        button.text = "Wait 30 seconds"
        button.setBackgroundColor(ContextCompat.getColor(this, android.R.color.darker_gray))

        object : CountDownTimer(30000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val secondsRemaining = millisUntilFinished / 1000
                button.text = "Wait $secondsRemaining seconds"
            }

            override fun onFinish() {
                button.isEnabled = true
                button.text = "Send Reset Email"
                button.setBackgroundColor(ContextCompat.getColor(this@LoginActivity, R.color.Khaki))
            }
        }.start()
    }

    private fun sendPasswordResetEmail(email: String) {
        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    lastResetTime = System.currentTimeMillis()
                    showPasswordResetDialog()
                } else {
                    Toast.makeText(baseContext, "Failed to send reset email: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun showPasswordResetDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Password Reset")
            .setMessage("A password reset email has been sent to your email address.")
            .setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun loginUser(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    val userReference = user?.uid?.let { database.reference.child("users").child(it) }

                    Toast.makeText(baseContext, "Login successful.", Toast.LENGTH_SHORT).show()

                    // Navigate to DashboardActivity and finish LoginActivity
                    startActivity(Intent(this, DashboardActivity::class.java))
                    finish() // Finish LoginActivity
                } else {
                    Toast.makeText(baseContext, "Login failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }

}
