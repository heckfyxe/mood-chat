package com.heckfyxe.moodchat.databinding

import android.graphics.drawable.Drawable
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.heckfyxe.moodchat.R
import com.heckfyxe.moodchat.model.Conversation
import com.heckfyxe.moodchat.model.Group
import com.heckfyxe.moodchat.model.User
import com.heckfyxe.moodchat.util.*


@BindingAdapter("app:userImage")
fun loadUserImage(imageView: ImageView?, user: User?) {
    if (user != null)
        imageView?.loadUser(user)
}

@BindingAdapter("app:groupImage")
fun loadGroupImage(imageView: ImageView?, group: Group?) {
    if (group != null)
        imageView?.loadGroup(group)
}

@BindingAdapter("app:chatImage")
fun loadChatImage(imageView: ImageView?, chatSettings: Conversation.ChatSettings?) {
    if (chatSettings != null)
        imageView?.loadChat(chatSettings)
}

@BindingAdapter("app:userName")
fun userName(textView: TextView?, user: User?) {
    textView?.text = user?.toString() ?: ""
}

@BindingAdapter("app:timeHHMM")
fun timeHHMM(textView: TextView?, time: Long) {
    textView?.text = getHHMM(time)
}

@BindingAdapter("app:onlineStatus")
fun onlineStatus(imageView: ImageView, user: User?) {
    imageView.hide()

    if (user == null)
        return

    if (!user.online && !user.onlineMobile)
        return

    @DrawableRes
    val drawableRes = if (user.onlineMobile)
        R.drawable.ic_online_mobile
    else
        R.drawable.ic_online

    Glide.with(imageView)
            .load(drawableRes)
            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable>?,
                        isFirstResource: Boolean): Boolean = false

                override fun onResourceReady(
                        resource: Drawable?,
                        model: Any?,
                        target: Target<Drawable>?,
                        dataSource: DataSource?,
                        isFirstResource: Boolean): Boolean {
                    imageView.show()
                    return false
                }
            })
            .into(imageView)
}