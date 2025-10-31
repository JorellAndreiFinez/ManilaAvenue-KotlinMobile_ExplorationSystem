package com.example.manilaavenue.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.manilaavenue.GifImageView
import com.example.manilaavenue.R

class GifAdapter(
    private val context: Context,
    private val gifUrls: List<String>
) : RecyclerView.Adapter<GifAdapter.GifViewHolder>() {

    class GifViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val gifImageView: GifImageView = view.findViewById(R.id.gifImageView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GifViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_gif, parent, false)
        return GifViewHolder(view)
    }

    override fun onBindViewHolder(holder: GifViewHolder, position: Int) {
        val gifUrl = gifUrls[position]
        Glide.with(context).asGif().load(gifUrl).into(holder.gifImageView)
    }

    override fun getItemCount() = gifUrls.size
}
