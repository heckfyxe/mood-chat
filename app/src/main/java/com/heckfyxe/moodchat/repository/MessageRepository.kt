package com.heckfyxe.moodchat.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.heckfyxe.moodchat.database.GroupDao
import com.heckfyxe.moodchat.database.MessageDao
import com.heckfyxe.moodchat.database.UserDao
import com.vk.sdk.api.VKError
import org.koin.standalone.KoinComponent
import org.koin.standalone.inject

class MessageRepository(peerId: Int) : KoinComponent {
    private val messageDao: MessageDao by inject()
    private val userDao: UserDao by inject()
    private val groupDao: GroupDao by inject()

    private val _errors = MutableLiveData<VKError>()
    val errors: LiveData<VKError> = Transformations.map(_errors) { it!! }

    val dataSourceFactory = MessageDataSource.Factory(peerId)
}