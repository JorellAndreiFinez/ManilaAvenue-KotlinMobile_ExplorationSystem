package com.example.manilaavenue.activity

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.manilaavenue.R
import com.example.manilaavenue.adapter.AddImagePostAdapter
import com.example.manilaavenue.model.Post
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class EditPostActivity : AppCompatActivity() {

    private lateinit var editTitleEditText: EditText
    private lateinit var editCaptionEditText: EditText
    private lateinit var editAddImageBtn: Button
    private lateinit var savePostBtn: Button
    private lateinit var editRecyclerView: RecyclerView

    private lateinit var selectedImageUris: MutableList<Uri>
    private lateinit var addImagePostAdapter: AddImagePostAdapter

    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase

    private var hasChanges: Boolean = false

    private var postId: String = ""

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_post)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()

        editTitleEditText = findViewById(R.id.editTitleEditText)
        editCaptionEditText = findViewById(R.id.editCaptionEditText)
        editAddImageBtn = findViewById(R.id.editAddImageBtn)
        savePostBtn = findViewById(R.id.savePostBtn)
        editRecyclerView = findViewById(R.id.editRecyclerView)

        selectedImageUris = mutableListOf()
        addImagePostAdapter = AddImagePostAdapter(selectedImageUris) { position ->
            removeImage(position)
        }
        editRecyclerView.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        editRecyclerView.adapter = addImagePostAdapter

        postId = intent.getStringExtra("postId") ?: ""

        loadPostData(postId)

        editAddImageBtn.setOnClickListener {
            openImageChooser()
        }

        savePostBtn.setOnClickListener {
            updatePost(postId)
        }

        findViewById<ImageView>(R.id.backBtn).setOnClickListener {
            onBackPressed() // Trigger onBackPressed behavior for backBtn
        }
    }

    override fun onBackPressed() {
        if (hasChanges) {
            showDiscardChangesDialog()
        } else {
            super.onBackPressed()
        }
    }

    private fun showDiscardChangesDialog() {
        AlertDialog.Builder(this)
            .setTitle("Discard Changes?")
            .setMessage("Are you sure you want to discard changes and go back?")
            .setPositiveButton("Discard") { dialogInterface: DialogInterface, i: Int ->
                dialogInterface.dismiss()
                super.onBackPressed()
            }
            .setNegativeButton("Cancel") { dialogInterface: DialogInterface, i: Int ->
                dialogInterface.dismiss()
            }
            .show()
    }

    private fun loadPostData(postId: String) {
        val userId = auth.currentUser?.uid ?: return
        val postRef =
            database.reference.child("users").child(userId).child("Posts").child(postId)

        postRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val post = snapshot.getValue(Post::class.java)
                if (post != null) {
                    editTitleEditText.setText(post.title)
                    editCaptionEditText.setText(post.description)
                    selectedImageUris.clear()
                    selectedImageUris.addAll(post.imageUrls.map { Uri.parse(it) })
                    addImagePostAdapter.notifyDataSetChanged()
                } else {
                    Toast.makeText(this@EditPostActivity, "Post not found", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@EditPostActivity, "Failed to load post data", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun openImageChooser() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        resultLauncher.launch(intent)
    }

    private var resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val data: Intent? = result.data
                if (data != null) {
                    if (data.clipData != null) {
                        val count = data.clipData!!.itemCount
                        for (i in 0 until count) {
                            val uri = data.clipData!!.getItemAt(i).uri
                            selectedImageUris.add(uri)
                        }
                    } else if (data.data != null) {
                        val uri = data.data!!
                        selectedImageUris.add(uri)
                    }
                    addImagePostAdapter.notifyDataSetChanged()
                }
            }
        }

    private fun removeImage(position: Int) {
        selectedImageUris.removeAt(position)
        addImagePostAdapter.notifyDataSetChanged()
    }

    private fun updatePost(postId: String) {
        val title = editTitleEditText.text.toString().trim()
        val caption = editCaptionEditText.text.toString().trim()

        if (title.isNotEmpty() && caption.isNotEmpty()) {
            val userId = auth.currentUser?.uid ?: return
            val postRef =
                database.reference.child("users").child(userId).child("Posts").child(postId)

            val updatedPost = mapOf(
                "title" to title,
                "description" to caption,
                "imageUrls" to selectedImageUris.map { it.toString() }
                // Update other fields as needed
            )

            postRef.updateChildren(updatedPost).addOnSuccessListener {
                Toast.makeText(this, "Post updated successfully", Toast.LENGTH_SHORT).show()
                finish()
            }.addOnFailureListener {
                Toast.makeText(this, "Failed to update post", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "Title and caption cannot be empty", Toast.LENGTH_SHORT).show()
        }
    }
}
