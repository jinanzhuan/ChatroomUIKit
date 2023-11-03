package io.agora.chatroom.compose.chatbottombar

import android.annotation.SuppressLint
import android.graphics.Rect
import android.util.Log
import android.widget.LinearLayout
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import io.agora.chatroom.commons.ComposerInputMessageState
import io.agora.chatroom.commons.UIValidationError
import io.agora.chatroom.compose.utils.DisplayUtils
import io.agora.chatroom.compose.utils.mirrorRtl
import io.agora.chatroom.data.emojiList
import io.agora.chatroom.model.UICapabilities
import io.agora.chatroom.model.UIChatBarMenuItem
import io.agora.chatroom.model.emoji.UIExpressionEntity
import io.agora.chatroom.theme.ChatroomUIKitTheme
import io.agora.chatroom.viewmodel.messages.MessageChatBarViewModel
import io.agora.chatroom.uikit.R

/**
 * Default ComposeChatBottomBar component that relies on [MessageChatBarViewModel] to handle data and
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
fun ComposeChatBottomBar(
    viewModel: MessageChatBarViewModel,
    modifier: Modifier = Modifier,
    showInput: Boolean = false,
    onInputClick: () -> Unit = {},
    onMenuClick: (Int) -> Unit = {},
    menuItemResource: List<UIChatBarMenuItem> = viewModel.getMenuItem,
    onSendMessage: (String) -> Unit = { },
    onValueChange: (String) -> Unit = { viewModel.setMessageInput(it) },
    label: @Composable (ComposerInputMessageState) -> Unit = { DefaultComposerLabel(isDarkTheme = viewModel.getTheme,it.ownCapabilities) },
    input: @Composable RowScope.(ComposerInputMessageState) -> Unit = { it ->
        @Suppress("DEPRECATION_ERROR")
        DefaultComposerInputContent(
            isDarkTheme = viewModel.getTheme,
            composerMessageState = it,
            onValueChange = onValueChange,
            label = label,
            viewModel = viewModel,
        )
    },
    trailingContent: @Composable (ComposerInputMessageState) -> Unit = {
        DefaultMessageComposerTrailingContent(
            isDarkTheme = viewModel.getTheme,
            value = it.inputValue,
            validationErrors = it.validationErrors,
            ownCapabilities = it.ownCapabilities,
            onSendMessage = { input ->
                onSendMessage(input)
                viewModel.clearData()
            }
        )
    },
    voiceContent: @Composable (ComposerInputMessageState) -> Unit = {
        DefaultMessageComposerVoiceContent(
            isDarkTheme = viewModel.getTheme,
            ownCapabilities = it.ownCapabilities,
            onVoiceClick = {}
        )
    },
    emojiContent: @Composable (ComposerInputMessageState,onEmojiClick: (isShowFace:Boolean) -> Unit) -> Unit = { it,status->
        DefaultMessageComposerEmojiContent(
            isDarkTheme = viewModel.getTheme,
            viewModel = viewModel,
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
    defaultChatBarMenu: @Composable (ComposerInputMessageState) -> Unit = {
        DefaultChatBarMenuComposerContent(
            isDarkTheme = viewModel.getTheme,
            onMenuClick = onMenuClick,
            menuItemResource = menuItemResource,
            ownCapabilities = it.ownCapabilities,
        )
    }
) {
    val messageComposerState by viewModel.composerMessageState.collectAsState()

    ComposeChatBottomBar(
        viewModel = viewModel,
        isDarkTheme = viewModel.getTheme,
        modifier = modifier,
        onSendMessage = { text ->
            onSendMessage(text)
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

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun ComposeChatBottomBar(
    isDarkTheme: Boolean?,
    viewModel: MessageChatBarViewModel,
    composerMessageState: ComposerInputMessageState,
    onSendMessage: (String) -> Unit,
    modifier: Modifier = Modifier,
    showInput: Boolean,
    onInputClick: () -> Unit,
    onMenuClick: (Int) -> Unit = {},
    menuItemResource: List<UIChatBarMenuItem>,
    onValueChange: (String) -> Unit = {},
    label: @Composable (ComposerInputMessageState) -> Unit = { DefaultComposerLabel(isDarkTheme,composerMessageState.ownCapabilities) },
    input: @Composable RowScope.(ComposerInputMessageState) -> Unit = { it ->
        @Suppress("DEPRECATION_ERROR")
        DefaultComposerInputContent(
            isDarkTheme = isDarkTheme,
            composerMessageState = composerMessageState,
            onValueChange = onValueChange,
            label = label,
            viewModel = viewModel,
        )
    },
    trailingContent: @Composable (ComposerInputMessageState) -> Unit = {
        DefaultMessageComposerTrailingContent(
            isDarkTheme = isDarkTheme,
            value = it.inputValue,
            validationErrors = it.validationErrors,
            onSendMessage = onSendMessage,
            ownCapabilities = composerMessageState.ownCapabilities,
        )
    },
    voiceContent: @Composable (ComposerInputMessageState) -> Unit = {
        DefaultMessageComposerVoiceContent(
            isDarkTheme = isDarkTheme,
            ownCapabilities = it.ownCapabilities,
            onVoiceClick = {}
        )
    },
    emojiContent: @Composable (ComposerInputMessageState, onEmojiClick: (isShowFace:Boolean) -> Unit) -> Unit = { it,status->
        DefaultMessageComposerEmojiContent(
            isDarkTheme = isDarkTheme,
            viewModel = viewModel,
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
    defaultChatBarMenu: @Composable (ComposerInputMessageState) -> Unit = {
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

    MessageInputValidationError(
        validationErrors = validationErrors,
        snackbarHostState = snackbarHostState
    )

    val exH =  remember { mutableIntStateOf(0) }
    val exHeight by exH

    val kbHeight =  remember { mutableIntStateOf(0) }
    val keyboardHeight by kbHeight

    val navigationBarsHeight = WindowInsets.navigationBars.getBottom(Density(LocalContext.current))

    AndroidView(factory = { context ->
        LinearLayout(context).apply {
            viewTreeObserver.addOnGlobalLayoutListener {
                val rect = Rect()
                getWindowVisibleDisplayFrame(rect)
                val screenHeight = rootView.height
                val keypadHeight = screenHeight - rect.bottom
                if (keypadHeight > screenHeight * 0.15) { // A threshold to filter the visibility of the keypad
                    kbHeight.intValue = keypadHeight
                }else{
                    viewModel.hideKeyBoard()
                }
            }
        }
    })
    Log.e("apex","keyboardHeight  $keyboardHeight")
    Log.e("apex","navigationBars:  ${WindowInsets.navigationBars.getBottom(Density(LocalContext.current))}")

    exH.intValue = DisplayUtils.pxToDp(keyboardHeight - navigationBarsHeight).toInt()

    Box(modifier = modifier.navigationBarsPadding()) {
        if (showInput){
            Column(
                Modifier.wrapContentHeight()
            ) {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .background(ChatroomUIKitTheme.colors.background),
                    verticalAlignment = Bottom
                ) {

                    voiceContent(composerMessageState)

                    Row (
                        modifier = Modifier
                            .weight(1f)
                            .padding(top = 8.dp, bottom = 8.dp, start = 8.dp)
                    ){
                        input(this,composerMessageState)
                    }

                    emojiContent(composerMessageState,object : (Boolean) -> Unit{
                        override fun invoke(isShowEmoji: Boolean) {
                            if (isShowEmoji){
                                viewModel.hideKeyBoard()
                                viewModel.showEmoji()
                            }else{
                                viewModel.showKeyBoard()
                                viewModel.hideEmoji()
                            }
                        }
                    })

                    trailingContent(composerMessageState)
                }

                if (viewModel.isShowEmoji.value){
                    DefaultComposerEmoji(
                        isDarkTheme = isDarkTheme,
                        maxH = exHeight,
                        emojis = emojiList,
                        viewModel = viewModel
                    )
                }

                Row(
                    Modifier
                        .imePadding()
                        .background(Color.Yellow)
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .background(ChatroomUIKitTheme.colors.background),
                    verticalAlignment = Bottom
                ) {

                }

            }
        }else{

            Column(
                Modifier
                    .wrapContentHeight()
            ){
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
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
                                color = ChatroomUIKitTheme.colors.barrageL20D10
                            )
                            .clickable(
                                indication = null,
                                interactionSource = remember { MutableInteractionSource() },
                            ) {
                                viewModel.showKeyBoard()
                                viewModel.hideEmoji()
                                onInputClick()
                            }

                    ){
                        defaultChatBar()
                    }

                    defaultChatBarMenu(composerMessageState)
                }
            }
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
    isDarkTheme: Boolean?,
    ownCapabilities: Set<String>)
{
    Text(
        text = stringResource(id = R.string.stream_compose_message_label),
        style = ChatroomUIKitTheme.typography.bodyLarge,
        color = ChatroomUIKitTheme.colors.onBackground
    )
}

@Composable
fun DefaultComposerEmoji(
    isDarkTheme: Boolean?,
    emojis:List<UIExpressionEntity>,
    maxH:Int,
    viewModel: MessageChatBarViewModel,
){
    Log.e("apex","DefaultComposerEmoji: $maxH")
    Column(modifier = Modifier
        .fillMaxWidth()
        .height(maxH.dp)
        .background(ChatroomUIKitTheme.colors.background)
    ){
        LazyVerticalGrid(
            columns = GridCells.Fixed(viewModel.eColumns.value)) {
            items(emojis) { emoji ->
                Image(
                    painter = painterResource(emoji.icon),
                    contentDescription = null,
                    modifier = Modifier
                        .size(32.dp)
                        .padding(2.dp)
                        .clickable {
                            viewModel.setEmojiInput(emoji.emojiText)
                        }
                )
            }
        }
    }
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
        imports = arrayOf("ComposeMessageInput")
    ),
    level = DeprecationLevel.ERROR,
)
@Composable
fun RowScope.DefaultComposerInputContent(
    isDarkTheme: Boolean? = false,
    viewModel: MessageChatBarViewModel,
    composerMessageState: ComposerInputMessageState,
    onValueChange: (String) -> Unit,
    label: @Composable (ComposerInputMessageState) -> Unit,
) {
    ComposeMessageInput(
        isDarkTheme = isDarkTheme,
        modifier = Modifier.weight(1f),
        label = label,
        viewModel = viewModel,
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
    isDarkTheme: Boolean? = false,
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
                tint = ChatroomUIKitTheme.colors.primary
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
    isDarkTheme: Boolean? = false,
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
                    tint = ChatroomUIKitTheme.colors.onBackground
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
    isDarkTheme: Boolean? = false,
    viewModel: MessageChatBarViewModel,
    onEmojiClick: (isShowFace:Boolean) -> Unit,
) {
    val resourceId = remember { mutableStateOf(R.drawable.icon_face) }
    val resource by resourceId

    var isShowEmoji = viewModel.isShowEmoji.value
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
                tint = ChatroomUIKitTheme.colors.onBackground
            )
        },
        onClick = {
            isShowEmoji = !isShowEmoji
            resourceId.value = if (isShowEmoji){
                R.drawable.icon_keyboard
            }else{
                R.drawable.icon_face
            }
            onEmojiClick(isShowEmoji)
        }

    )
}

@Composable
internal fun DefaultChatBarMenuComposerContent(
    isDarkTheme: Boolean? = false,
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
                            color = ChatroomUIKitTheme.colors.barrageL20D10,
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
                            tint = ChatroomUIKitTheme.colors.neutralL98D98
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
    isDarkTheme: Boolean? = false,
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
                tint = ChatroomUIKitTheme.colors.neutralL98D98,
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
            color = ChatroomUIKitTheme.colors.neutralL98D98,
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
