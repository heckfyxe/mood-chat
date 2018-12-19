package com.heckfyxe.moodchat.model

import androidx.room.Entity
import androidx.room.Index
import com.vk.sdk.api.model.VKApiCommunityFull
import org.json.JSONObject

@Entity(indices = [Index(value = ["id"], unique = true)], primaryKeys = ["id"])
class Group {

    var id: Int = 0

    var name: String = ""

    var photo50: String? = null

    var photo100: String? = null

    var photo200: String? = null

    constructor()

    constructor(source: JSONObject) : this(VKApiCommunityFull().parse(source))

    constructor(group: VKApiCommunityFull) {
        id = group.id
        name = group.name
        photo50 = group.photo_50
        photo100 = group.photo_100
        photo200 = group.photo_200
    }
}