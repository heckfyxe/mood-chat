package com.heckfyxe.moodchat.model

import androidx.room.ColumnInfo

class PhotoSizes {
    @ColumnInfo(name = "photo_50")
    var photo50: String? = null

    @ColumnInfo(name = "photo_100")
    var photo100: String? = null

    @ColumnInfo(name = "photo_200")
    var photo200: String? = null
}