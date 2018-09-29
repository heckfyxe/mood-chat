package com.heckfyxe.moodchat

import androidx.multidex.MultiDexApplication
import com.vk.sdk.VKAccessToken
import com.vk.sdk.VKAccessTokenTracker
import com.vk.sdk.VKSdk

class App: MultiDexApplication() {

    private val vkAccessTokenTracker: VKAccessTokenTracker = object : VKAccessTokenTracker() {
        override fun onVKAccessTokenChanged(oldToken: VKAccessToken?, newToken: VKAccessToken?) {
            if (newToken == null) {

            }
        }
    }

    override fun onCreate() {
        super.onCreate()

        vkAccessTokenTracker.startTracking()

        VKSdk.initialize(applicationContext)
    }

    override fun onTerminate() {
        super.onTerminate()

        vkAccessTokenTracker.stopTracking()
    }
}