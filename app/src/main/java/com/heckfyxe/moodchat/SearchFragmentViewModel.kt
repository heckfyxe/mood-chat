package com.heckfyxe.moodchat

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.vk.sdk.api.*
import com.vk.sdk.api.model.VKApiUserFull
import com.vk.sdk.api.model.VKUsersArray

class SearchFragmentViewModel: ViewModel() {

    val usersLiveData = MutableLiveData<UsersListResult>()

    private val vkRequestListener by lazy { object : VKRequest.VKRequestListener() {

        override fun onComplete(response: VKResponse) {
            val users = VKUsersArray().apply {
                parse(response.json)
            }

            usersLiveData.postValue(UsersListResult(result = users))
        }

        override fun onError(error: VKError) {
            usersLiveData.postValue(UsersListResult(error = error))
        }
    }}

    fun updateUsers(q: String?) {
        if (q != null && q.isNotEmpty()) {
            VKApi.users().search(VKParameters(mapOf(
                    VKApiConst.Q to q,
                    VKApiConst.FIELDS to "photo_50")))
        } else {
            VKApi.users().search(VKParameters(mapOf(
                    VKApiConst.FIELDS to "photo_50"
            )))
        }.executeWithListener(vkRequestListener)
    }

    class UsersListResult (
        var error: VKError? = null,
        var result: List<VKApiUserFull>? = null
    )
}