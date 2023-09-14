package io.agora.chatroom.ui.compose.chatbottombar

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.SnackbarHost
import androidx.compose.material.SnackbarHostState
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment.Companion.Bottom
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import io.agora.chatroom.model.UIMessage
import io.agora.chatroom.ui.commons.MessageComposerState
import io.agora.chatroom.ui.commons.UIValidationError
import io.agora.chatroom.ui.compose.MessageComposeInput
import io.agora.chatroom.ui.compose.utils.AboveAnchorPopupPositionProvider
import io.agora.chatroom.ui.compose.utils.mirrorRtl
import io.agora.chatroom.ui.model.UICapabilities
import io.agora.chatroom.ui.theme.AlphabetBodyLarge
import io.agora.chatroom.ui.theme.neutralColor1
import io.agora.chatroom.ui.theme.neutralColor8
import io.agora.chatroom.ui.theme.neutralColor98
import io.agora.chatroom.ui.theme.primaryColor5
import io.agora.chatroom.ui.viewmodel.messages.MessageComposerViewModel
import io.agora.chatroom.uikit.R

/**
 * Default ComposeChatBottomBar component that relies on [MessageComposerViewModel] to handle data and
 * communicate various events.
 *
 * @param viewModel The ViewModel that provides pieces of data to show in the composer, like the
 * currently selected integration data or the user input. It also handles sending messages.
 * @param modifier Modifier for styling.
 * @param onSendMessage Handler when the user sends a message. By default it delegates this to the
 * ViewModel, but the user can override if they want more custom behavior.
 * @param onValueChange Handler when the input field value changes.
 * their own integrations, which they need to hook up to their own data providers and UI.
 * @param label Customizable composable that represents the input field label (hint).
 * @param input Customizable composable that represents the input field for the composer, [MessageInput] by default.
 * by default.
 */
@Composable
public fun ComposeChatBottomBar(
    isDarkTheme:Boolean = false,
    viewModel: MessageComposerViewModel,
    modifier: Modifier = Modifier,
    onSendMessage: (UIMessage) -> Unit = { viewModel.sendMessage(it) },
    onValueChange: (String) -> Unit = { viewModel.setMessageInput(it) },
    label: @Composable (MessageComposerState) -> Unit = { DefaultComposerLabel(isDarkTheme = isDarkTheme,it.ownCapabilities) },
    input: @Composable RowScope.(MessageComposerState) -> Unit = {
        @Suppress("DEPRECATION_ERROR")
        DefaultComposerInputContent(
            isDarkTheme = isDarkTheme,
            messageComposerState = it,
            onValueChange = onValueChange,
            label = label,
        )
    },
    trailingContent: @Composable (MessageComposerState) -> Unit = {
        DefaultMessageComposerTrailingContent(
            isDarkTheme = isDarkTheme,
            value = it.inputValue,
            UIValidationErrors = it.UIValidationErrors,
            ownCapabilities = it.ownCapabilities,
            onSendMessage = { input ->
                val message = viewModel.buildNewMessage(input)

                onSendMessage(message)
            }
        )
    },
    voiceContent: @Composable (MessageComposerState) -> Unit = {
        DefaultMessageComposerVoiceContent(
            isDarkTheme = isDarkTheme,
            ownCapabilities = it.ownCapabilities,
            onVoiceClick = {}
        )
    },
    emojiContent: @Composable (MessageComposerState) -> Unit = {
        DefaultMessageComposerEmojiContent(
            isDarkTheme = isDarkTheme,
            ownCapabilities = it.ownCapabilities,
            onEmojiClick = {}
        )
    }
) {
    val messageComposerState by viewModel.messageComposerState.collectAsState()

    ComposeChatBottomBar(
        isDarkTheme = isDarkTheme,
        modifier = modifier,
        onSendMessage = { text ->
            val messageWithData = viewModel.buildNewMessage(text)
            onSendMessage(messageWithData)
        },
        input = input,
        voiceContent = voiceContent,
        emojiContent = emojiContent,
        trailingContent = trailingContent,
        messageComposerState = messageComposerState,
    )
}

/**
 * Clean version of the [ComposeChatBottomBar] that doesn't rely on ViewModels, so the user can provide a
 * manual way to handle and represent data and various operations.
 *
 * @param messageComposerState The state of the message input.
 * @param onSendMessage Handler when the user wants to send a message.
 * @param modifier Modifier for styling.
 * @param onValueChange Handler when the input field value changes.
 * their own integrations, which they need to hook up to their own data providers and UI.
 * @param label Customizable composable that represents the input field label (hint).
 * @param input Customizable composable that represents the input field for the composer, [MessageInput] by default.
 * by default.
 */
