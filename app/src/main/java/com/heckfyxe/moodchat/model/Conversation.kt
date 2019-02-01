package com.heckfyxe.moodchat.model

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.Index
import com.vk.sdk.api.model.VKApiChatSettings
import com.vk.sdk.api.model.VKApiConversation
import org.json.JSONObject

@Entity(primaryKeys = ["peerId"], indices = [Index(value = ["peerId"], unique = true)])
data class Conversation(

        var peerId: Int = 0,

        var localId: Int = 0,

        var type: VKApiConversation.Type = VKApiConversation.Type.USER,

        var inRead: Int = 0,

        var outRead: Int = 0,

        var unreadCount: Int = 0,

        var canWrite: Boolean = false,

        var lastMessageId: Int = 0,

        @Embedded
        var chatSettings: ChatSettings? = null) {

    companion object {
        @JvmStatic
        fun create(source: JSONObject) =
                create(VKApiConversation(source))

        @JvmStatic
        fun create(conversation: VKApiConversation) =
                Conversation(
                        peerId = conversation.peer_id,
                        localId = conversation.local_id,
                        type = conversation.type,
                        inRead = conversation.in_read,
                        outRead = conversation.out_read,
                        unreadCount = conversation.unread_count,
                        canWrite = conversation.can_write,
                        lastMessageId = conversation.last_message_id,
                        chatSettings = if (conversation.chat_settings != null)
                            ChatSettings.create(conversation.chat_settings)
                        else null)
    }


    data class ChatSettings(

            var title: String = "",

            var membersCount: Int = 0,

            var state: VKApiChatSettings.State = VKApiChatSettings.State.IN,

            @Embedded
            var photo: PhotoSizes? = null,

            @Ignore
            var activeIds: IntArray = IntArray(4),

            var isGroupChannel: Boolean = false,

            @Ignore
            var pinnedMessage: Message? = null) {

        companion object {
            @JvmStatic
            fun create(source: JSONObject) =
                    create(VKApiChatSettings(source))

            @JvmStatic
            fun create(chatSettings: VKApiChatSettings) =
                    ChatSettings(
                            title = chatSettings.title,
                            membersCount = chatSettings.members_count,
                            state = chatSettings.state,
                            activeIds = chatSettings.active_ids,
                            isGroupChannel = chatSettings.is_group_channel,
                            pinnedMessage = if (chatSettings.pinned_message != null)
                                Message.create(chatSettings.pinned_message)
                            else null,
                            photo = if (chatSettings.photo != null)
                                with(chatSettings.photo) {
                                    PhotoSizes(photo_50, photo_100, photo_200)
                                }
                            else null)
        }
    }
}