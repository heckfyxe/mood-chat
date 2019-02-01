package com.heckfyxe.moodchat.model

import androidx.room.Entity
import androidx.room.Index
import com.vk.sdk.api.model.VKApiCommunityFull
import org.json.JSONObject

@Entity(indices = [Index(value = ["id"], unique = true)], primaryKeys = ["id"])
data class Group(
        var id: Int = 0,

        var name: String = "",

        var photo50: String? = null,

        var photo100: String? = null,

        var photo200: String? = null) {

    companion object {
        @JvmStatic
        fun create(source: JSONObject) =
                create(VKApiCommunityFull().apply {
                    id = source.optInt("id")
                    name = source.optString("name")
                    photo_50 = source.optString("photo_50")
                    photo_100 = source.optString("photo_100")
                    photo_200 = source.optString("photo_200")
                })

        @JvmStatic
        fun create(group: VKApiCommunityFull) =
                Group(
                        id = group.id,
                        name = group.name,
                        photo50 = group.photo_50,
                        photo100 = group.photo_100,
                        photo200 = group.photo_200
                )
    }
}