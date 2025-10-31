package com.example.manilaavenue.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.request.RequestOptions
import com.example.manilaavenue.activity.DetailActivity
import com.example.manilaavenue.databinding.ViewholderBestPlaceBinding
import com.example.manilaavenue.model.ItemsModel

class BestPlaceAdapter(private val items: MutableList<ItemsModel>) :
    RecyclerView.Adapter<BestPlaceAdapter.ViewHolder>() {

    private lateinit var context: Context

    inner class ViewHolder(val binding: ViewholderBestPlaceBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        val binding = ViewholderBestPlaceBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        with(holder.binding) {
            titleTxt.text = item.title
            location.text = item.location
            ratingTxt.text = item.rating.toString()

            val requestOptions = RequestOptions().transform(CenterCrop())
            Glide.with(context)
                .load(item.picUrl[0])
                .apply(requestOptions)
                .into(picBestPlace)

            // Inside onBindViewHolder() method
            Glide.with(context)
                .load(item.picUrl[0]) // Load first image from picUrl list
                .apply(RequestOptions().transform(CenterCrop()))
                .into(holder.binding.picBestPlace)


            root.setOnClickListener {
                val intent = Intent(context, DetailActivity::class.java)
                intent.putExtra("object", item)
                context.startActivity(intent)
            }
        }
    }

    override fun getItemCount(): Int = items.size
}
