package com.heckfyxe.moodchat

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.heckfyxe.moodchat.model.Conversation
import com.heckfyxe.moodchat.repository.ConversationRepository
import com.vk.sdk.api.VKApiConst
import com.vk.sdk.api.VKError
import com.vk.sdk.api.VKParameters

class ConversationsViewModel(private val repository: ConversationRepository) : ViewModel() {

    private val boundaryCallback = object : PagedList.BoundaryCallback<Conversation>() {
        override fun onZeroItemsLoaded() {
            refresh()
        }

        override fun onItemAtEndLoaded(itemAtEnd: Conversation) {
            repository.refresh(
                VKParameters(
                    mapOf(
                        VKApiConst.COUNT to PAGE_SIZE,
                        VKApiConst.EXTENDED to true,
                        VKApiConst.START_MESSAGE_ID to itemAtEnd.lastMessageId,
                        VKApiConst.OFFSET to 1
                    )
                )
            )
        }
    }

    private val config = PagedList.Config.Builder()
        .setPageSize(PAGE_SIZE)
        .setPrefetchDistance(PREFETCH_DISTANCE)
        .build()

    val pagedList = LivePagedListBuilder<Int, Conversation>(repository.dataSourceFactory, config)
        .setBoundaryCallback(boundaryCallback)
        .build()

    val errors: LiveData<VKError> = Transformations.map(repository.errors) { it }

    fun refresh(): LiveData<Boolean> =
        repository.refresh(
            VKParameters(
                mapOf(
                    VKApiConst.COUNT to ConversationsViewModel.PAGE_SIZE,
                    VKApiConst.EXTENDED to true
                )
            )
        )


    companion object {
        private const val PAGE_SIZE = 20
        private const val PREFETCH_DISTANCE = 7
    }
}
