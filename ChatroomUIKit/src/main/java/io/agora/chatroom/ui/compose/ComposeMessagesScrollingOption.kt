package io.agora.chatroom.ui.compose

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import io.agora.chatroom.ui.theme.AlphabetHeadlineSmall
import io.agora.chatroom.ui.theme.neutralColor98
import io.agora.chatroom.ui.theme.primaryColor5
import io.agora.chatroom.uikit.R

/**
 * Shows an option when the user scrolls away from the bottom of the list. If there are any new messages it also gives
 * the user information on how many messages they haven't read.
 *
 * @param unreadCount The count of unread messages.
 * @param onClick The handler that's triggered when the user taps on the action.
 */
@OptIn(ExperimentalMaterialApi::class)
@Composable
internal fun ComposeMessagesScrollingOption(
    unreadCount: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .padding(16.dp)
            .wrapContentSize()
    ) {
        Surface(
            onClick = onClick,
            modifier = Modifier
                .padding(top = 12.dp)
                .size(48.dp),
            shape = CircleShape,
            elevation = 4.dp,
            color = neutralColor98
        ) {
            Icon(
                modifier = Modifier.padding(16.dp),
                painter = painterResource(R.drawable.icon_vector),
                contentDescription = null,
                tint = primaryColor5
            )
        }

        if (unreadCount != 0) {
            Surface(
                modifier = Modifier
                    .align(Alignment.TopCenter),
                shape = RoundedCornerShape(16.dp),
                color = primaryColor5
            ) {
                Text(
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                    text = unreadCount.toString(),
                    style = AlphabetHeadlineSmall,
                    color = Color.White
                )
            }
        }
    }
}
