package com.heckfyxe.moodchat.database

import androidx.paging.DataSource
import androidx.room.*
import com.heckfyxe.moodchat.model.Conversation

@Dao
interface ConversationDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(vararg conversations: Conversation)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(conversations: List<Conversation>)

    @Query("SELECT * FROM conversation ORDER BY lastMessageId DESC")
    fun getConversations(): DataSource.Factory<Int, Conversation>

    @Query("SELECT * FROM conversation WHERE peerId = :peerId LIMIT 1")
    suspend fun getConversationByPeerId(peerId: Int): Conversation

    @Query("SELECT * FROM conversation WHERE lastMessageId BETWEEN :start AND :end")
    suspend fun getConversationRange(start: Int, end: Int): List<Conversation>

    @Update
    suspend fun update(vararg conversations: Conversation)

    @Update
    suspend fun update(conversations: List<Conversation>)

    @Delete
    suspend fun delete(vararg conversation: Conversation)

    @Delete
    suspend fun delete(conversations: List<Conversation>)
}