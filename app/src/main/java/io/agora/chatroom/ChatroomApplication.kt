package io.agora.chatroom

import android.app.Application
import io.agora.chatroom.commons.GlobalConfig

class ChatroomApplication : Application() {

    private val activityLifecycleCallbacks by lazy { UserActivityLifecycleCallbacks() }

    override fun onCreate() {
        super.onCreate()

        val chatroomUIKitOptions = ChatroomUIKitOptions(
            chatOptions = ChatSDKOptions(enableDebug = true),
            uiOptions = UiOptions(
                targetLanguageList = listOf(GlobalConfig.targetLanguage.code),
                useGiftsInList = false,
            )
        )

        ChatroomUIKitClient.getInstance().setUp(
            applicationContext = this,
            options = chatroomUIKitOptions,
            appKey = BuildConfig.CHATROOM_APP_KEY
        )

        registerActivityLifecycleCallbacks(activityLifecycleCallbacks)
    }

    fun getUserActivityLifecycleCallbacks(): UserActivityLifecycleCallbacks {
        return activityLifecycleCallbacks
    }

}