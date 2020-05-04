package ru.adonixis.vkcupunsub

import android.app.Application
import com.vk.api.sdk.VK
import com.vk.api.sdk.VKTokenExpiredHandler
import ru.adonixis.vkcupunsub.activity.WelcomeActivity

class VKCupUnsubApp: Application() {
    override fun onCreate() {
        super.onCreate()
        VK.addTokenExpiredHandler(tokenTracker)
    }

    private val tokenTracker = object: VKTokenExpiredHandler {
        override fun onTokenExpired() {
            WelcomeActivity.startFrom(this@VKCupUnsubApp)
        }
    }
}