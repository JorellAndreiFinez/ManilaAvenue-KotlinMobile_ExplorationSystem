package com.example.manilaavenue.activity

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.manilaavenue.R
import com.example.manilaavenue.databinding.ActivitySignupBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class SignupActivity : AppCompatActivity(), View.OnClickListener, View.OnFocusChangeListener {

    private lateinit var _binding: ActivitySignupBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        _binding = ActivitySignupBinding.inflate(LayoutInflater.from(this))
        setContentView(_binding.root)

        _binding.gifBackground.setGifResource(R.drawable.signup)

        _binding.returnBack.setOnClickListener {
            finish()
        }

        _binding.fullnameEditTxts.onFocusChangeListener = this
        _binding.usernameEditTxts.onFocusChangeListener = this
        _binding.emailEditTxts.onFocusChangeListener = this
        _binding.passwordEditTxts.onFocusChangeListener = this
        _binding.confirmPassEditTxts.onFocusChangeListener = this

        _binding.signUpBtn.setOnClickListener {
            signUp()
        }

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
    }

    private fun signUp() {
        val fullName = _binding.fullnameEditTxts.text.toString()
        val username = _binding.usernameEditTxts.text.toString()
        val email = _binding.emailEditTxts.text.toString()
        val password = _binding.passwordEditTxts.text.toString()
        val bio = "Put Something on This Bio"
        val image = ""
        val cover = ""
        val confirmPassword = _binding.confirmPassEditTxts.text.toString()

        if (validateFullName() && validateUsername() && validateEmail() && validatePasswordAndConfirmPass()) {
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, save user info to Realtime Database
                        val user = auth.currentUser
                        val userReference = user?.uid?.let { database.reference.child("users").child(it) }

                        val userData = hashMapOf(
                            "fullName" to fullName.toLowerCase(),
                            "username" to username.toLowerCase(),
                            "email" to email,
                            "bio" to bio,
                            "image" to "https://firebasestorage.googleapis.com/v0/b/manilaavenue-87263.appspot.com/o/default_profile_user.png?alt=media&token=05a7820e-3109-4a63-b0c3-864bd9584745",
                            "cover" to ""

                        )

                        userReference?.setValue(userData)
                            ?.addOnSuccessListener {
                                Toast.makeText(baseContext, "Signup successful.", Toast.LENGTH_SHORT).show()
                                // Start DashboardActivity and clear the back stack
                                startActivity(Intent(this, DashboardActivity::class.java).apply {
                                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                })
                                finish()
                            }
                            ?.addOnFailureListener { e ->
                                Toast.makeText(baseContext, "Failed to save user data: $e", Toast.LENGTH_SHORT).show()
                            }
                    } else {
                        // If sign in fails, display a message to the user.
                        Toast.makeText(baseContext, "Signup failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }

    private fun validateUsername(): Boolean {
        val value = _binding.usernameEditTxts.text.toString().trim()
        return if (value.isEmpty()) {
            _binding.usernameLayout.error = "Username is required"
            false
        } else {
            _binding.usernameLayout.error = null
            true
        }
    }

    private fun validateFullName(): Boolean {
        val value = _binding.fullnameEditTxts.text.toString().trim()
        return if (value.isEmpty()) {
            _binding.fullnameLayout.error = "Full Name is required"
            false
        } else {
            _binding.fullnameLayout.error = null
            true
        }
    }

    private fun validateEmail(): Boolean {
        val value = _binding.emailEditTxts.text.toString().trim()
        return if (value.isEmpty()) {
            _binding.emailLayout.error = "Email Address is required"
            false
        } else if (!Patterns.EMAIL_ADDRESS.matcher(value).matches()) {
            _binding.emailLayout.error = "Invalid Email Address"
            false
        } else {
            _binding.emailLayout.error = null
            true
        }
    }

    private fun validatePassword(): Boolean {
        val value = _binding.passwordEditTxts.text.toString().trim()
        return if (value.isEmpty()) {
            _binding.passLayout.error = "Password is required"
            false
        } else if (value.length < 6) {
            _binding.passLayout.error = "Password must be at least 6 characters long"
            false
        } else {
            _binding.passLayout.error = null
            true
        }
    }

    private fun validateConfirmPassword(): Boolean {
        val value = _binding.confirmPassEditTxts.text.toString().trim()
        return if (value.isEmpty()) {
            _binding.confirmPassLayout.error = "Confirm Password is required"
            false
        } else {
            _binding.confirmPassLayout.error = null
            true
        }
    }

    private fun validatePasswordAndConfirmPass(): Boolean {
        val passwordValid = validatePassword()
        val confirmPasswordValid = validateConfirmPassword()

        if (passwordValid && confirmPasswordValid) {
            val password = _binding.passwordEditTxts.text.toString()
            val confirmPassword = _binding.confirmPassEditTxts.text.toString()

            if (password != confirmPassword) {
                _binding.confirmPassLayout.error = "Passwords do not match"
                return false
            }
        }

        return passwordValid && confirmPasswordValid
    }

    override fun onClick(v: View?) {
        // Handle click events if needed
    }

    override fun onFocusChange(view: View?, hasFocus: Boolean) {
        if (view != null) {
            when (view.id) {
                R.id.fullnameEditTxts -> {
                    if (hasFocus) {
                        if (_binding.fullnameLayout.isErrorEnabled) {
                            _binding.fullnameLayout.isErrorEnabled = false
                        }
                    } else {
                        validateFullName()
                    }
                }

                R.id.usernameEditTxts -> {
                    if (hasFocus) {
                        if (_binding.usernameLayout.isErrorEnabled) {
                            _binding.usernameLayout.isErrorEnabled = false
                        }
                    } else {
                        validateUsername()
                    }
                }

                R.id.emailEditTxts -> {
                    if (hasFocus) {
                        if (_binding.emailLayout.isErrorEnabled) {
                            _binding.emailLayout.isErrorEnabled = false
                        }
                    } else {
                        validateEmail()
                    }
                }

                R.id.passwordEditTxts -> {
                    if (hasFocus) {
                        if (_binding.passLayout.isErrorEnabled) {
                            _binding.passLayout.isErrorEnabled = false
                        }
                    } else {
                        if (validatePassword() && _binding.confirmPassEditTxts.text!!.isNotEmpty()
                            && validateConfirmPassword() && validatePasswordAndConfirmPass()) {
                            if (_binding.confirmPassLayout.isErrorEnabled) {
                                _binding.confirmPassLayout.isErrorEnabled = false
                            }
                            _binding.passLayout.apply {
                                setStartIconDrawable(R.drawable.checkcircle)
                                setStartIconTintList(ColorStateList.valueOf(Color.GREEN))
                            }
                        }
                    }
                }

                R.id.confirmPassEditTxts -> {
                    if (hasFocus) {
                        if (_binding.confirmPassLayout.isErrorEnabled) {
                            _binding.confirmPassLayout.isErrorEnabled = false
                        }
                    } else {
                        if (validateConfirmPassword() && validatePassword() && validatePasswordAndConfirmPass()) {
                            if (_binding.passLayout.isErrorEnabled) {
                                _binding.passLayout.isErrorEnabled = false
                            }
                            _binding.confirmPassLayout.apply {
                                setStartIconDrawable(R.drawable.checkcircle)
                                setStartIconTintList(ColorStateList.valueOf(Color.GREEN))
                            }
                        }
                    }
                }
            }
        }
    }
}
