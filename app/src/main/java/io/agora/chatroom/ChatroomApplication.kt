package io.agora.chatroom

import android.app.Application
import io.agora.chatroom.model.UICommonConfig

class ChatroomApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        ChatroomUIKitClient.getInstance().setUp(this,BuildConfig.CHATROOM_APP_KEY)
    }
}