package com.example.manilaavenue.activity

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.InputType
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.example.manilaavenue.R
import com.example.manilaavenue.databinding.ActivityAccountSettingsBinding
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage

class AccountSettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAccountSettingsBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var storage: FirebaseStorage
    private var selectedImageUri: Uri? = null
    private var selectedCoverImageUri: Uri? = null

    private val selectImageLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                selectedImageUri = result.data?.data
                binding.profileImageViewProfileFrag.setImageURI(selectedImageUri)
            }
        }

    private val selectImageLauncher2 =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                selectedCoverImageUri = result.data?.data
                binding.coverImageViewProfileFrag.setImageURI(selectedCoverImageUri)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAccountSettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        storage = FirebaseStorage.getInstance()

        binding.changeImageTextBtn.setOnClickListener {
            showImagePicker(selectImageLauncher)
        }

        binding.changeCoverImageBtn.setOnClickListener {
            showImagePicker(selectImageLauncher2)
        }

        binding.saveInforProfileBtn.setOnClickListener {
            saveUserInformation()
            Toast.makeText(this, "You successfully updated your profile!", Toast.LENGTH_LONG).show()
            finish()
        }

        binding.closeProfileBtn.setOnClickListener {
            // Create an AlertDialog to confirm with the user
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Discard Changes")
            builder.setMessage("Are you sure you want to discard the changes and return?")

            builder.setPositiveButton("Yes") { dialog, which ->
                finish()
            }

            builder.setNegativeButton("No") { dialog, which ->
                dialog.dismiss()
            }

            builder.show()
        }

        binding.deleteAccountBtn.setOnClickListener {
            showPasswordDialog()
        }

        loadUserInformation()
    }

    private fun showImagePicker(launcher: ActivityResultLauncher<Intent>) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Choose Image Source")
        val options = arrayOf("Gallery", "Storage")

        builder.setItems(options) { dialog, which ->
            when (which) {
                0 -> launchGalleryPicker(launcher)
                1 -> launchDownloadsPicker(launcher)
            }
        }

        builder.show()
    }

    private fun launchGalleryPicker(launcher: ActivityResultLauncher<Intent>) {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        launcher.launch(intent)
    }

    private fun launchDownloadsPicker(launcher: ActivityResultLauncher<Intent>) {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type = "image/*"
        launcher.launch(intent)
    }

    private fun loadUserInformation() {
        val currentUser = auth.currentUser ?: return

        val userRef = database.getReference("users").child(currentUser.uid)
        userRef.get().addOnSuccessListener { snapshot ->
            if (snapshot.exists()) {
                binding.fullNameProfileFrag.setText(snapshot.child("fullName").getValue(String::class.java))
                binding.usernameProfileFrag.setText(snapshot.child("username").getValue(String::class.java))
                binding.emailProfileFrag.setText(snapshot.child("email").getValue(String::class.java))

                binding.bioProfileFrag.setText(snapshot.child("bio").getValue(String::class.java))

                val imageUrl = snapshot.child("image").getValue(String::class.java)
                if (!imageUrl.isNullOrEmpty()) {
                    Glide.with(this).load(imageUrl).into(binding.profileImageViewProfileFrag)
                }

                val coverImage = snapshot.child("cover").getValue(String::class.java)
                if (!coverImage.isNullOrEmpty()) {
                    Glide.with(this).load(coverImage).into(binding.coverImageViewProfileFrag)
                }
            }
        }
    }

    private fun saveUserInformation() {
        val currentUser = auth.currentUser ?: return
        val fullname = binding.fullNameProfileFrag.text.toString()
        val username = binding.usernameProfileFrag.text.toString()
        val email = binding.emailProfileFrag.text.toString()
        val bio = binding.bioProfileFrag.text.toString()

        if (fullname.isEmpty() || username.isEmpty() || bio.isEmpty()) {
            Toast.makeText(this, "All fields must be filled out", Toast.LENGTH_SHORT).show()
            return
        }

        val userMap = mutableMapOf<String, Any>()
        userMap["fullName"] = fullname
        userMap["username"] = username
        userMap["email"] = email
        userMap["bio"] = bio

        if (selectedImageUri != null) {
            val imagePath = "profile_images/${currentUser.uid}.jpg"
            val storageRef = storage.reference.child(imagePath)

            storageRef.putFile(selectedImageUri!!)
                .addOnSuccessListener {
                    storageRef.downloadUrl.addOnSuccessListener { uri ->
                        userMap["image"] = uri.toString()
                        updateUserInDatabase(currentUser.uid, userMap)
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Failed to upload image", Toast.LENGTH_SHORT).show()
                }
        } else {
            updateUserInDatabase(currentUser.uid, userMap)
        }

        if (selectedCoverImageUri != null) {
            val imagePath = "cover_images/${currentUser.uid}.jpg"
            val storageRef = storage.reference.child(imagePath)

            storageRef.putFile(selectedCoverImageUri!!)
                .addOnSuccessListener {
                    storageRef.downloadUrl.addOnSuccessListener { uri ->
                        userMap["cover"] = uri.toString()
                        updateUserInDatabase(currentUser.uid, userMap)
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Failed to upload image", Toast.LENGTH_SHORT).show()
                }
        } else {
            updateUserInDatabase(currentUser.uid, userMap)
        }
    }

    private fun updateUserInDatabase(uid: String, userMap: Map<String, Any>) {
        val userRef = database.getReference("users").child(uid)
        userRef.updateChildren(userMap)
            .addOnSuccessListener {
                Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to update profile", Toast.LENGTH_SHORT).show()
            }
    }

    private fun showPasswordDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Delete Account")
        builder.setMessage("Please enter your password to confirm deletion:")

        val input = EditText(this)
        input.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        builder.setView(input)

        builder.setPositiveButton("Confirm") { dialog, _ ->
            val password = input.text.toString()
            if (password.isNotEmpty()) {
                reauthenticateUser(password)
            } else {
                Toast.makeText(this, "Password cannot be empty", Toast.LENGTH_SHORT).show()
            }
            dialog.dismiss()
        }

        builder.setNegativeButton("Cancel") { dialog, _ ->
            dialog.dismiss()
        }

        builder.show()
    }

    private fun reauthenticateUser(password: String) {
        val user = FirebaseAuth.getInstance().currentUser
        val credential = EmailAuthProvider.getCredential(user?.email ?: "", password)

        user?.reauthenticate(credential)
            ?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    deleteUserAccount()
                } else {
                    Toast.makeText(this, "Authentication failed. Please try again.", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun deleteUserAccount() {
        val user = FirebaseAuth.getInstance().currentUser

        user?.delete()
            ?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Account deleted successfully", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, GetStartedActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this, "Failed to delete account. Please try again later.", Toast.LENGTH_SHORT).show()
                }
            }
    }
}
