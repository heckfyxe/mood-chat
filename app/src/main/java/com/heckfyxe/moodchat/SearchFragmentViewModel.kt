package com.heckfyxe.moodchat

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.vk.sdk.api.*
import com.vk.sdk.api.model.VKApiUserFull
import com.vk.sdk.api.model.VKUsersArray

class SearchFragmentViewModel: ViewModel() {

    companion object {
        const val COUNT = 20
    }

    private var offset = 0

    val usersLiveData = MutableLiveData<UsersListResult>()

    fun updateUsers(q: String?) {
        offset = 0
        loadUsers(q).executeWithListener(VKRequestCompletedListener {
            usersLiveData.postValue(UsersListResult(result = it))
        })
    }

    fun loadNextUsers(q: String?) {
        loadUsers(q).executeWithListener(VKRequestCompletedListener {
            usersLiveData.postValue(UsersListResult(result = it, isAdvanced = true))
        })
    }

    private fun loadUsers(q: String?): VKRequest {
        Log.i("offset", offset.toString())
        return if (q != null && q.isNotEmpty())
            VKApi.users().search(VKParameters(mapOf(
                    VKApiConst.Q to q,
                    VKApiConst.OFFSET to offset,
                    VKApiConst.COUNT to COUNT,
                    VKApiConst.FIELDS to "photo_50")))
        else
            VKApi.users().search(VKParameters(mapOf(
                    VKApiConst.OFFSET to offset,
                    VKApiConst.COUNT to COUNT,
                    VKApiConst.FIELDS to "photo_50")))

    }


    class UsersListResult (
        var error: VKError? = null,
        var result: List<VKApiUserFull>? = null,
        var isAdvanced: Boolean = false
    )

    private inner class VKRequestCompletedListener(val onCompleted: (VKUsersArray) -> Unit):
            VKRequest.VKRequestListener() {

        override fun onComplete(response: VKResponse) {
            val users = VKUsersArray().apply {
                parse(response.json)
            }

            offset += users.size

            onCompleted(users)
        }

        override fun onError(error: VKError) {
            usersLiveData.postValue(UsersListResult(error = error))
        }
    }
}

