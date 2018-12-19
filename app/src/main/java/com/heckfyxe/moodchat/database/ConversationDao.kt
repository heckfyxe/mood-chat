package com.heckfyxe.moodchat.database

import androidx.paging.DataSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.heckfyxe.moodchat.model.Conversation

@Dao
interface ConversationDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(vararg conversations: Conversation)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(conversations: List<Conversation>)

    @Query("SELECT * FROM conversation ORDER BY lastMessageId DESC")
    fun getConversations(): DataSource.Factory<Int, Conversation>

    @Query("SELECT * FROM conversation WHERE peerId = :peerId LIMIT 1")
    fun getConversationByPeerId(peerId: Int): Conversation
}