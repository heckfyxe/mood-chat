package com.heckfyxe.moodchat.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.heckfyxe.moodchat.R
import com.heckfyxe.moodchat.database.GroupDao
import com.heckfyxe.moodchat.database.MessageDao
import com.heckfyxe.moodchat.database.UserDao
import com.heckfyxe.moodchat.model.Conversation
import com.heckfyxe.moodchat.model.Message
import com.heckfyxe.moodchat.util.*
import com.vk.sdk.api.model.VKApiConversation
import com.vk.sdk.api.model.VKApiConversation.Type.*
import kotlinx.android.synthetic.main.item_non_user_conversation.view.*
import kotlinx.android.synthetic.main.item_user_avatar.view.*
import kotlinx.coroutines.*
import org.koin.standalone.KoinComponent
import org.koin.standalone.inject

class ConversationAdapter(val onClick: (Conversation) -> Unit) :
        PagedListAdapter<Conversation, RecyclerView.ViewHolder>(DIFF),
        KoinComponent {

    private val userDao: UserDao by inject()
    private val messageDao: MessageDao by inject()
    private val groupDao: GroupDao by inject()

    private val scope: CoroutineScope = CoroutineScope(Dispatchers.Main)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            CONVERSATION_TYPE -> {
                val view = inflater.inflate(R.layout.item_non_user_conversation, parent, false)
                ConversationViewHolder(view)
            }
            USER_CONVERSATION_TYPE -> {
                val view = inflater.inflate(R.layout.item_user_conversation, parent, false)
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

    override fun getItemViewType(position: Int): Int {
        val item = getItem(position)

        if (item == null)
            LOADING_VIEW_TYPE

        return if (item!!.type == VKApiConversation.Type.USER)
            USER_CONVERSATION_TYPE
        else
            CONVERSATION_TYPE
    }


    companion object {
        private const val CONVERSATION_TYPE = 0
        private const val USER_CONVERSATION_TYPE = 1
        private const val LOADING_VIEW_TYPE = 2

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
                USER, EMAIL -> bindUserConversation(conversation)
                CHAT -> bindChatConversation(conversation)
                GROUP -> bindGroupConversation(conversation)
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

            if (conversation != null)
                itemView.setOnClickListener {
                    onClick(conversation)
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
                    itemView.apply {
                        userAvatar?.loadUser(it)
                        onlineImageView?.gone()
                        onlineMobileImageView?.gone()
                        if (it.onlineMobile)
                            onlineMobileImageView?.show()
                        else if (it.online)
                            onlineImageView?.show()

                        conversationNameTextView.text =
                                String.format("%s %s", it.lastName, it.firstName)

                    }
                }

                itemView.lastMessageTextView?.text = lastMessage.await().text
            }
        }

        /**
         * @param conversation must have non-null [Conversation.chatSettings]
         */
        private fun bindChatConversation(conversation: Conversation) {
            itemView.conversationNameTextView?.text = conversation.chatSettings?.title
            itemView.conversationImageView?.loadChat(conversation.chatSettings)
            scope.launch(Dispatchers.IO) {
                val message: Message? = messageDao.getMessageById(conversation.lastMessageId)
                withContext(Dispatchers.Main) {
                    itemView.lastMessageTextView?.text = message?.text ?: ""
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