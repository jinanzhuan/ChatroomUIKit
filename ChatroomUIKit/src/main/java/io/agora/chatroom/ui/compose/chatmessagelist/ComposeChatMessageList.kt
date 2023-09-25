package io.agora.chatroom.ui.compose.chatmessagelist

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import io.agora.chatroom.service.ChatMessage
import io.agora.chatroom.ui.commons.ComposeMessagesState
import io.agora.chatroom.ui.compose.ComposeMessageListItemState
import io.agora.chatroom.ui.compose.LoadingIndicator
import io.agora.chatroom.ui.compose.utils.rememberMessageListState
import io.agora.chatroom.ui.theme.AlphabetHeadlineMedium
import io.agora.chatroom.ui.theme.primaryColor5
import io.agora.chatroom.ui.viewmodel.messages.MessageListViewModel
import io.agora.chatroom.uikit.R

@Composable
public fun ComposeChatMessageList(
    viewModel: MessageListViewModel,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(vertical = 16.dp),
    lazyListState: LazyListState =
        rememberMessageListState(parentMessageId = viewModel.currentMessagesState.parentMessageId),
    onLongItemClick: (ChatMessage) -> Unit = { viewModel.selectMessage(it) },
    onMessagesStartReached: () -> Unit = { viewModel.isShowLoadMore() },
    onScrollToBottom: () -> Unit = { viewModel.clearMessageState() },
    loadingContent: @Composable () -> Unit = { DefaultMessageListLoadingIndicator(modifier) },
    emptyContent: @Composable () -> Unit = { DefaultMessageListEmptyContent(modifier) },
    loadingMoreContent: @Composable () -> Unit = { DefaultMessagesLoadingMoreIndicator() },
    helperContent: @Composable BoxScope.() -> Unit = {

    },
    itemContent: @Composable (ComposeMessageListItemState) -> Unit = { messageListItem ->
        DefaultMessageContainer(
            viewModel = viewModel,
            messageListItem = messageListItem,
            onLongItemClick = onLongItemClick,
        )
    },
){
    MessageList(
        modifier = modifier,
        viewModel = viewModel,
        contentPadding = contentPadding,
        currentState = viewModel.currentMessagesState,
        lazyListState = lazyListState,
        onMessagesStartReached = onMessagesStartReached,
        onLongItemClick = onLongItemClick,
        onScrolledToBottom = onScrollToBottom,
        itemContent = itemContent,
        helperContent = helperContent,
        loadingMoreContent = loadingMoreContent,
        loadingContent = loadingContent,
        emptyContent = emptyContent,
    )
}


@Composable
public fun MessageList(
    viewModel: MessageListViewModel,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(vertical = 16.dp),
    currentState: ComposeMessagesState,
    lazyListState: LazyListState = rememberMessageListState(parentMessageId = currentState.parentMessageId),
    onMessagesStartReached: () -> Unit = {},
    onLongItemClick: (ChatMessage) -> Unit = {},
    onScrolledToBottom: () -> Unit = {},
    loadingMoreContent: @Composable () -> Unit = { DefaultMessagesLoadingMoreIndicator() },
    loadingContent: @Composable () -> Unit = { DefaultMessageListLoadingIndicator(modifier) },
    emptyContent: @Composable () -> Unit = { DefaultMessageListEmptyContent(modifier) },
    helperContent: @Composable BoxScope.() -> Unit = {

    },
    itemContent: @Composable (ComposeMessageListItemState) -> Unit = {
        DefaultMessageContainer(
            viewModel = viewModel,
            messageListItem = it,
            onLongItemClick = onLongItemClick,
        )
    },
 ){
    Log.e("apex","aa ${viewModel.currentMessagesState.messageItems}")
    val (isLoading,_,_,messages) = currentState
    Log.e("apex","messages size:  ${messages.size}")
    when {
        isLoading -> loadingContent()
        (messages.isNotEmpty()) -> ComposeMessages(
            modifier = modifier,
            contentPadding = contentPadding,
            messagesState = currentState,
            lazyListState = lazyListState,
            onMessagesStartReached = onMessagesStartReached,
            onScrolledToBottom = onScrolledToBottom,
            helperContent = helperContent,
            loadingMoreContent = loadingMoreContent,
            itemContent = itemContent,
        )
        else -> {
            emptyContent()
        }
    }
}


/**
 * The default message list loading indicator.
 *
 * @param modifier Modifier for styling.
 */
@Composable
internal fun DefaultMessageListLoadingIndicator(modifier: Modifier) {

}

/**
 * The default empty placeholder that is displayed when there are no messages in the channel.
 *
 * @param modifier Modifier for styling.
 */
@Composable
internal fun DefaultMessageListEmptyContent(modifier: Modifier) {
    Box(
        modifier = modifier.background(color = Color.Transparent),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = stringResource(R.string.stream_compose_message_list_empty_messages),
            style = AlphabetHeadlineMedium,
            color = primaryColor5,
            textAlign = TextAlign.Center
        )
    }
}

/**
 * The default loading more indicator.
 */
@Composable
internal fun DefaultMessagesLoadingMoreIndicator() {
    LoadingIndicator(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(8.dp)
    )
}

@Composable
internal fun DefaultMessageContainer(
    viewModel: MessageListViewModel,
    messageListItem: ComposeMessageListItemState,
    onLongItemClick: (ChatMessage) -> Unit,
) {
    MessageContainer(
        viewModel = viewModel,
        messageListItem = messageListItem,
        onLongItemClick = onLongItemClick,
    )
}