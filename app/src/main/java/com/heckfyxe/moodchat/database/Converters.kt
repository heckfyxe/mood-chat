package com.heckfyxe.moodchat.database

import androidx.room.TypeConverter
import com.vk.sdk.api.model.VKApiChatSettings
import com.vk.sdk.api.model.VKApiConversation
import com.vk.sdk.api.model.VKApiConversation.Type.*

class Converters {

    @TypeConverter
    fun fromType(type: VKApiConversation.Type): String {
        return when (type) {
            USER -> "user"
            GROUP -> "group"
            EMAIL -> "email"
            CHAT -> "chat"
        }
    }

    @TypeConverter
    fun toType(type: String): VKApiConversation.Type {
        return when (type) {
            "user" -> USER
            "group" -> GROUP
            "email" -> EMAIL
            "chat" -> CHAT
            else -> throw Exception("VKApiConversation doesn't have $type type!")
        }
    }

    @TypeConverter
    fun fromState(state: VKApiChatSettings.State): String {
        return when (state) {
            VKApiChatSettings.State.IN -> "in"
            VKApiChatSettings.State.KICKED -> "kicked"
            VKApiChatSettings.State.LEFT -> "left"
        }
    }

    @TypeConverter
    fun toState(state: String): VKApiChatSettings.State {
        return when (state) {
            "in" -> VKApiChatSettings.State.IN
            "kicked" -> VKApiChatSettings.State.KICKED
            "left" -> VKApiChatSettings.State.LEFT
            else -> throw Exception("VKApiChatSettings.State enumeration doesn't have $state")
        }
    }
}