@Composable
public fun ComposeChatBottomBar(
    isDarkTheme: Boolean = false,
    messageComposerState: MessageComposerState,
    onSendMessage: (String) -> Unit,
    modifier: Modifier = Modifier,
    onValueChange: (String) -> Unit = {},
    label: @Composable (MessageComposerState) -> Unit = { DefaultComposerLabel(isDarkTheme,messageComposerState.ownCapabilities) },
    input: @Composable RowScope.(MessageComposerState) -> Unit = {
        @Suppress("DEPRECATION_ERROR")
        DefaultComposerInputContent(
            isDarkTheme = isDarkTheme,
            messageComposerState = messageComposerState,
            onValueChange = onValueChange,
            label = label,
        )
    },
    trailingContent: @Composable (MessageComposerState) -> Unit = {
        DefaultMessageComposerTrailingContent(
            isDarkTheme = isDarkTheme,
            value = it.inputValue,
            UIValidationErrors = it.UIValidationErrors,
            onSendMessage = onSendMessage,
            ownCapabilities = messageComposerState.ownCapabilities,
        )
    },
    voiceContent: @Composable (MessageComposerState) -> Unit = {
        DefaultMessageComposerVoiceContent(
            isDarkTheme = isDarkTheme,
            ownCapabilities = it.ownCapabilities,
            onVoiceClick = {}
        )
    },
    emojiContent: @Composable (MessageComposerState) -> Unit = {
        DefaultMessageComposerEmojiContent(
            isDarkTheme = isDarkTheme,
            ownCapabilities = it.ownCapabilities,
            onEmojiClick = {}
        )
    }
) {
    val (_,_,validationErrors) = messageComposerState
    val snackbarHostState = remember { SnackbarHostState() }

    MessageInputValidationError(
        UIValidationErrors = validationErrors,
        snackbarHostState = snackbarHostState
    )

    Surface(
        modifier = modifier,
        elevation = 4.dp,
        color = neutralColor8,
    ) {
        Column(Modifier
            .height(52.dp)
            .background(if (isDarkTheme) neutralColor1 else neutralColor98)
        ) {

            Row(
                Modifier.fillMaxWidth(),
                verticalAlignment = Bottom
            ) {

                voiceContent(messageComposerState)

                input(messageComposerState)

                emojiContent(messageComposerState)

                trailingContent(messageComposerState)
            }

        }

        if (snackbarHostState.currentSnackbarData != null) {
            SnackbarPopup(snackbarHostState = snackbarHostState)
        }

    }
}


/**
 * Default input field label that the user can override in [ComposeChatBottomBar].
 *
 * @param ownCapabilities Set of capabilities the user is given for the current channel.
 */
@Composable
internal fun DefaultComposerLabel(
    isDarkTheme: Boolean,
    ownCapabilities: Set<String>)
{
    Text(
        text = stringResource(id = R.string.stream_compose_message_label),
        style = AlphabetBodyLarge,
        color = if (isDarkTheme) neutralColor98 else neutralColor1
    )
}

/**
 * Represents the default input content of the Composer.
 *
 * @param label Customizable composable that represents the input field label (hint).
 * @param messageComposerState The state of the message input.
 * @param onValueChange Handler when the input field value changes.
 */
@Deprecated(
    message = "To be marked as an internal component. Use MessageInput directly.",
    replaceWith = ReplaceWith(
        expression = "MessageInput(" +
            "    messageComposerState: MessageComposerState," +
            "    onValueChange: (String) -> Unit," +
            "    modifier: Modifier = Modifier," +
            "    maxLines: Int = DefaultMessageInputMaxLines," +
            "    keyboardCapitalization: KeyboardCapitalization," +
            "    label: @Composable (MessageComposerState) -> Unit," +
            "    innerLeadingContent: @Composable RowScope.() -> Unit," +
            "    innerTrailingContent: @Composable RowScope.() -> Unit" +
            ")",
        imports = arrayOf("io.getstream.chat.android.compose.ui.components.composer.MessageInput")
    ),
    level = DeprecationLevel.ERROR,
)
@Composable
public fun RowScope.DefaultComposerInputContent(
    isDarkTheme: Boolean = false,
    messageComposerState: MessageComposerState,
    onValueChange: (String) -> Unit,
    label: @Composable (MessageComposerState) -> Unit,
) {
    MessageComposeInput(
        isDarkTheme = isDarkTheme,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp, bottom = 8.dp, start = 12.dp)
            .weight(1f),
        label = label,
        messageComposerState = messageComposerState,
        onValueChange = onValueChange,
    )
}

/**
 * Represents the default trailing content for the Composer, which represent a send button or a cooldown timer.
 *
 * @param value The input value.
 * @param UIValidationErrors List of errors for message validation.
 * @param onSendMessage Handler when the user wants to send a message.
 * @param ownCapabilities Set of capabilities the user is given for the current channel.
 */
