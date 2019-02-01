package com.heckfyxe.moodchat.repository

import android.util.Log
import androidx.paging.DataSource
import androidx.paging.ItemKeyedDataSource
import com.heckfyxe.moodchat.database.AppDatabase
import com.heckfyxe.moodchat.database.GroupDao
import com.heckfyxe.moodchat.database.MessageDao
import com.heckfyxe.moodchat.database.UserDao
import com.heckfyxe.moodchat.model.Group
import com.heckfyxe.moodchat.model.Message
import com.heckfyxe.moodchat.model.User
import com.vk.sdk.api.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.koin.standalone.KoinComponent
import org.koin.standalone.inject

class MessageDataSource(private val peerId: Int): ItemKeyedDataSource<Int, Message>(), KoinComponent {

    private val database: AppDatabase by inject()
    private val messageDao: MessageDao by inject()
    private val userDao: UserDao by inject()
    private val groupDao: GroupDao by inject()

    private val scope = CoroutineScope(Dispatchers.IO)

    override fun loadInitial(
        params: LoadInitialParams<Int>,
        callback: LoadInitialCallback<Message>
    ) {
        scope.launch {
            val message = messageDao.getMessageById(params.requestedInitialKey!!)
            callback.onResult(listOf(message))
        }
    }

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Message>) {
        val startMessageId = params.key
        val count = params.requestedLoadSize

        scope.launch {
            val channel = updateHistory(startMessageId, count)
            channel.receive()

            val messages = messageDao.getMessagesByPeerId(peerId, startMessageId, count)
            callback.onResult(messages)
        }
    }

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Message>) {

    }

    override fun getKey(item: Message): Int =
            item.id

    private fun updateHistory(startMessageId: Int, count: Int = 20): ReceiveChannel<Boolean> {
        val channel = Channel<Boolean>(1)

        VKApi.messages().getHistory(
            VKParameters(mapOf(
                VKApiConst.PEER_ID to peerId,
                VKApiConst.START_MESSAGE_ID to startMessageId,
                VKApiConst.EXTENDED to true,
                VKApiConst.COUNT to count,
                VKApiConst.OFFSET to 1,
                VKApiConst.VERSION to 5.48
            ))
        ).executeWithListener(object : VKRequest.VKRequestListener() {
            override fun onComplete(response: VKResponse) {
                val responseJsonObject = response.json.optJSONObject("response")
                val messages = responseJsonObject.optJSONArray("items")
                val profiles: JSONArray? = responseJsonObject.optJSONArray("profiles")
                val groups: JSONArray? = responseJsonObject.optJSONArray("groups")

                scope.launch {
                    database.runInTransaction {
                        launch {
                            if (profiles != null)
                                userDao.insert(List(profiles.length()) {
                                    User.create(profiles.getJSONObject(it))
                                })

                            if (groups != null)
                                groupDao.insert(List(groups.length()) {
                                    Group.create(groups.getJSONObject(it))
                                })

                            messageDao.insert(List(messages.length()) {
                                Message.create(messages.getJSONObject(it))
                            })
                        }
                    }

                    channel.send(true)
                }
            }

            override fun onError(error: VKError) {
//                _errors.postValue(error)
                Log.e("MessageDataSource", error.toString())
                scope.launch { channel.send(false) }
            }
        })
        return channel
    }

    class Factory(private val peerId: Int): DataSource.Factory<Int, Message>() {
        override fun create(): DataSource<Int, Message> =
            MessageDataSource(peerId)
    }
}