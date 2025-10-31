package com.example.manilaavenue.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.manilaavenue.databinding.ViewholderBlogimageBinding
import com.example.manilaavenue.databinding.ViewholderPostListBinding

class PostImageListAdapter(
    private val items: List<String>, // List of image URLs
    private val picMain: ImageView // Main image view to update
) : RecyclerView.Adapter<PostImageListAdapter.ViewHolder>() {

    private lateinit var context: Context

    inner class ViewHolder(val binding: ViewholderPostListBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        val binding = ViewholderPostListBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // Load image into ImageView using Glide
        Glide.with(context)
            .load(items[position])
            .into(holder.binding.picPostList)

        // Handle click events to update the main image view (picMain)
        holder.binding.root.setOnClickListener {
            Glide.with(context)
                .load(items[position])
                .into(picMain)
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }
}
