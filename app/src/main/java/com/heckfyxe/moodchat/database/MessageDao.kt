package com.heckfyxe.moodchat.database

import androidx.paging.DataSource
import androidx.room.*
import com.heckfyxe.moodchat.model.Attachment
import com.heckfyxe.moodchat.model.Message
import com.heckfyxe.moodchat.model.MessageWithAdditional
import kotlinx.coroutines.runBlocking

@Dao
interface MessageDao : BaseDao<Message> {
    @Query("SELECT * FROM message WHERE id = :id LIMIT 1")
    suspend fun getMessageById(id: Int): Message?

    @Query("SELECT * FROM message WHERE peerId = :peerId ORDER BY conversationMessageId ASC")
    fun getMessagesByPeerId(peerId: Int): DataSource.Factory<Int, Message?>

    @Query("SELECT * FROM message WHERE peerId = :peerId AND id < :startMessageId ORDER BY conversationMessageId DESC LIMIT :count")
    suspend fun getMessagesByPeerId(peerId: Int, startMessageId: Int, count: Int): List<Message?>

    @Query("SELECT * FROM message WHERE peerId = :peerId AND conversationMessageId = :conversationMessageId LIMIT 1")
    suspend fun getMessageByConversationMessageId(peerId: Int, conversationMessageId: Int): Message?

    @Query("SELECT * FROM message WHERE id = :id LIMIT 1")
    suspend fun getMessageWithAdditionalById(id: Int): MessageWithAdditional?

    @Query("SELECT * FROM message WHERE peerId = :peerId ORDER BY conversationMessageId ASC")
    fun getMessagesWithAdditionalByPeerId(peerId: Int): DataSource.Factory<Int, MessageWithAdditional?>

    @Query("SELECT * FROM message WHERE peerId = :peerId AND id < :startMessageId ORDER BY conversationMessageId DESC LIMIT :count")
    suspend fun getMessagesWithAdditionalByPeerId(peerId: Int, startMessageId: Int, count: Int): List<MessageWithAdditional?>

    @Query("SELECT * FROM message WHERE peerId = :peerId AND conversationMessageId = :conversationMessageId LIMIT 1")
    suspend fun getMessageWithAdditionalByConversationMessageId(peerId: Int, conversationMessageId: Int): MessageWithAdditional?

    @Transaction
    fun insertWithAdditional(messages: List<Message?>) {
        insertWithAdditional(*messages.toTypedArray())
    }

    @Transaction
    fun insertWithAdditional(vararg messages: Message?) {
        val list = messages.map { message ->
            if (!message?.attachments.isNullOrEmpty()) {
                insert(message!!.attachments!!.map {
                    it.messageId = message.id
                    it
                })
            }
            message

        }
        runBlocking {
            insert(list)
        }
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(vararg attachments: Attachment)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(attachments: List<Attachment>)
}