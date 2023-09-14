package io.agora.chatroom.ui.binder

import androidx.compose.ui.platform.ComposeView
import io.agora.chatroom.ui.theme.ChatroomUIKitTheme

class UIComposeChatBottomBarBinder(
    private val chatBottomBar: ComposeView
) :UIBinder{

    init {
        chatBottomBar.setContent {
            ChatroomUIKitTheme{

            }
        }
    }

    override fun bind() {

    }

    override fun unBind() {

    }


}