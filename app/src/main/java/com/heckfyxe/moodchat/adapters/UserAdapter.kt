package com.heckfyxe.moodchat.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.heckfyxe.moodchat.R
import com.vk.sdk.api.model.VKApiUserFull
import kotlinx.android.synthetic.main.item_user.view.*

class UserAdapter: RecyclerView.Adapter<androidx.recyclerview.widget.RecyclerView.ViewHolder>() {
    companion object {
        private const val USER_VIEW_TYPE = 0
        private const val LOADING_VIEW_TYPE = 1
    }

    private val users = mutableListOf<VKApiUserFull?>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): androidx.recyclerview.widget.RecyclerView.ViewHolder =
            when (viewType) {
                USER_VIEW_TYPE -> {
                    val view = LayoutInflater.from(parent.context)
                            .inflate(R.layout.item_user, parent, false)
                    UserViewHolder(view)
                }
                else -> {
                    val view = LayoutInflater.from(parent.context)
                            .inflate(R.layout.item_loading, parent, false)
                    LoadingViewHolder(view)
                }
            }

    override fun getItemCount(): Int =
            users.size

    override fun onBindViewHolder(holder: androidx.recyclerview.widget.RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is UserViewHolder -> holder.bind(users[position]!!)
        }
    }

    override fun getItemViewType(position: Int): Int =
            if (users[position] == null)
                LOADING_VIEW_TYPE
            else
                USER_VIEW_TYPE


    fun showLoadingView() {
        users.add(null)
        notifyItemInserted(users.lastIndex)
    }

    fun hideLoadingView() {
        val index = users.indexOfLast { it == null }
        if (index != -1) {
            users.removeAt(index)
            notifyItemRemoved(index)
        }

    }

    fun replaceUsers(userList: List<VKApiUserFull>) {
        users.clear()
        users.addAll(userList)
        notifyDataSetChanged()
    }

    fun insertUsers(vararg user: VKApiUserFull) {
        if (user.isEmpty())
            return

        val startIndex = users.lastIndex + 1
        users.addAll(user)
        val endIndex = user.lastIndex

        for (index in startIndex..endIndex) {
            notifyItemInserted(index)
        }
    }

    fun clear() {
        users.clear()
    }

    fun size(): Int =
            if (users.last() == null)
                users.size - 1
            else
                users.size

}

class UserViewHolder(view: View): RecyclerView.ViewHolder(view) {
    fun bind(user: VKApiUserFull) {
        // loading image
        val photoUrl = user.photo_50

        Glide.with(itemView)
                .setDefaultRequestOptions(RequestOptions.circleCropTransform())
                .load(photoUrl)
                .into(itemView.userAvatar)


        // loading name
        val firstName = user.first_name
        val lastName = user.last_name
        val name = if(lastName.length >= firstName.length) "$lastName $firstName"
                    else "$firstName $lastName"

        itemView.userName.text = name
    }
}

class LoadingViewHolder(view: View): androidx.recyclerview.widget.RecyclerView.ViewHolder(view)