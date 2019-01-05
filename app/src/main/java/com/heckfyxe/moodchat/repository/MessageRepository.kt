package com.heckfyxe.moodchat.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.heckfyxe.moodchat.database.GroupDao
import com.heckfyxe.moodchat.database.MessageDao
import com.heckfyxe.moodchat.database.UserDao
import com.heckfyxe.moodchat.model.Group
import com.heckfyxe.moodchat.model.Message
import com.heckfyxe.moodchat.model.User
import com.vk.sdk.api.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.koin.standalone.KoinComponent
import org.koin.standalone.inject

class MessageRepository(private val peerId: Int): KoinComponent {
    private val messageDao: MessageDao by inject()
    private val userDao: UserDao by inject()
    private val groupDao: GroupDao by inject()

    private val _errors = MutableLiveData<VKError>()
    val errors: LiveData<VKError> = Transformations.map(_errors) { it!! }

    val dataSourceFactory = messageDao.getMessagesByPeerId(peerId)

    val scope = CoroutineScope(Dispatchers.IO)

    fun updateHistory(startMessageId: Int, count: Int = 20) {
        VKApi.messages().getHistory(VKParameters(mapOf(
            VKApiConst.PEER_ID to peerId,
            VKApiConst.START_MESSAGE_ID to startMessageId,
            VKApiConst.EXTENDED to true,
            VKApiConst.COUNT to count,
            VKApiConst.OFFSET to 1,
            VKApiConst.VERSION to 5.48
        ))).executeWithListener(object : VKRequest.VKRequestListener() {
            override fun onComplete(response: VKResponse) {
                val responseJsonObject = response.json.optJSONObject("response")
                val messages = responseJsonObject.optJSONArray("items")
                val profiles: JSONArray? = responseJsonObject.optJSONArray("profiles")
                val groups: JSONArray? = responseJsonObject.optJSONArray("groups")

                scope.launch {
                    if (profiles != null)
                        userDao.insert(List(profiles.length()) {
                            User(profiles.getJSONObject(it))
                        })

                    if (groups != null)
                        groupDao.insert(List(groups.length()) {
                            Group(groups.getJSONObject(it))
                        })

                    messageDao.insert(List(messages.length()) {
                        Message(messages.getJSONObject(it))
                    })
                }
            }

            override fun onError(error: VKError) {
                _errors.postValue(error)
            }
        })
    }
}