package com.heckfyxe.moodchat.model

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.Index
import com.vk.sdk.api.model.VKApiMessage
import com.vk.sdk.api.model.VKAttachments
import com.vk.sdk.api.model.VKList
import org.json.JSONObject

@Entity(primaryKeys = ["id"], indices = [Index(value = ["id"], unique = true)])
class Message {

    var id: Int = 0

    var date: Long = 0L

    var peerId: Int = 0

    var fromId: Int = 0

    var text: String = ""

    var randomId: Int = 0

    @Ignore
    var attachments: VKAttachments? = null

    var important: Boolean = false

    @Ignore
    var fwdMessages: VKList<VKApiMessage>? = null

    var conversationMessageId: Int = 0

    constructor()

    constructor(source: JSONObject) : this(VKApiMessage(source))

    constructor(message: VKApiMessage) {
        id = message.id
        date = message.date
        peerId = message.peer_id
        fromId = message.from_id
        text = message.text
        randomId = message.random_id
        attachments = message.attachments
        important = message.important
        fwdMessages = message.fwd_messages
        conversationMessageId = message.conversation_message_id
    }
}