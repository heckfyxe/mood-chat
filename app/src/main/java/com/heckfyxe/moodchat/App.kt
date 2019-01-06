package com.heckfyxe.moodchat

import android.content.Intent
import android.widget.Toast
import androidx.multidex.MultiDexApplication
import com.crashlytics.android.Crashlytics
import com.vk.sdk.VKAccessToken
import com.vk.sdk.VKAccessTokenTracker
import com.vk.sdk.VKSdk
import io.fabric.sdk.android.Fabric
import org.koin.android.ext.android.startKoin




class App : MultiDexApplication() {

    private val vkAccessTokenTracker: VKAccessTokenTracker = object : VKAccessTokenTracker() {
        override fun onVKAccessTokenChanged(oldToken: VKAccessToken?, newToken: VKAccessToken?) {
            if (newToken == null) {
                Toast.makeText(this@App, "AccessToken invalidated", Toast.LENGTH_LONG)
                    .show()
                val intent = Intent(this@App, AuthActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                startActivity(intent)
            }
        }
    }

    override fun onCreate() {
        super.onCreate()

        Fabric.with(this, Crashlytics())

        startKoin(this, listOf(koinModule))

        vkAccessTokenTracker.startTracking()

        VKSdk.initialize(applicationContext)
    }
}