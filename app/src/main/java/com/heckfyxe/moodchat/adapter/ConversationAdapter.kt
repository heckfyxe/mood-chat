package com.heckfyxe.moodchat.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.heckfyxe.moodchat.R
import com.heckfyxe.moodchat.database.GroupDao
import com.heckfyxe.moodchat.database.MessageDao
import com.heckfyxe.moodchat.database.UserDao
import com.heckfyxe.moodchat.databinding.ItemChatConversationBinding
import com.heckfyxe.moodchat.databinding.ItemGroupConversationBinding
import com.heckfyxe.moodchat.databinding.ItemUserConversationBinding
import com.heckfyxe.moodchat.model.Conversation
import com.heckfyxe.moodchat.model.Message
import com.heckfyxe.moodchat.model.User
import com.vk.sdk.api.model.VKApiConversation.Type.*
import kotlinx.android.synthetic.main.item_chat_conversation.view.*
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
        val view = when (viewType) {
            GROUP_CONVERSATION_TYPE ->
                inflater.inflate(R.layout.item_group_conversation, parent, false)
            CHAT_CONVERSATION_TYPE ->
                inflater.inflate(R.layout.item_chat_conversation, parent, false)
            USER_CONVERSATION_TYPE -> {
                inflater.inflate(R.layout.item_user_conversation, parent, false)
            }
            else -> throw Exception("Unknown view type")
        }
        return ConversationViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ConversationViewHolder -> {
                holder.bind(getItem(position))
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        val item = getItem(position) ?: return LOADING_VIEW_TYPE

        return when (item.type) {
            USER, EMAIL -> USER_CONVERSATION_TYPE
            GROUP -> GROUP_CONVERSATION_TYPE
            CHAT -> CHAT_CONVERSATION_TYPE
        }
    }


    companion object {
        private const val GROUP_CONVERSATION_TYPE = 0
        private const val CHAT_CONVERSATION_TYPE = 1
        private const val USER_CONVERSATION_TYPE = 2
        private const val LOADING_VIEW_TYPE = 3

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
                        lastMessageImageView?.visibility = View.GONE
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
                val user: Deferred<User?> = async(Dispatchers.IO) {
                    userDao.getUserById(conversation.peerId)
                }
                val lastMessage: Deferred<Message?> = async(Dispatchers.IO) {
                    messageDao.getMessageById(conversation.lastMessageId)
                }

                val binding = DataBindingUtil.bind<ItemUserConversationBinding>(itemView)
                user.await().let {
                    binding?.user = it
                }

                binding?.message = lastMessage.await()
            }
        }

        /**
         * @param conversation must have non-null [Conversation.chatSettings]
         */
        private fun bindChatConversation(conversation: Conversation) {
            val binding = DataBindingUtil.bind<ItemChatConversationBinding>(itemView)

            binding?.chat = conversation.chatSettings
            scope.launch(Dispatchers.IO) {
                val message: Message? = messageDao.getMessageById(conversation.lastMessageId)
                val user: User? = userDao.getUserById(message?.fromId ?: -1)
                withContext(Dispatchers.Main) {
                    binding?.message = message
                    binding?.messageSender = user
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

                val binding = DataBindingUtil.bind<ItemGroupConversationBinding>(itemView)
                binding?.group = group.await()
                binding?.message = message.await()
            }
        }
    }
}