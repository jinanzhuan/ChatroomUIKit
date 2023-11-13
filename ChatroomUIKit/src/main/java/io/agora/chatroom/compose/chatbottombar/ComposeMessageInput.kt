package io.agora.chatroom.compose.chatbottombar

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import io.agora.chatroom.commons.ComposerInputMessageState
import io.agora.chatroom.viewmodel.messages.MessageChatBarViewModel
import io.agora.chatroom.widget.WidgetInputField

/**
 * Input field for the Messages/Conversation screen. Allows label customization, as well as handlers
 * when the input changes.
 *
 * @param composerMessageState The state of the input.
 * @param onValueChange Handler when the value changes.
 * @param modifier Modifier for styling.
 * @param maxLines The number of lines that are allowed in the input.
 */
@Composable
fun ComposeMessageInput(
    composerMessageState: ComposerInputMessageState,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: MessageChatBarViewModel,
    maxLines: Int = DefaultMessageInputMaxLines,
) {
    val (value) = composerMessageState

    WidgetInputField(
        modifier = modifier,
        maxLines = maxLines,
        onValueChange = onValueChange,
        enabled = true,
        viewModel = viewModel,
    )
}

/**
 * The default number of lines allowed in the input. The message input will become scrollable after
 * this threshold is exceeded.
 */
private const val DefaultMessageInputMaxLines = 6
