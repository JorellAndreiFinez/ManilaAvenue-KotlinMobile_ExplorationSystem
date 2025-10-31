package com.example.manilaavenue.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.manilaavenue.R
import com.example.manilaavenue.adapter.AddImagePostAdapter
import com.example.manilaavenue.model.Post
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import java.util.*

class AddPostActivity : AppCompatActivity() {

    private lateinit var titleEditText: EditText
    private lateinit var captionEditText: EditText
    private lateinit var addImageBtn: Button
    private lateinit var postBtn: Button
    private lateinit var recyclerView: RecyclerView

    private lateinit var selectedImageUris: MutableList<Uri>
    private lateinit var addImagePostAdapter: AddImagePostAdapter

    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var storage: FirebaseStorage

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_post)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        storage = FirebaseStorage.getInstance()

        titleEditText = findViewById(R.id.titleEditText)
        captionEditText = findViewById(R.id.captionEditText)
        addImageBtn = findViewById(R.id.addImageBtn)
        postBtn = findViewById(R.id.postBtn)
        recyclerView = findViewById(R.id.recyclerView)

        selectedImageUris = mutableListOf()
        selectedImageUris.add(Uri.parse("android.resource://${packageName}/${R.drawable.ic_testicon}"))

        // Initialize RecyclerView
        addImagePostAdapter = AddImagePostAdapter(selectedImageUris) { position ->
            removeImage(position)
        }
        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        recyclerView.adapter = addImagePostAdapter

        addImageBtn.setOnClickListener {
            openImageChooser()
        }

        postBtn.setOnClickListener {
            uploadPost()
        }

        findViewById<ImageView>(R.id.backBtn).setOnClickListener{
            finish()
        }

    }

    private fun openImageChooser() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true) // Allow multiple image selection
        resultLauncher.launch(intent)
    }

    private var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            val data: Intent? = result.data
            if (data != null) {
                if (data.clipData != null) { // Multiple images selected
                    val count = data.clipData!!.itemCount
                    for (i in 0 until count) {
                        val uri = data.clipData!!.getItemAt(i).uri
                        selectedImageUris.add(uri)
                    }
                } else if (data.data != null) { // Single image selected
                    val uri = data.data!!
                    selectedImageUris.add(uri)
                }
                addImagePostAdapter.notifyDataSetChanged() // Update RecyclerView
            }
        }
    }

    private fun removeImage(position: Int) {
        selectedImageUris.removeAt(position)
        addImagePostAdapter.notifyDataSetChanged() // Update RecyclerView
    }

    private fun uploadPost() {
        val title = titleEditText.text.toString().trim()
        val caption = captionEditText.text.toString().trim()

        if (title.isEmpty() || caption.isEmpty()) {
            Toast.makeText(this, "Title and Caption cannot be empty", Toast.LENGTH_SHORT).show()
            return
        }

        val userId = auth.currentUser?.uid ?: return
        val postsRef = database.reference.child("users").child(userId).child("Posts").push()

        val imageUrls = mutableListOf<String>()
        val uploadTasks = mutableListOf<Uri>()

        for (uri in selectedImageUris) {
            val imageFileName = UUID.randomUUID().toString()
            val imagesRef = storage.reference.child("images/$userId/posts/$imageFileName")

            val uploadTask = imagesRef.putFile(uri)
            uploadTasks.add(uri)

            uploadTask.continueWithTask { task ->
                if (!task.isSuccessful) {
                    task.exception?.let {
                        throw it
                    }
                }
                imagesRef.downloadUrl
            }.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val downloadUri = task.result
                    downloadUri?.let {
                        imageUrls.add(it.toString())
                        if (uploadTasks.size == selectedImageUris.size) {
                            val post = Post(title, caption, imageUrls, System.currentTimeMillis())
                            postsRef.setValue(post)
                                .addOnSuccessListener {
                                    Toast.makeText(this, "Post added successfully", Toast.LENGTH_SHORT).show()
                                    finish()
                                }
                                .addOnFailureListener { e ->
                                    Toast.makeText(this, "Failed to add post: ${e.message}", Toast.LENGTH_SHORT).show()
                                }
                        }
                    }
                } else {
                    Toast.makeText(this, "Failed to upload image", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
