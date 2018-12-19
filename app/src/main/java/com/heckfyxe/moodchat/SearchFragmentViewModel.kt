package com.heckfyxe.moodchat

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.heckfyxe.moodchat.model.User
import com.vk.sdk.api.*
import com.vk.sdk.api.model.VKUsersArray

class SearchFragmentViewModel : ViewModel() {

    companion object {
        const val COUNT = 20
    }

    private var offset = 0

    val usersLiveData = MutableLiveData<UsersListResult>()

    fun updateUsers(q: String?) {
        offset = 0
        loadUsers(q).executeWithListener(VKRequestCompletedListener { users ->
            usersLiveData.postValue(UsersListResult(result = users.map { User(it) }))
        })
    }

    fun loadNextUsers(q: String?) {
        loadUsers(q).executeWithListener(VKRequestCompletedListener { users ->
            usersLiveData.postValue(
                UsersListResult(
                    result = users.map { User(it) },
                    isAdvanced = true,
                    data = with(usersLiveData.value!!) {
                        if (isAdvanced) {
                            listOf(*data!!.toTypedArray(), *result!!.toTypedArray())
                        } else {
                            result
                        }
                    })
            )
        })
    }

    private fun loadUsers(q: String?): VKRequest {
        Log.i("offset", offset.toString())
        val params = VKParameters(
            mapOf(
                VKApiConst.OFFSET to offset,
                VKApiConst.COUNT to COUNT,
                VKApiConst.FIELDS to "photo_50, online, photo_100"
            )
        )

        if (q != null && q.isNotEmpty())
            params[VKApiConst.Q] = q

        return VKApi.users().search(params)
    }


    class UsersListResult(
        var error: VKError? = null,
        var result: List<User>? = null,
        var isAdvanced: Boolean = false,
        var data: List<User>? = null  // previously loaded users if it's next users
    )

    private inner class VKRequestCompletedListener(val onCompleted: (VKUsersArray) -> Unit) :
        VKRequest.VKRequestListener() {

        override fun onComplete(response: VKResponse) {
            val users = VKUsersArray().apply {
                parse(response.json)
            }

            offset += COUNT

            onCompleted(users)
        }

        override fun onError(error: VKError) {
            usersLiveData.postValue(UsersListResult(error = error))
        }
    }
}

