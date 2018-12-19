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
    fun insert(vararg messages: Message)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(messages: List<Message>)

    @Query("SELECT * FROM message WHERE id = :id LIMIT 1")
    fun getMessageById(id: Int): Message

    @Query("SELECT * FROM message WHERE peerId = :peerId ORDER BY conversationMessageId DESC")
    fun getMessagesByPeerId(peerId: Int): DataSource.Factory<Int, Message>

    @Query("SELECT * FROM message WHERE peerId = :peerId AND conversationMessageId = :conversationMessageId LIMIT 1")
    fun getMessageByConversationMessageId(peerId: Int, conversationMessageId: Int): Message
}