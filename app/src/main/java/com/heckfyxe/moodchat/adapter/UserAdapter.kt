package com.heckfyxe.moodchat.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.heckfyxe.moodchat.R
import com.heckfyxe.moodchat.model.User
import com.heckfyxe.moodchat.util.hide
import com.heckfyxe.moodchat.util.show
import kotlinx.android.synthetic.main.item_user.view.*
import kotlinx.android.synthetic.main.item_user_avatar.view.*

class UserAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    companion object {
        private const val USER_VIEW_TYPE = 0
        private const val LOADING_VIEW_TYPE = 1
    }

    private val users = mutableListOf<User?>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
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

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
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

    fun replaceUsers(userList: List<User>) {
        users.clear()
        users.addAll(userList)
        notifyDataSetChanged()
    }

    fun insertUsers(vararg user: User) {
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
        users.filter { it != null }.size

}

class UserViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    fun bind(user: User) {
        // loading image
        val thumbnail = Glide.with(itemView.userAvatar).load(user.photo50)

        Glide.with(itemView.userAvatar)
            .setDefaultRequestOptions(
                RequestOptions
                    .circleCropTransform()
                    .placeholder(R.drawable.ic_user)
            )
            .load(user.photo100)
            .thumbnail(thumbnail)
            .into(itemView.userAvatar)

        // set online status
        itemView.apply {
            onlineImageView?.hide()
            onlineMobileImageView?.hide()
            if (user.onlineMobile)
                onlineImageView?.show()
            else if (user.online)
                onlineMobileImageView?.show()
        }

        itemView.userName?.text = user.toString()
    }
}

class LoadingViewHolder(view: View) : RecyclerView.ViewHolder(view)