@Composable
internal fun DefaultMessageComposerTrailingContent(
    isDarkTheme: Boolean = false,
    value: String,
    UIValidationErrors: List<UIValidationError>,
    ownCapabilities: Set<String>,
    onSendMessage: (String) -> Unit,
) {
    val isSendButtonEnabled = ownCapabilities.contains(UICapabilities.SEND_MESSAGE)
    val isInputValid by lazy { (value.isNotBlank()) && UIValidationErrors.isEmpty() }
    val description = stringResource(id = R.string.stream_compose_cd_send_button)

    IconButton(
        modifier = Modifier.semantics { contentDescription = description },
        enabled = isSendButtonEnabled && isInputValid,
        content = {
            val layoutDirection = LocalLayoutDirection.current

            Icon(
                modifier = Modifier.mirrorRtl(layoutDirection = layoutDirection),
                painter = painterResource(id = R.drawable.icon_send),
                contentDescription = stringResource(id = R.string.stream_compose_send_message),
                tint = if (isInputValid) primaryColor5 else primaryColor5
            )
        },
        onClick = {
            if (isInputValid) {
                onSendMessage(value)
            }
        }
    )
}

@Composable
internal fun DefaultMessageComposerVoiceContent(
    isDarkTheme: Boolean = false,
    ownCapabilities: Set<String>,
    onVoiceClick: () -> Unit,
) {
    val isVoiceButtonEnabled = ownCapabilities.contains(UICapabilities.SHOW_VOICE)
    val description = stringResource(id = R.string.stream_compose_cd_voice_button)

    if (isVoiceButtonEnabled){
        IconButton(
            modifier = Modifier.semantics { contentDescription = description },
            content = {
                val layoutDirection = LocalLayoutDirection.current
                Icon(
                    modifier = Modifier.mirrorRtl(layoutDirection = layoutDirection).size(30.dp,30.dp),
                    painter = painterResource(id = R.drawable.icon_wave_in_circle),
                    contentDescription = stringResource(id = R.string.stream_compose_send_message),
                    tint = if (isDarkTheme) neutralColor98 else neutralColor1
                )
            },
            onClick = {
                onVoiceClick()
            }
        )
    }
}

@Composable
internal fun DefaultMessageComposerEmojiContent(
    isDarkTheme: Boolean = false,
    ownCapabilities: Set<String>,
    onEmojiClick: (isShowFace:Boolean) -> Unit,
) {
    val resourceId = remember { mutableStateOf(R.drawable.icon_face) }
    val resource by resourceId

    var isShowFace = true
    val description = stringResource(id = R.string.stream_compose_cd_emoji_button)

    IconButton(
        modifier = Modifier.semantics { contentDescription = description },
        content = {
            val layoutDirection = LocalLayoutDirection.current
            Icon(
                modifier = Modifier.mirrorRtl(layoutDirection = layoutDirection).size(30.dp,30.dp),
                painter = painterResource(id = resource),
                contentDescription = stringResource(id = R.string.stream_compose_send_message),
                tint = if (isDarkTheme) neutralColor98 else neutralColor1
            )
            LaunchedEffect(resource) {
                Log.e("apex","LaunchedEffect:  ${resource}")
            }
        },
        onClick = {
            isShowFace = !isShowFace
            resourceId.value = if (isShowFace){
                R.drawable.icon_face
            }else{
                R.drawable.icon_keyboard
            }
            onEmojiClick(isShowFace)
        }

    )
}

/**
 * Shows a [Toast] with an error if one of the following constraints are violated:
 *
 * - The message length exceeds the maximum allowed message length.
 * - The number of selected attachments is too big.
 * - At least one of the attachments is too big.
 *
 * @param UIValidationErrors The list of validation errors for the current user input.
 */
@Composable
private fun MessageInputValidationError(UIValidationErrors: List<UIValidationError>, snackbarHostState: SnackbarHostState) {
    if (UIValidationErrors.isNotEmpty()) {
        val firstValidationError = UIValidationErrors.first()

        val errorMessage = when (firstValidationError) {
            is UIValidationError.MessageLengthExceeded -> {
                stringResource(
                    R.string.stream_compose_message_composer_error_message_length,
                    firstValidationError.maxMessageLength
                )
            }

        }

        val context = LocalContext.current
        LaunchedEffect(UIValidationErrors.size) {
            Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
        }
    }
}

/**
 * A snackbar wrapped inside of a popup allowing it be
 * displayed above the Composable it's anchored to.
 *
 * @param snackbarHostState The state of the snackbar host. Contains
 * the snackbar data necessary to display the snackbar.
 */
@Composable
private fun SnackbarPopup(snackbarHostState: SnackbarHostState) {
    Popup(popupPositionProvider = AboveAnchorPopupPositionProvider()) {
        SnackbarHost(hostState = snackbarHostState)
    }
}
