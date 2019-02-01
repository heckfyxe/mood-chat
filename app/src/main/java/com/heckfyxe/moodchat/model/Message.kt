package com.heckfyxe.moodchat.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Ignore
import androidx.room.Index
import com.vk.sdk.api.model.VKApiMessage
import com.vk.sdk.api.model.VKAttachments
import com.vk.sdk.api.model.VKList
import org.json.JSONObject

@Entity(primaryKeys = ["id"],
        indices = [Index(value = ["id"], unique = true)],
        foreignKeys = [ForeignKey(
                entity = Conversation::class,
                parentColumns = ["peerId"],
                childColumns = ["peerId"],
                onDelete = ForeignKey.CASCADE)])
data class Message(
        var id: Int = 0,

        var date: Long = 0L,

        var peerId: Int = 0,

        var fromId: Int = 0,

        var text: String = "",

        var randomId: Int = 0,

        var out: Boolean = false,

        @Ignore
        var attachments: VKAttachments? = null,

        var important: Boolean = false,

        @Ignore
        var fwdMessages: VKList<VKApiMessage>? = null,

        var conversationMessageId: Int = 0) {

    companion object {
        @JvmStatic
        fun create(source: JSONObject): Message =
                create(VKApiMessage(source))

        @JvmStatic
        fun create(message: VKApiMessage) =
                Message(
                        id = message.id,
                        date = message.date,
                        peerId = message.peer_id,
                        fromId = message.from_id,
                        text = message.text,
                        randomId = message.random_id,
                        out = message.out,
                        attachments = message.attachments,
                        important = message.important,
                        fwdMessages = message.fwd_messages,
                        conversationMessageId = message.conversation_message_id)
    }
}