package io.agora.chatroom.ui.compose.chatmessagelist

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import io.agora.chatroom.service.ChatMessage
import io.agora.chatroom.ui.compose.ComposeItemType
import io.agora.chatroom.ui.compose.ComposeMessageItem
import io.agora.chatroom.ui.compose.ComposeMessageItemState
import io.agora.chatroom.ui.compose.ComposeMessageListItemState
import io.agora.chatroom.ui.compose.GiftMessageState
import io.agora.chatroom.ui.compose.JoinedMessageState
import io.agora.chatroom.ui.theme.AlphabetLabelLarge
import io.agora.chatroom.ui.theme.primaryColor6
import io.agora.chatroom.ui.viewmodel.messages.MessageListViewModel

/**
 * Represents the message item container that allows us to customize each type of item in the MessageList.
 *
 * @param messageListItem The state of the message list item.
 * @param onLongItemClick Handler when the user long taps on an item.
 * @param giftMessageContent Composable that represents system messages.
 * @param messageItemContent Composable that represents regular messages.
 */
@Composable
public fun MessageContainer(
    viewModel: MessageListViewModel,
    messageListItem: ComposeMessageListItemState,
    onLongItemClick: (ChatMessage) -> Unit = {},
    giftMessageContent: @Composable (GiftMessageState) -> Unit = {
        DefaultGiftMessageContent(giftMessageState = it)
    },
    messageItemContent: @Composable (ComposeMessageItemState) -> Unit = {
        DefaultMessageItem(
            viewModel = viewModel,
            messageItem = it,
            onLongItemClick = onLongItemClick,
        )
    },
    joinedItemContent: @Composable (JoinedMessageState) -> Unit = {
        DefaultJoinedMessageItem(
            viewModel = viewModel,
            messageItem = it,
            onLongItemClick = onLongItemClick,
        )
    }
) {
    when (messageListItem) {
        is JoinedMessageState -> joinedItemContent(messageListItem)
        is GiftMessageState -> giftMessageContent(messageListItem)
        is ComposeMessageItemState -> messageItemContent(messageListItem)

        else -> {}
    }
}

/**
 * The default System message content.
 *
 * A system message is a message generated by a system event, such as updating the channel or muting a user.
 *
 * @param giftMessageState The system message item to show.
 */
@Composable
internal fun DefaultGiftMessageContent(giftMessageState: GiftMessageState) {
    Text(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp, horizontal = 16.dp),
        text = giftMessageState.message.body.toString(),
        color = primaryColor6,
        style = AlphabetLabelLarge,
        textAlign = TextAlign.Center
    )
}

/**
 * The default message item content.
 *
 * @param messageItem The message item to show.
 * @param onLongItemClick Handler when the user long taps on an item.
 */
@Composable
internal fun DefaultMessageItem(
    viewModel: MessageListViewModel,
    messageItem: ComposeMessageItemState,
    onLongItemClick: (ChatMessage) -> Unit,
) {
    ComposeMessageItem(
        itemType = ComposeItemType.NORMAL,
        isShowDateSeparator = viewModel.isShowDateSeparators,
        isShowLabel  = viewModel.isShowLabel,
        isShowGift = viewModel.isShowGift,
        isShowAvatar  = viewModel.isShowAvatar,
        dateSeparatorColor = viewModel.getDateSeparatorColor,
        userNameColor = viewModel.getNickNameColor,
        isDarkTheme = viewModel.getTheme,
        messageItem = messageItem,
        onLongItemClick = onLongItemClick,
    )
}

/**
 * The join message item content.
 *
 * @param messageItem The message item to show.
 * @param onLongItemClick Handler when the user long taps on an item.
 */
@Composable
internal fun DefaultJoinedMessageItem(
    viewModel: MessageListViewModel,
    messageItem: JoinedMessageState,
    onLongItemClick: (ChatMessage) -> Unit,
) {
    ComposeMessageItem(
        itemType = ComposeItemType.ITEM_JOIN,
        isShowDateSeparator = viewModel.isShowDateSeparators,
        isShowLabel = viewModel.isShowLabel,
        isShowGift = false,
        isShowAvatar = viewModel.isShowAvatar,
        dateSeparatorColor = viewModel.getDateSeparatorColor,
        userNameColor = viewModel.getNickNameColor,
        isDarkTheme = viewModel.getTheme,
        messageItem = messageItem,
        onLongItemClick = onLongItemClick,
    )
}