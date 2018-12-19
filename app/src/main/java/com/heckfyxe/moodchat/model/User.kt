package com.heckfyxe.moodchat.model

import androidx.room.Entity
import androidx.room.Index
import com.vk.sdk.api.model.VKApiUserFull
import org.json.JSONObject

@Entity(primaryKeys = ["id"], indices = [Index(value = ["id"], unique = true)])
class User {

    var id: Int = 0

    var firstName: String = ""

    var lastName: String = ""

    var online: Boolean = false

    var onlineMobile: Boolean = false

    var photo50: String? = null

    var photo100: String? = null

    var photo200: String? = null

    constructor()

    constructor(source: JSONObject) : this(VKApiUserFull(source))

    constructor(user: VKApiUserFull) {
        id = user.id
        firstName = user.first_name
        lastName = user.last_name
        online = user.online
        onlineMobile = user.online_mobile
        photo50 = user.photo_50
        photo100 = user.photo_100
        photo200 = user.photo_200
    }

    override fun toString(): String = "$lastName $firstName"
}