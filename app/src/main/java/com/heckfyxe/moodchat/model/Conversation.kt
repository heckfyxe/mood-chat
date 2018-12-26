package com.heckfyxe.moodchat.model

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.Index
import com.vk.sdk.api.model.VKApiChatSettings
import com.vk.sdk.api.model.VKApiConversation
import org.json.JSONObject

@Entity(primaryKeys = ["peerId"], indices = [Index(value = ["peerId"], unique = true)])
class Conversation {

    var peerId: Int = 0

    var localId: Int = 0

    var type: VKApiConversation.Type = VKApiConversation.Type.USER

    var inRead: Int = 0

    var outRead: Int = 0

    var unreadCount: Int = 0

    var canWrite: Boolean = false

    var lastMessageId: Int = 0

    @Embedded
    var chatSettings: ChatSettings? = null

    constructor()

    constructor(source: JSONObject) : this(VKApiConversation(source))

    constructor(conversation: VKApiConversation) {
        peerId = conversation.peer_id
        localId = conversation.local_id
        type = conversation.type
        inRead = conversation.in_read
        outRead = conversation.out_read
        unreadCount = conversation.unread_count
        canWrite = conversation.can_write
        lastMessageId = conversation.last_message_id
        if (conversation.chat_settings != null)
            chatSettings = ChatSettings(conversation.chat_settings)
    }

    class ChatSettings {
        var title: String = ""

        var membersCount = 0

        var state: VKApiChatSettings.State = VKApiChatSettings.State.IN

        @Embedded
        var photo: PhotoSizes? = null

        @Ignore
        var activeIds = IntArray(4)

        var isGroupChannel = false

        @Ignore
        var pinnedMessage: Message? = null

        constructor()

        constructor(source: JSONObject) : this(VKApiChatSettings(source))

        constructor(chatSettings: VKApiChatSettings) {
            title = chatSettings.title
            membersCount = chatSettings.members_count
            state = chatSettings.state
            if (chatSettings.photo != null) {
                photo = PhotoSizes().apply {
                    chatSettings.photo.let {
                        photo50 = it.photo_50
                        photo100 = it.photo_100
                        photo200 = it.photo_200
                    }
                }
            }
            activeIds = chatSettings.active_ids
            isGroupChannel = chatSettings.is_group_channel
            if (chatSettings.pinned_message != null)
                pinnedMessage = Message(chatSettings.pinned_message)
        }
    }
}