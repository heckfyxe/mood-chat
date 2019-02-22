package com.heckfyxe.moodchat.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.heckfyxe.moodchat.R
import com.heckfyxe.moodchat.databinding.ItemUserBinding
import com.heckfyxe.moodchat.model.User

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
        DataBindingUtil.bind<ItemUserBinding>(itemView)?.user = user
    }
}

class LoadingViewHolder(view: View) : RecyclerView.ViewHolder(view)