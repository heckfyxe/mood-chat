package com.heckfyxe.moodchat

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.heckfyxe.moodchat.util.show
import com.vk.sdk.VKAccessToken
import com.vk.sdk.VKCallback
import com.vk.sdk.VKSdk
import com.vk.sdk.api.VKError
import com.vk.sdk.api.model.VKScopes
import kotlinx.android.synthetic.main.activity_auth.*

class AuthActivity : AppCompatActivity() {

    companion object {
        const val RC_MAIN_ACTIVITY = 1

        private val SCOPES = arrayOf(VKScopes.MESSAGES, VKScopes.GROUPS)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)

        VKSdk.wakeUpSession(this, object : VKCallback<VKSdk.LoginState> {
            override fun onResult(res: VKSdk.LoginState) {
                when (res) {
                    VKSdk.LoginState.LoggedIn ->
                        launchMainActivity()
                    VKSdk.LoginState.LoggedOut ->
                        VKSdk.login(this@AuthActivity, *SCOPES)
                    VKSdk.LoginState.Unknown ->
                        VKSdk.login(this@AuthActivity, *SCOPES)
                    VKSdk.LoginState.Pending -> {
                        noNetworkGroup?.show()
                    }
                }
            }

            override fun onError(error: VKError?) {
                Log.e("AuthActivity", "auth error: ${error?.errorReason}")
                Toast.makeText(this@AuthActivity, R.string.auth_error, Toast.LENGTH_SHORT).show()
                VKSdk.login(this@AuthActivity, *SCOPES)
            }
        })
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (!VKSdk.onActivityResult(
                requestCode,
                resultCode,
                data,
                object : VKCallback<VKAccessToken> {
                    override fun onResult(res: VKAccessToken) {
                        launchMainActivity()
                    }

                    override fun onError(error: VKError) {
                        finish()
                    }
                })
        ) {

            when (requestCode) {
                RC_MAIN_ACTIVITY -> {
                    if (resultCode == AppCompatActivity.RESULT_CANCELED)
                        finish()
                    else
                        recreate()
                }
                else -> super.onActivityResult(requestCode, resultCode, data)
            }
        }


    }

    private fun launchMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivityForResult(intent, RC_MAIN_ACTIVITY)
    }
}
