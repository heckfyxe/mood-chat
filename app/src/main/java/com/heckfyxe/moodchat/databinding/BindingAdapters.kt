package com.heckfyxe.moodchat.databinding

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.squareup.picasso.Picasso

class BindingAdapters {
    companion object {
        @JvmStatic
        @BindingAdapter("image")
        fun loadImage(imageView: ImageView, imageUrl: String?) {
            Picasso.get()
                    .load(imageUrl)
                    .fit()
                    .centerCrop()
                    .into(imageView)
        }
    }
}