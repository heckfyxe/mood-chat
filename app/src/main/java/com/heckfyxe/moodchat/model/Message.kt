package com.heckfyxe.moodchat.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Ignore
import androidx.room.Index
import com.vk.sdk.api.model.VKApiMessage
import com.vk.sdk.api.model.VKApiPhoto
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
        var attachments: List<Attachment>? = null,

        var important: Boolean = false,

        @Ignore
        var fwdMessages: VKList<VKApiMessage>? = null,

        var conversationMessageId: Int = 0) {

    companion object {
        @JvmStatic
        fun create(source: JSONObject): Message =
                create(VKApiMessage(source))

        @JvmStatic
        @Suppress("IMPLICIT_CAST_TO_ANY")
        fun create(message: VKApiMessage): Message {
            var list: List<Attachment>? = null
            if (message.attachments.isNotEmpty()) {
                list = message.attachments.flatMap {
                    if (it.type == VKAttachments.TYPE_PHOTO) {
                        listOf(Photo.create(it as VKApiPhoto))
                    } else listOf()
                }
            }
            return Message(
                    id = message.id,
                    date = message.date,
                    peerId = message.peer_id,
                    fromId = message.from_id,
                    text = message.text,
                    randomId = message.random_id,
                    out = message.out,
                    attachments = list,
                    important = message.important,
                    fwdMessages = message.fwd_messages,
                    conversationMessageId = message.conversation_message_id)
        }
    }
}