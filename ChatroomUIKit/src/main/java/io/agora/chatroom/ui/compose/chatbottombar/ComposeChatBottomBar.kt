package io.agora.chatroom.ui.compose.chatbottombar

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.SnackbarHost
import androidx.compose.material.SnackbarHostState
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.Bottom
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import io.agora.chatroom.service.ChatMessage
import io.agora.chatroom.ui.commons.ComposerMessageState
import io.agora.chatroom.ui.commons.UIValidationError
import io.agora.chatroom.ui.compose.utils.AboveAnchorPopupPositionProvider
import io.agora.chatroom.ui.compose.utils.mirrorRtl
import io.agora.chatroom.ui.model.UICapabilities
import io.agora.chatroom.ui.model.UIChatBarMenuItem
import io.agora.chatroom.ui.theme.AlphabetBodyLarge
import io.agora.chatroom.ui.theme.barrageDarkColor2
import io.agora.chatroom.ui.theme.barrageLightColor2
import io.agora.chatroom.ui.theme.neutralColor1
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
 * @param input Customizable composable that represents the input field for the composer, [ComposeMessageInput] by default.
 * by default.
 */
@Composable
public fun ComposeChatBottomBar(
    viewModel: MessageComposerViewModel,
    modifier: Modifier = Modifier,
    showInput: Boolean = false,
    onInputClick: () -> Unit = {},
    onMenuClick: (Int) -> Unit = {},
    menuItemResource: List<UIChatBarMenuItem> = viewModel.getMenuItem,
    onSendMessage: (ChatMessage) -> Unit = { },
    onValueChange: (String) -> Unit = { viewModel.setMessageInput(it) },
    label: @Composable (ComposerMessageState) -> Unit = { DefaultComposerLabel(isDarkTheme = viewModel.getTheme,it.ownCapabilities) },
    input: @Composable RowScope.(ComposerMessageState, Boolean) -> Unit = { it, isShowKeyBoard ->
        @Suppress("DEPRECATION_ERROR")
        DefaultComposerInputContent(
            isDarkTheme = viewModel.getTheme,
            composerMessageState = it,
            onValueChange = onValueChange,
            label = label,
            isShowKeyboard = isShowKeyBoard,
        )
    },
    trailingContent: @Composable (ComposerMessageState) -> Unit = {
        DefaultMessageComposerTrailingContent(
            isDarkTheme = viewModel.getTheme,
            value = it.inputValue,
            validationErrors = it.validationErrors,
            ownCapabilities = it.ownCapabilities,
            onSendMessage = { input ->
                val message = viewModel.buildNewMessage(input)
                Log.e("apex","onSendMessage 1")
                onSendMessage(message)
                viewModel.clearData()
            }
        )
    },
    voiceContent: @Composable (ComposerMessageState) -> Unit = {
        DefaultMessageComposerVoiceContent(
            isDarkTheme = viewModel.getTheme,
            ownCapabilities = it.ownCapabilities,
            onVoiceClick = {}
        )
    },
    emojiContent: @Composable (ComposerMessageState, onEmojiClick: (isShowFace:Boolean) -> Unit) -> Unit = { it, status->
        DefaultMessageComposerEmojiContent(
            isDarkTheme = viewModel.getTheme,
            ownCapabilities = it.ownCapabilities,
            onEmojiClick = {
                status(it)
            }
        )
    },
    defaultChatBar: @Composable () -> Unit = {
        DefaultChatBarComposerContent(
            isDarkTheme = viewModel.getTheme,
        )
    },
    defaultChatBarMenu: @Composable (ComposerMessageState) -> Unit = {
        DefaultChatBarMenuComposerContent(
            isDarkTheme = viewModel.getTheme,
            onMenuClick = onMenuClick,
            menuItemResource = menuItemResource,
            ownCapabilities = it.ownCapabilities,
        )
    }
) {
    val messageComposerState by viewModel.composerMessageState.collectAsState()
    if (!showInput){
        viewModel.updateInputValue()
    }

    ComposeChatBottomBar(
        isDarkTheme = viewModel.getTheme,
        modifier = modifier,
        onSendMessage = { text ->
            Log.e("apex","onSendMessage2")
            val messageWithData = viewModel.buildNewMessage(text)
            onSendMessage(messageWithData)
        },
        showInput = showInput,
        input = input,
        onMenuClick = onMenuClick,
        onInputClick = onInputClick,
        menuItemResource = menuItemResource,
        voiceContent = voiceContent,
        emojiContent = emojiContent,
        trailingContent = trailingContent,
        defaultChatBar = defaultChatBar,
        defaultChatBarMenu = defaultChatBarMenu,
        composerMessageState = messageComposerState,
    )
}

