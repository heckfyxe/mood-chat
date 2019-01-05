package com.heckfyxe.moodchat.database

import androidx.paging.DataSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.heckfyxe.moodchat.model.Message

@Dao
interface MessageDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(vararg messages: Message)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(messages: List<Message>)

    @Query("SELECT * FROM message WHERE id = :id LIMIT 1")
    suspend fun getMessageById(id: Int): Message

    @Query("SELECT * FROM message WHERE peerId = :peerId ORDER BY conversationMessageId ASC")
    fun getMessagesByPeerId(peerId: Int): DataSource.Factory<Int, Message>

    @Query("SELECT * FROM message WHERE peerId = :peerId AND conversationMessageId = :conversationMessageId LIMIT 1")
    suspend fun getMessageByConversationMessageId(peerId: Int, conversationMessageId: Int): Message
}