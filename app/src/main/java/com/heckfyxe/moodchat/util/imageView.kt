package com.heckfyxe.moodchat.util

import android.view.View.VISIBLE
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.heckfyxe.moodchat.R
import com.heckfyxe.moodchat.model.Conversation
import com.heckfyxe.moodchat.model.Group
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

    visibility = VISIBLE
}

fun ImageView.loadGroup(group: Group) {
    val thumbnail = Glide.with(this)
        .load(group.photo50)

    Glide.with(this)
        .setDefaultRequestOptions(
            RequestOptions
                .circleCropTransform()
                .placeholder(R.drawable.ic_group)
        )
        .load(group.photo100)
        .thumbnail(thumbnail)
        .into(this)

    visibility = VISIBLE
}

fun ImageView.loadChat(chat: Conversation.ChatSettings?) {
    val thumbnail = Glide.with(this)
        .load(chat?.photo?.photo50)

    Glide.with(this)
        .setDefaultRequestOptions(
            RequestOptions
                .circleCropTransform()
                .placeholder(R.drawable.ic_group)
        )
        .load(chat?.photo?.photo100)
        .thumbnail(thumbnail)
        .into(this)

    visibility = VISIBLE
}