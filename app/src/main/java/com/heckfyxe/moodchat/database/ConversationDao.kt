package com.heckfyxe.moodchat.database

import androidx.paging.DataSource
import androidx.room.Dao
import androidx.room.Query
import com.heckfyxe.moodchat.model.Conversation

@Dao
interface ConversationDao : BaseDao<Conversation> {
    @Query("SELECT * FROM conversation ORDER BY lastMessageId DESC")
    fun getConversations(): DataSource.Factory<Int, Conversation?>

    @Query("SELECT * FROM conversation WHERE peerId = :peerId LIMIT 1")
    suspend fun getConversationByPeerId(peerId: Int): Conversation?

    @Query("SELECT * FROM conversation WHERE lastMessageId BETWEEN :start AND :end")
    suspend fun getConversationRange(start: Int, end: Int): List<Conversation?>
}