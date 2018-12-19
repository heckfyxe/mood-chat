package com.heckfyxe.moodchat.util

import android.content.Context
import android.net.Uri
import android.provider.MediaStore

fun Uri.getRealPath(context: Context): String? {
    val projection = arrayOf(MediaStore.Images.Media.DATA)
    val cursor = context.contentResolver.query(this, projection, null, null, null)
    val columnIndex = cursor?.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)

    cursor?.moveToFirst()

    return cursor.use {
        it?.getString(columnIndex ?: 0)
    }
}