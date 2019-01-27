package com.heckfyxe.moodchat.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.heckfyxe.moodchat.database.*
import com.heckfyxe.moodchat.model.Conversation
import com.heckfyxe.moodchat.model.Group
import com.heckfyxe.moodchat.model.Message
import com.heckfyxe.moodchat.model.User
import com.vk.sdk.api.*
import com.vk.sdk.api.model.VKApiGetConversationsResponse
import com.vk.sdk.api.model.VKApiGetConversationsResponseItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.standalone.KoinComponent
import org.koin.standalone.inject

class ConversationRepository: KoinComponent {

    private val database: AppDatabase by inject()
    private val conversationDao: ConversationDao by inject()
    private val userDao: UserDao by inject()
    private val groupDao: GroupDao by inject()
    private val messageDao: MessageDao by inject()

    val dataSourceFactory = conversationDao.getConversations()

    private val _errors = MutableLiveData<VKError>()
    val errors: LiveData<VKError> = Transformations.map(_errors) { it }

    private val scope = CoroutineScope(Dispatchers.IO)

    fun updateDatabase(response: VKApiGetConversationsResponse) {
        response.apply {
            val profilesDef = profiles?.map {
                User(it)
            }
            val groupsDef = groups?.map {
                Group(it)
            }

            val messages = mutableListOf<Message>()
            val conversations = mutableListOf<Conversation>()
            val conversationLastMessageIdSet = mutableSetOf<Int>()
            var minLastMessageId = items.getOrNull(0)?.last_message?.id
            var maxLastMessageId = items.getOrNull(0)?.last_message?.id
            items?.map<VKApiGetConversationsResponseItem, Unit> {
                conversations.add(Conversation(it.conversation))
                messages.add(Message(it.last_message))
                conversationLastMessageIdSet.add(it.conversation.last_message_id)
                if (minLastMessageId != null && maxLastMessageId != null) {
                    val lastMessageId = it.last_message.id
                    if (lastMessageId < minLastMessageId!!)
                        minLastMessageId = lastMessageId
                    if (lastMessageId > maxLastMessageId!!)
                        maxLastMessageId = lastMessageId
                }
            }

            scope.launch {
                val localConversations = if (minLastMessageId != null && maxLastMessageId != null) {
                    conversationDao.getConversationRange(minLastMessageId!!, maxLastMessageId!!)
                } else
                    emptyList()

                val conversationsForUpdate = mutableListOf<Conversation>()

                val localConversationLastMessageSet = HashSet<Int>()
                val conversationsForDelete = localConversations.filter {
                    localConversationLastMessageSet.add(it.lastMessageId)
                    if (!conversationLastMessageIdSet.contains(it.lastMessageId)) {
                        true
                    } else {
                        conversationsForUpdate.add(it)
                        false
                    }
                }

                val conversationsForInsert =
                        with(conversationLastMessageIdSet - localConversationLastMessageSet) {
                    conversations.flatMap {
                        if (contains(it.lastMessageId))
                            listOf(it)
                        else
                            emptyList()
                    }
                }

                database.runInTransaction {
                    launch {
                        conversationDao.apply {
                            delete(conversationsForDelete)
                            update(conversationsForUpdate)
                            insert(conversationsForInsert)
                        }

                        messageDao.insert(messages)
                        userDao.insert(profilesDef ?: emptyList())
                        groupDao.insert(groupsDef ?: emptyList())
                    }
                }
            }
        }
    }

    fun refresh(params: VKParameters): LiveData<Boolean> {
        val updateStatus = MutableLiveData<Boolean>()
        VKApi.messages().getConversations(params).executeWithListener(object: VKRequest.VKRequestListener() {
            override fun onComplete(response: VKResponse?) {
                requestListener.onComplete(response)
                updateStatus.postValue(true)
            }

            override fun onError(error: VKError?) {
                requestListener.onError(error)
                updateStatus.postValue(false)
            }
        })
        return updateStatus
    }

    val requestListener = object : VKRequest.VKRequestListener() {
        override fun onComplete(response: VKResponse?) {
            val conversationResponse = VKApiGetConversationsResponse(response!!.json)
            updateDatabase(conversationResponse)
        }

        override fun onError(error: VKError?) {
            _errors.postValue(error)
        }
    }
}