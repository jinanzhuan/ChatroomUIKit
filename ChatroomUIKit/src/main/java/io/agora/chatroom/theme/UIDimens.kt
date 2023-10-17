package io.agora.chatroom.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Immutable
class UIDimens(
    val messageItemMaxWidth: Dp,
) {
    companion object {
        @Composable
        fun defaultDimens(): UIDimens = UIDimens(
            messageItemMaxWidth = 250.dp,
        )
    }
}