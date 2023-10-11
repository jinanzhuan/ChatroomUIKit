package io.agora.chatroom.theme

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

class UIDimens(
    public val messageItemMaxWidth: Dp,
) {
    public companion object {
        public fun defaultDimens(): UIDimens = UIDimens(
            messageItemMaxWidth = 250.dp,
        )
    }
}