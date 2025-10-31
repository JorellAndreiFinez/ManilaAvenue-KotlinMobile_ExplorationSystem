package com.example.manilaavenue.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.manilaavenue.databinding.ViewholderCategoryBinding
import com.example.manilaavenue.model.CategoryModel
import android.view.View
import android.widget.ImageView
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.load.resource.bitmap.CenterInside
import com.bumptech.glide.request.RequestOptions
import com.example.manilaavenue.R
import com.example.manilaavenue.model.SliderModel

class CategoryAdapter(val items: MutableList<CategoryModel>):
RecyclerView.Adapter<CategoryAdapter.Viewholder>()
{
    private lateinit var context: Context

    inner class Viewholder(val binding: ViewholderCategoryBinding) :
            RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Viewholder {
        context = parent.context
        val binding = ViewholderCategoryBinding.inflate(LayoutInflater.from(context), parent, false)
        return Viewholder(binding)
    }

    override fun onBindViewHolder(holder: Viewholder, position: Int) {
        val item = items[position]
        holder.binding.titleCat.text = item.title

        Glide.with(holder.itemView.context)
            .load(item.picUrl)
            .into(holder.binding.picCat)
    }
    override fun getItemCount(): Int =  items.size

}