/**
 * Clean version of the [ComposeChatBottomBar] that doesn't rely on ViewModels, so the user can provide a
 * manual way to handle and represent data and various operations.
 *
 * @param composerMessageState The state of the message input.
 * @param onSendMessage Handler when the user wants to send a message.
 * @param modifier Modifier for styling.
 * @param onValueChange Handler when the input field value changes.
 * their own integrations, which they need to hook up to their own data providers and UI.
 * @param label Customizable composable that represents the input field label (hint).
 * @param input Customizable composable that represents the input field for the composer, [ComposeMessageInput] by default.
 * by default.
 */
@Composable
public fun ComposeChatBottomBar(
    isDarkTheme: Boolean,
    composerMessageState: ComposerMessageState,
    onSendMessage: (String) -> Unit,
    modifier: Modifier = Modifier,
    showInput: Boolean,
    onInputClick: () -> Unit,
    onMenuClick: (Int) -> Unit = {},
    menuItemResource: List<UIChatBarMenuItem>,
    onValueChange: (String) -> Unit = {},
    label: @Composable (ComposerMessageState) -> Unit = { DefaultComposerLabel(isDarkTheme,composerMessageState.ownCapabilities) },
    input: @Composable RowScope.(ComposerMessageState, Boolean) -> Unit = { it, isShowKeyBoard ->
        @Suppress("DEPRECATION_ERROR")
        DefaultComposerInputContent(
            isDarkTheme = isDarkTheme,
            composerMessageState = composerMessageState,
            onValueChange = onValueChange,
            label = label,
            isShowKeyboard = isShowKeyBoard,
        )
    },
    trailingContent: @Composable (ComposerMessageState) -> Unit = {
        DefaultMessageComposerTrailingContent(
            isDarkTheme = isDarkTheme,
            value = it.inputValue,
            validationErrors = it.validationErrors,
            onSendMessage = onSendMessage,
            ownCapabilities = composerMessageState.ownCapabilities,
        )
    },
    voiceContent: @Composable (ComposerMessageState) -> Unit = {
        DefaultMessageComposerVoiceContent(
            isDarkTheme = isDarkTheme,
            ownCapabilities = it.ownCapabilities,
            onVoiceClick = {}
        )
    },
    emojiContent: @Composable (ComposerMessageState, onEmojiClick: (isShowFace:Boolean) -> Unit) -> Unit = { it, status->
        DefaultMessageComposerEmojiContent(
            isDarkTheme = isDarkTheme,
            ownCapabilities = it.ownCapabilities,
            onEmojiClick = {
                status(it)
            }
        )
    },
    defaultChatBar: @Composable ( ) -> Unit = {
        DefaultChatBarComposerContent(
            isDarkTheme = isDarkTheme,
        )
    },
    defaultChatBarMenu: @Composable (ComposerMessageState) -> Unit = {
        DefaultChatBarMenuComposerContent(
            isDarkTheme = isDarkTheme,
            onMenuClick = onMenuClick,
            menuItemResource = menuItemResource,
            ownCapabilities = it.ownCapabilities,
        )
    }
) {
    val (_,_,validationErrors) = composerMessageState
    val snackbarHostState = remember { SnackbarHostState() }

    val showKeyBoard = remember { mutableStateOf(false) }
    val isShowKeyBoard by showKeyBoard

    MessageInputValidationError(
        validationErrors = validationErrors,
        snackbarHostState = snackbarHostState
    )

    Box(modifier = modifier) {
        if (showInput){
            Column(
                Modifier
                    .height(52.dp)
            ) {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .background(if (isDarkTheme) neutralColor1 else neutralColor98),
                    verticalAlignment = Bottom
                ) {

                    voiceContent(composerMessageState)

                    Row (
                        modifier = Modifier
                            .weight(1f)
                            .padding(top = 8.dp, bottom = 8.dp, start = 8.dp)
                    ){
                        input(composerMessageState,isShowKeyBoard)
                    }

                    emojiContent(composerMessageState,object : (Boolean) -> Unit{
                        override fun invoke(isShowFace: Boolean) {
                            Log.e("apex","emojiContent: $isShowFace")
                        }
                    })

                    trailingContent(composerMessageState)
                }

            }
        }else{

            Column(
                Modifier
                    .height(52.dp)
            ){
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.Transparent),
                    verticalAlignment = Bottom,
                ){
                    Row (
                        horizontalArrangement = Arrangement.spacedBy(0.dp, Alignment.Start),
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .weight(1f)
                            .padding(top = 8.dp, bottom = 8.dp, start = 8.dp)
                            .background(
                                shape = RoundedCornerShape(size = 20.dp),
                                color = if (isDarkTheme) barrageDarkColor2 else barrageLightColor2
                            )
                            .clickable {
                                showKeyBoard.value = true
                                onInputClick()
                            }

                    ){
                        defaultChatBar()
                    }

                    defaultChatBarMenu(composerMessageState)
                }
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
 * @param composerMessageState The state of the message input.
 * @param onValueChange Handler when the input field value changes.
 */
@Deprecated(
    message = "To be marked as an internal component. Use ComposeMessageInput directly.",
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
        imports = arrayOf("io.getstream.chat.android.compose.ui.components.composer.ComposeMessageInput")
    ),
    level = DeprecationLevel.ERROR,
)
@Composable
public fun RowScope.DefaultComposerInputContent(
    isDarkTheme: Boolean = false,
    isShowKeyboard: Boolean,
    composerMessageState: ComposerMessageState,
    onValueChange: (String) -> Unit,
    label: @Composable (ComposerMessageState) -> Unit,
) {
    ComposeMessageInput(
        isDarkTheme = isDarkTheme,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp, bottom = 8.dp, start = 12.dp)
            .weight(1f),
        label = label,
        isShowKeyboard = isShowKeyboard,
        composerMessageState = composerMessageState,
        onValueChange = onValueChange,
    )
}

