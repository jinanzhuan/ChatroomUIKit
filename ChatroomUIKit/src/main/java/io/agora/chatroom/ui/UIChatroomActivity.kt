package io.agora.chatroom.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import io.agora.chatroom.ui.compose.chatbottombar.MessagesScreen
import io.agora.chatroom.ui.theme.ChatroomUIKitTheme

class UIChatroomActivity : ComponentActivity(){


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val roomId = intent.getStringExtra(KEY_ROOM_ID) ?: return

        setContent {
            ChatroomUIKitTheme {
                MessagesScreen(isDarkTheme = isSystemInDarkTheme(), roomId = "123")
            }
        }
    }


    companion object {
        private const val KEY_ROOM_ID = "roomId"

        fun createIntent(
            context: Context,
            roomId: String,
        ): Intent {
            return Intent(context, UIChatroomActivity::class.java).apply {
                putExtra(KEY_ROOM_ID, roomId)
            }
        }
    }
}