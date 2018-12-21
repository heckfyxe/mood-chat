package com.heckfyxe.moodchat.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.heckfyxe.moodchat.R
import com.heckfyxe.moodchat.database.GroupDao
import com.heckfyxe.moodchat.database.MessageDao
import com.heckfyxe.moodchat.database.UserDao
import com.heckfyxe.moodchat.model.Conversation
import com.heckfyxe.moodchat.util.loadGroup
import com.heckfyxe.moodchat.util.loadUser
import com.vk.sdk.api.model.VKApiConversation
import kotlinx.android.synthetic.main.item_conversation.view.*
import kotlinx.coroutines.*
import org.koin.standalone.KoinComponent
import org.koin.standalone.inject

class ConversationAdapter : PagedListAdapter<Conversation, RecyclerView.ViewHolder>(DIFF),
    KoinComponent {

    private val userDao: UserDao by inject()
    private val messageDao: MessageDao by inject()
    private val groupDao: GroupDao by inject()

    private val scope: CoroutineScope = CoroutineScope(Dispatchers.Main)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            CONVERSATION_TYPE -> {
                val view = inflater.inflate(R.layout.item_conversation, parent, false)
                ConversationViewHolder(view)
            }
            else -> throw Exception("Unknown view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ConversationViewHolder -> {
                holder.bind(getItem(position))
            }
        }
    }

    override fun getItemViewType(position: Int): Int =
        if (getItem(position) != null)
            CONVERSATION_TYPE
        else
            LOADING_VIEW_TYPE


    companion object {
        private const val CONVERSATION_TYPE = 0
        private const val LOADING_VIEW_TYPE = 1

        private val DIFF = object : DiffUtil.ItemCallback<Conversation>() {
            override fun areItemsTheSame(oldItem: Conversation, newItem: Conversation) =
                oldItem.peerId == newItem.peerId

            override fun areContentsTheSame(oldItem: Conversation, newItem: Conversation) =
                oldItem == newItem
        }
    }

    inner class ConversationViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bind(conversation: Conversation?) {
            when (conversation?.type) {
                VKApiConversation.Type.USER -> bindUserConversation(conversation)
                VKApiConversation.Type.CHAT -> bindChatConversation(conversation)
                VKApiConversation.Type.GROUP -> bindGroupConversation(conversation)
                null -> {
                    itemView.apply {
                        Glide.with(conversationImageView)
                            .load(R.drawable.ic_user)
                            .into(conversationImageView)
                        conversationNameTextView?.text = ""
                        lastMessageTextView?.text = ""
                    }
                }
            }
        }

        private fun bindUserConversation(conversation: Conversation) {
            scope.launch(Dispatchers.Main) {
                val user = async(Dispatchers.IO) {
                    userDao.getUserById(conversation.peerId)
                }
                val lastMessage = async(Dispatchers.IO) {
                    messageDao.getMessageById(conversation.lastMessageId)
                }

                user.await().let {
                    itemView.conversationImageView?.loadUser(it)
                    itemView.conversationNameTextView.text =
                            String.format("%s %s", it.lastName, it.firstName)

                }

                itemView.lastMessageTextView?.text = lastMessage.await().text
            }
        }

        private fun bindChatConversation(conversation: Conversation) {
            itemView.conversationNameTextView?.text = conversation.chatSettings?.title
            Glide.with(itemView.conversationImageView)
                .setDefaultRequestOptions(
                    RequestOptions
                        .circleCropTransform()
                )
                .load(R.drawable.ic_group)
                .into(itemView.conversationImageView)
            scope.launch(Dispatchers.IO) {
                val message = messageDao.getMessageById(conversation.lastMessageId)
                withContext(Dispatchers.Main) {
                    itemView.lastMessageTextView?.text = message.text
                }
            }
        }

        private fun bindGroupConversation(conversation: Conversation) {
            scope.launch(Dispatchers.Main) {
                val group = async(Dispatchers.IO) {
                    groupDao.getGroupById(conversation.localId)
                }
                val message = async(Dispatchers.IO) {
                    messageDao.getMessageById(conversation.lastMessageId)
                }

                itemView.apply {
                    group.await().let {
                        conversationNameTextView?.text = it.name
                        conversationImageView.loadGroup(it)
                    }
                    lastMessageTextView?.text = message.await().text
                }
            }
        }
    }
}