package com.heckfyxe.moodchat.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Ignore
import androidx.room.Index
import com.vk.sdk.api.model.VKApiPhoto
import org.json.JSONObject

@Entity(
        primaryKeys = ["id"],
        indices = [
            Index("id", unique = true)
        ],
        foreignKeys = [
            ForeignKey(
                    entity = Message::class,
                    parentColumns = ["id"],
                    childColumns = ["messageId"],
                    onDelete = ForeignKey.CASCADE)
        ])
data class Photo(
        @Ignore override var id: Int = 0,
        var albumId: Int = 0,
        var ownerId: Int = 0,
        var userId: Int = 0,
        var width: Int = 0,
        var height: Int = 0,
        var text: String = "",
        var date: Long = 0,
        var photo75: String? = null,
        var photo130: String? = null,
        var photo604: String? = null,
        var photo807: String? = null,
        var photo1280: String? = null,
        var photo2560: String? = null,
        var likedByMe: Boolean = false,
        var canComment: Boolean = false,
        var likes: Int = 0,
        var comments: Int = 0,
        var tags: Int = 0,
        @Ignore override var messageId: Int = 0,
        @Ignore override var accessKey: String? = null) : Attachment(id, messageId, accessKey) {

    companion object {
        @JvmStatic
        fun create(source: JSONObject) =
                create(VKApiPhoto(source))

        @JvmStatic
        fun create(photo: VKApiPhoto): Photo = with(photo) {
            Photo(
                    id = id,
                    albumId = album_id,
                    ownerId = owner_id,
                    width = width,
                    height = height,
                    text = text,
                    date = date,
                    photo75 = photo_75,
                    photo130 = photo_130,
                    photo604 = photo_604,
                    photo807 = photo_807,
                    photo1280 = photo_1280,
                    photo2560 = photo_2560,
                    likedByMe = user_likes,
                    canComment = can_comment,
                    likes = likes,
                    comments = comments,
                    tags = tags,
                    accessKey = access_key
            )
        }
    }
}