package com.heckfyxe.moodchat.adapter

import android.view.View
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.heckfyxe.moodchat.R
import com.heckfyxe.moodchat.database.UserDao
import com.heckfyxe.moodchat.model.MessageWithAdditional
import com.heckfyxe.moodchat.model.User
import com.heckfyxe.moodchat.util.getHHMM
import com.heckfyxe.moodchat.util.inflate
import com.heckfyxe.moodchat.util.loadUser
import kotlinx.android.synthetic.main.item_chat_in_message.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.standalone.KoinComponent
import org.koin.standalone.inject

class MessageAdapter(private val isChat: Boolean):
        PagedListAdapter<MessageWithAdditional, MessageAdapter.MessageViewHolder>(DIFF),
        KoinComponent {

    val userDao: UserDao by inject()

    val scope = CoroutineScope(Dispatchers.Main)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val view = when (viewType) {
            IN_MESSAGE_VIEW_TYPE -> inflate(parent, R.layout.item_in_message)
            OUT_MESSAGE_VIEW_TYPE -> inflate(parent, R.layout.item_out_message)
            IN_CHAT_MESSAGE_VIEW_TYPE -> inflate(parent, R.layout.item_chat_in_message)
            else -> throw Exception("Unknown ViewType")
        }
        return MessageViewHolder(view)
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        holder.bind(getItem(position)!!)
    }

    override fun getItemViewType(position: Int): Int {
        val messageWithAdditional = getItem(position)!!

        if (messageWithAdditional.message.out)
            return OUT_MESSAGE_VIEW_TYPE

        return when (isChat) {
            true -> IN_CHAT_MESSAGE_VIEW_TYPE
            false -> IN_MESSAGE_VIEW_TYPE
        }
    }

    inner class MessageViewHolder(view: View): RecyclerView.ViewHolder(view) {
        fun bind(messageWithAdditional: MessageWithAdditional) {
            itemView.apply {
                messageWithAdditional.message.also { message ->
                    if (isChat && !message.out) {
                        scope.launch {
                            val user: User? = withContext(Dispatchers.IO) {
                                userDao.getUserById(message.fromId)
                            }
                            user ?: return@launch

                            messageAvatar?.loadUser(user)
                            messageUsername?.text = user.firstName
                        }
                    }
                    messageText?.text = message.text
                    val time = getHHMM(message.date)
                    messageTime?.text = time
                }
            }
        }
    }

    companion object {
        private const val IN_MESSAGE_VIEW_TYPE = 0
        private const val OUT_MESSAGE_VIEW_TYPE = 1
        private const val IN_CHAT_MESSAGE_VIEW_TYPE = 2

        private val DIFF = object : DiffUtil.ItemCallback<MessageWithAdditional>() {
            override fun areItemsTheSame(oldItem: MessageWithAdditional,
                                         newItem: MessageWithAdditional) =
                    oldItem.message.id == newItem.message.id

            override fun areContentsTheSame(oldItem: MessageWithAdditional,
                                            newItem: MessageWithAdditional) =
                oldItem == newItem
        }
    }
}