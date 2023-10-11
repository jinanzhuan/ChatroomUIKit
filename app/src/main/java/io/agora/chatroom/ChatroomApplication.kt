package io.agora.chatroom

import android.app.Application

class ChatroomApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        ChatroomUIKitClient.shared.setUp(this,BuildConfig.CHATROOM_APP_KEY)
    }
}