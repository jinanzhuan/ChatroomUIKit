package io.agora.chatroom

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.ui.Modifier
import io.agora.chatroom.data.emojiMap
import io.agora.chatroom.theme.ChatroomUIKitTheme
import io.agora.chatroom.widget.RichEditText
import io.agora.chatroom.widget.SpecialCharEditText
import test

class TestActivity:ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ChatroomUIKitTheme {
                SpecialCharEditText(
                    modifier = Modifier.fillMaxWidth().wrapContentHeight()
                )

            }
        }
    }
}