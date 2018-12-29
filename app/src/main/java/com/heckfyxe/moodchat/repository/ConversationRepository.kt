package com.heckfyxe.moodchat.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.heckfyxe.moodchat.database.ConversationDao
import com.heckfyxe.moodchat.database.GroupDao
import com.heckfyxe.moodchat.database.MessageDao
import com.heckfyxe.moodchat.database.UserDao
import com.heckfyxe.moodchat.model.Conversation
import com.heckfyxe.moodchat.model.Group
import com.heckfyxe.moodchat.model.Message
import com.heckfyxe.moodchat.model.User
import com.vk.sdk.api.*
import com.vk.sdk.api.model.VKApiGetConversationsResponse
import kotlinx.coroutines.*
import org.koin.standalone.KoinComponent
import org.koin.standalone.inject

class ConversationRepository: KoinComponent {

    private val conversationDao: ConversationDao by inject()
    private val userDao: UserDao by inject()
    private val groupDao: GroupDao by inject()
    private val messageDao: MessageDao by inject()

    val dataSourceFactory = conversationDao.getConversations()

    private val _errors = MutableLiveData<VKError>()
    val errors: LiveData<VKError> = Transformations.map(_errors) { it }

    fun updateDatabase(response: VKApiGetConversationsResponse) {
        response.apply {
            val profilesDef = profiles?.map {
                GlobalScope.async(Dispatchers.IO) {
                    userDao.insert(User(it))
                }
            }
            val groupsDef = groups?.map {
                GlobalScope.async(Dispatchers.IO) {
                    groupDao.insert(Group(it))
                }
            }
            val itemsDef = items?.map {
                GlobalScope.async(Dispatchers.IO) {
                    messageDao.insert(Message(it.last_message))
                    conversationDao.insert(Conversation(it.conversation))
                }
            }
            GlobalScope.launch {
                profilesDef?.awaitAll()
                groupsDef?.awaitAll()
                itemsDef?.awaitAll()
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