package com.heckfyxe.moodchat

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
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

class ConversationsViewModel(
    private val conversationDao: ConversationDao,
    private val userDao: UserDao,
    private val groupDao: GroupDao,
    private val messageDao: MessageDao
) : ViewModel() {

    val requestListener = object : VKRequest.VKRequestListener() {
        override fun onComplete(response: VKResponse?) {
            val conversationResponse = VKApiGetConversationsResponse(response!!.json)
            conversationResponse.apply {
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

        override fun onError(error: VKError?) {
            errors.postValue(error)
        }
    }

    private val boundaryCallback = object : PagedList.BoundaryCallback<Conversation>() {
        override fun onZeroItemsLoaded() {
            refresh()
        }

        override fun onItemAtEndLoaded(itemAtEnd: Conversation) {
            VKApi.messages().getConversations(
                VKParameters(
                    mapOf(
                        VKApiConst.COUNT to PAGE_SIZE,
                        VKApiConst.EXTENDED to true,
                        VKApiConst.START_MESSAGE_ID to itemAtEnd.lastMessageId,
                        VKApiConst.OFFSET to 1
                    )
                )
            ).executeWithListener(requestListener)
        }
    }

    fun refresh() {
        VKApi.messages().getConversations(
            VKParameters(
                mapOf(
                    VKApiConst.COUNT to PAGE_SIZE,
                    VKApiConst.EXTENDED to true
                )
            )
        ).executeWithListener(requestListener)
    }

    private val dataSource = conversationDao.getConversations()

    private val config = PagedList.Config.Builder()
        .setPageSize(PAGE_SIZE)
        .setPrefetchDistance(PREFETCH_DISTANCE)
        .build()

    val pagedList = LivePagedListBuilder<Int, Conversation>(dataSource, config)
        .setBoundaryCallback(boundaryCallback)
        .build()

    val errors = MutableLiveData<VKError>()

    companion object {
        private const val PAGE_SIZE = 20
        private const val PREFETCH_DISTANCE = 7
    }
}
