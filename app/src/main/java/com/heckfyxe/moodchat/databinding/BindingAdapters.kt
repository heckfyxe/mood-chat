package com.heckfyxe.moodchat.databinding

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide

class BindingAdapters {
    companion object {
        @JvmStatic
        @BindingAdapter("image")
        fun loadImage(imageView: ImageView, imageUrl: String?) {
            Glide.with(imageView)
                    .load(imageUrl)
                    .into(imageView)
        }
    }
}