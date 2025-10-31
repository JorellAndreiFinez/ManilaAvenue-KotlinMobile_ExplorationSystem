package com.example.manilaavenue.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.manilaavenue.databinding.ViewholderPicListBinding
import com.example.manilaavenue.model.ItemsModel

class PicListAdapter(
    private val items: List<String>, // List of picUrls
    private val picMain: ImageView // Main image view to update
) : RecyclerView.Adapter<PicListAdapter.ViewHolder>() {

    private lateinit var context: Context

    inner class ViewHolder(val binding: ViewholderPicListBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        val binding = ViewholderPicListBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // Load image into ImageView using Glide
        Glide.with(context)
            .load(items[position])
            .into(holder.binding.picList)

        // Handle click events or any specific logic here if needed
        holder.binding.root.setOnClickListener {
            // Update main image view (picMain) with clicked image
            Glide.with(context)
                .load(items[position])
                .into(picMain)
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }
}

