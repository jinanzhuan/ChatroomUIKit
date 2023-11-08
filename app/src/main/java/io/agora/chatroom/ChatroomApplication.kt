package io.agora.chatroom

import android.app.Application
import io.agora.chatroom.commons.GlobalConfig
import io.agora.chatroom.model.UICommonConfig

class ChatroomApplication : Application() {

    private val activityLifecycleCallbacks by lazy { UserActivityLifecycleCallbacks() }

    override fun onCreate() {
        super.onCreate()

        val uiCommonConfig = UICommonConfig(
            languageList = listOf(GlobalConfig.targetLanguage.code),
            isOpenAutoClearGiftList = true,
            autoClearTime = 3000L,
        )

        ChatroomUIKitClient.getInstance().setUp(
            applicationContext = this,
            config = uiCommonConfig,
            appKey = BuildConfig.CHATROOM_APP_KEY
        )

        ChatroomUIKitClient.getInstance().getContext().setUseGiftsInList(false)


        registerActivityLifecycleCallbacks(activityLifecycleCallbacks)
    }

    fun getUserActivityLifecycleCallbacks(): UserActivityLifecycleCallbacks {
        return activityLifecycleCallbacks
    }

}