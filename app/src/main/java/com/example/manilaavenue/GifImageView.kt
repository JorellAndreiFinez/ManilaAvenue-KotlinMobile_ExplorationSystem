package com.example.manilaavenue

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView
import com.bumptech.glide.Glide

class GifImageView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : AppCompatImageView(context, attrs, defStyleAttr) {

    fun setGifResource(resourceId: Int) {
        Glide.with(context).asGif().load(resourceId).into(this)
    }
}