/**
 * Represents the default trailing content for the Composer, which represent a send button or a cooldown timer.
 *
 * @param value The input value.
 * @param validationErrors List of errors for message validation.
 * @param onSendMessage Handler when the user wants to send a message.
 * @param ownCapabilities Set of capabilities the user is given for the current channel.
 */
@Composable
internal fun DefaultMessageComposerTrailingContent(
    isDarkTheme: Boolean = false,
    value: String,
    validationErrors: List<UIValidationError>,
    ownCapabilities: Set<String>,
    onSendMessage: (String) -> Unit,
) {
    val isSendButtonEnabled = ownCapabilities.contains(UICapabilities.SEND_MESSAGE)
    val isInputValid by lazy { (value.isNotBlank()) && validationErrors.isEmpty() }
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
                    modifier = Modifier
                        .mirrorRtl(layoutDirection = layoutDirection)
                        .size(30.dp, 30.dp),
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
                modifier = Modifier
                    .mirrorRtl(layoutDirection = layoutDirection)
                    .size(30.dp, 30.dp),
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

@Composable
internal fun DefaultChatBarMenuComposerContent(
    isDarkTheme: Boolean = false,
    ownCapabilities: Set<String>,
    onMenuClick: (drawableTag:Int) -> Unit,
    menuItemResource: List<UIChatBarMenuItem>,
){
    menuItemResource.forEach {
        IconButton(
            content = {
                val layoutDirection = LocalLayoutDirection.current
                Box (
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(38.dp, 38.dp)
                        .background(
                            color = if (isDarkTheme) barrageDarkColor2 else barrageLightColor2,
                            shape = RoundedCornerShape(20.dp)
                        )
                ){
                    if (it.drawableTag == 0){
                        Image(
                            modifier = Modifier
                                .mirrorRtl(layoutDirection = layoutDirection)
                                .size(30.dp, 30.dp),
                            painter = painterResource(id = it.drawableResource),
                            contentDescription = "",
                        )
                    }else{
                        Icon(
                            modifier = Modifier
                                .mirrorRtl(layoutDirection = layoutDirection)
                                .size(30.dp, 30.dp),
                            painter = painterResource(id = it.drawableResource),
                            contentDescription = "",
                            tint = if (isDarkTheme) neutralColor98 else neutralColor98
                        )
                    }
                }
            },
            onClick = {
                onMenuClick(it.drawableTag)
            },
        )
    }

}

@Composable
internal fun DefaultChatBarComposerContent(
    isDarkTheme: Boolean = false,
){
    IconButton(
        content = {
            val layoutDirection = LocalLayoutDirection.current
            Icon(
                modifier = Modifier
                    .mirrorRtl(layoutDirection = layoutDirection)
                    .size(20.dp, 20.dp),
                painter = painterResource(id = R.drawable.icon_bubble_fill),
                contentDescription = "",
                tint = if (isDarkTheme) neutralColor98 else neutralColor98,
            )
        },
        enabled = false,
        onClick = {},
    )

    Text(
        text = "Input",
        style = TextStyle(
            fontFamily = FontFamily.Default,
            fontWeight = FontWeight.Normal,
            lineHeight = 22.sp,
            fontSize = 16.sp,
            color = if (isDarkTheme) neutralColor98 else neutralColor98,
            letterSpacing = 0.03.sp,
        ),
        modifier = Modifier.padding(start = 4.dp)
    )
}

/**
 * Shows a [Toast] with an error if one of the following constraints are violated:
 *
 * - The message length exceeds the maximum allowed message length.
 * - The number of selected attachments is too big.
 * - At least one of the attachments is too big.
 *
 * @param validationErrors The list of validation errors for the current user input.
 */
@Composable
private fun MessageInputValidationError(validationErrors: List<UIValidationError>, snackbarHostState: SnackbarHostState) {
    if (validationErrors.isNotEmpty()) {
        val firstValidationError = validationErrors.first()

        val errorMessage = when (firstValidationError) {
            is UIValidationError.MessageLengthExceeded -> {
                stringResource(
                    R.string.stream_compose_message_composer_error_message_length,
                    firstValidationError.maxMessageLength
                )
            }
            else -> {""}
        }

        val context = LocalContext.current
        LaunchedEffect(validationErrors.size) {
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