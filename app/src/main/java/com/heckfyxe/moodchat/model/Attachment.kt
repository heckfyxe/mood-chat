package com.heckfyxe.moodchat.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
open class Attachment(
        @PrimaryKey open var id: Int = 0,
        open var messageId: Int = 0,
        open var accessKey: String? = null)