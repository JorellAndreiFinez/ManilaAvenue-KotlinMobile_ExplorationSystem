package com.example.manilaavenue.adapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.manilaavenue.R

class AddImagePostAdapter(
    private val imageUris: MutableList<Uri>,
    private val onDeleteClickListener: (Int) -> Unit
) : RecyclerView.Adapter<AddImagePostAdapter.ImageViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_image, parent, false)
        return ImageViewHolder(view)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        holder.bind(imageUris[position])
        holder.itemView.setOnClickListener {
            onDeleteClickListener(position)
        }
    }

    override fun getItemCount(): Int {
        return imageUris.size
    }

    class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imageView: ImageView = itemView.findViewById(R.id.imageView)

        fun bind(uri: Uri) {
            Glide.with(itemView.context)
                .load(uri)
                .into(imageView)
        }
    }
}
