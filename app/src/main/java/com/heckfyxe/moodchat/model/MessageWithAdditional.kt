package com.heckfyxe.moodchat.model

import androidx.room.Embedded
import androidx.room.Relation

data class MessageWithAdditional(@Embedded var message: Message) {

    @Relation(parentColumn = "id", entityColumn = "messageId")
    var attachments: List<Attachment>? = message.attachments
}