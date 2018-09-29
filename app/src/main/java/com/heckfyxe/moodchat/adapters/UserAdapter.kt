package com.heckfyxe.moodchat.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.heckfyxe.moodchat.R
import com.squareup.picasso.Picasso
import com.vk.sdk.api.model.VKApiUserFull
import kotlinx.android.synthetic.main.item_user.view.*

class UserAdapter: androidx.recyclerview.widget.RecyclerView.Adapter<UserViewHolder>() {
    val users = mutableListOf<VKApiUserFull>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_user, parent, false)
        return UserViewHolder(view)
    }

    override fun getItemCount(): Int =
            users.size

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        holder.bind(users[position])
    }
}

class UserViewHolder(view: View): androidx.recyclerview.widget.RecyclerView.ViewHolder(view) {
    fun bind(user: VKApiUserFull) {
        // loading image
        val photoUrl = user.photo_50

        Picasso.get()
                .load(photoUrl)
                .fit()
                .into(itemView.userAvatar)

        // loading name
        val firstName = user.first_name
        val lastName = user.last_name
        val name = if(lastName.length >= firstName.length) "$lastName $firstName"
                    else "$firstName $lastName"

        itemView.userName.text = name
    }
}