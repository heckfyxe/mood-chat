package com.heckfyxe.moodchat

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.heckfyxe.moodchat.model.Message
import com.heckfyxe.moodchat.repository.MessageRepository
import com.vk.sdk.api.VKError

class MessagesViewModel : ViewModel() {
    private lateinit var repository: MessageRepository

    lateinit var errors: LiveData<VKError>

    lateinit var pagedList: LiveData<PagedList<Message>>

//    private val boundaryCallback = object: PagedList.BoundaryCallback<Message>() {
//        override fun onItemAtFrontLoaded(itemAtFront: Message) {
//            Log.i("MessagesViewModel", "onItemAtFrontLoaded: id=${itemAtFront.id}, " +
//                    "conversation_message_id=${itemAtFront.conversationMessageId}")
//            updateHistory(itemAtFront.id, PAGE_SIZE)
//        }
//
//
//    }

    fun init(peerId: Int, lastMessageId: Int) {
        repository = MessageRepository(peerId)
        errors = Transformations.map(repository.errors) { it }
        pagedList = LivePagedListBuilder<Int, Message>(repository.dataSourceFactory, config)
            .setInitialLoadKey(if (lastMessageId != -1) lastMessageId else null)
//            .setBoundaryCallback(boundaryCallback)
            .build()
    }

//    fun updateHistory(startMessageId: Int, count: Int) {
//        repository.updateHistory(startMessageId, count)
//    }

    companion object {
        private const val PREFETCH_DISTANCE = 10
        private const val PAGE_SIZE = 20

        private val config = PagedList.Config.Builder()
            .setEnablePlaceholders(false)
            .setPageSize(PAGE_SIZE)
            .setPrefetchDistance(PREFETCH_DISTANCE)
            .build()
    }
}
