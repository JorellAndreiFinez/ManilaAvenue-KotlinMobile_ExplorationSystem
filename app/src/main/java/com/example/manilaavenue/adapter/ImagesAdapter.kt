package com.example.manilaavenue.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.request.RequestOptions
import com.example.manilaavenue.activity.PostDetailsActivity
import com.example.manilaavenue.databinding.ViewholderBlogimageBinding
import com.example.manilaavenue.model.Post

class ImagesAdapter(private val items: MutableList<Post>) :
    RecyclerView.Adapter<ImagesAdapter.ViewHolder>() {

    private lateinit var context: Context

    inner class ViewHolder(val binding: ViewholderBlogimageBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        val binding = ViewholderBlogimageBinding.inflate(
            LayoutInflater.from(context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        with(holder.binding) {
            titleTxt.text = item.title
            timeTxt.text = item.timestamp.toString()

            // Check if imageUrls is not empty before loading images
            if (item.imageUrls.isNotEmpty()) {
                Glide.with(context)
                    .load(item.imageUrls[0]) // Load first image from imageUrls list
                    .apply(RequestOptions().transform(CenterCrop()))
                    .into(picBestPlace)
            }

            root.setOnClickListener {
                val intent = Intent(context, PostDetailsActivity::class.java)
                intent.putExtra("object", item)
                context.startActivity(intent)
            }
        }
    }

    override fun getItemCount(): Int = items.size
}
