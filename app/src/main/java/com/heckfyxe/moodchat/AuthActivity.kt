package com.heckfyxe.moodchat

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.vk.sdk.VKAccessToken
import com.vk.sdk.VKCallback
import com.vk.sdk.VKSdk
import com.vk.sdk.api.VKError

class AuthActivity : AppCompatActivity(){

    companion object {
        const val RC_MAIN_ACTIVITY = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        VKSdk.wakeUpSession(this, object : VKCallback<VKSdk.LoginState> {
            override fun onResult(res: VKSdk.LoginState) {
                if (res == VKSdk.LoginState.LoggedIn) {
                    launchMainActivity()
                } else if (res == VKSdk.LoginState.LoggedOut) {
                    VKSdk.login(this@AuthActivity)
                }
            }

            override fun onError(error: VKError?) {
                Log.e("AuthActivity", "auth error: ${error?.errorReason}")
                Toast.makeText(this@AuthActivity, R.string.auth_error, Toast.LENGTH_SHORT).show()
                VKSdk.login(this@AuthActivity)
            }
        })
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (!VKSdk.onActivityResult(requestCode, resultCode, data, object : VKCallback<VKAccessToken> {
                            override fun onResult(res: VKAccessToken) {
                                launchMainActivity()
                            }
                            override fun onError(error: VKError) {
                                finish()
                            }
                })) {

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
        startActivity(intent)
    }
}
