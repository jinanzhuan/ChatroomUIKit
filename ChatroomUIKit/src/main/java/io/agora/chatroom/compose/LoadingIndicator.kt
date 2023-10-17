package io.agora.chatroom.compose

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.agora.chatroom.theme.ChatroomUIKitTheme

/**
 * Shows the default loading UI.
 *
 * @param modifier Modifier for styling.
 */
@Composable
public fun LoadingIndicator(modifier: Modifier = Modifier) {
    Box(
        modifier,
        contentAlignment = Alignment.Center,
    ) {
        CircularProgressIndicator(strokeWidth = 2.dp, color = ChatroomUIKitTheme.colors.primary)
    }
}
