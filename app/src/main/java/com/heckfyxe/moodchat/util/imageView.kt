package com.heckfyxe.moodchat.util

import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.heckfyxe.moodchat.R
import com.heckfyxe.moodchat.model.User

fun ImageView.loadUser(user: User) {
    val thumbnail = Glide.with(this)
        .load(user.photo50)

    Glide.with(this)
        .setDefaultRequestOptions(
            RequestOptions
                .circleCropTransform()
                .placeholder(R.drawable.ic_user)
        )
        .load(user.photo100)
        .thumbnail(thumbnail)
        .into(this)
}