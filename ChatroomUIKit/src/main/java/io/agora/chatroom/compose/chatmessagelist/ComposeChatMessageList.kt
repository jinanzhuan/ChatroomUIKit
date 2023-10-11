package io.agora.chatroom.compose.chatmessagelist

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import io.agora.chatroom.compose.LoadingIndicator
import io.agora.chatroom.theme.AlphabetHeadlineMedium
import io.agora.chatroom.theme.primaryColor5
import io.agora.chatroom.viewmodel.messages.MessageListViewModel
import io.agora.chatroom.uikit.R

@Composable
fun ComposeChatMessageList(
    viewModel: MessageListViewModel,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(vertical = 16.dp),
    onLongItemClick: (Int, ComposeMessageListItemState) -> Unit = { index, message->},
    loadingContent: @Composable () -> Unit = { DefaultMessageListLoadingIndicator(modifier) },
    emptyContent: @Composable () -> Unit = { DefaultMessageListEmptyContent(modifier) },
    itemContent: @Composable (Int, ComposeMessageListItemState) -> Unit = { index, messageListItem ->
        DefaultMessageContainer(
            itemIndex = index,
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
        onLongItemClick = onLongItemClick,
        itemContent = itemContent,
        loadingContent = loadingContent,
        emptyContent = emptyContent,
    )
}


@Composable
fun MessageList(
    viewModel: MessageListViewModel,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(vertical = 16.dp),
    onLongItemClick: (Int, ComposeMessageListItemState) -> Unit = { index, message->},
    loadingContent: @Composable () -> Unit = { DefaultMessageListLoadingIndicator(modifier) },
    emptyContent: @Composable () -> Unit = { DefaultMessageListEmptyContent(modifier) },
    itemContent: @Composable (Int, ComposeMessageListItemState) -> Unit = { index, it->
        DefaultMessageContainer(
            itemIndex = index,
            viewModel = viewModel,
            messageListItem = it,
            onLongItemClick = onLongItemClick,
        )
    },
 ){
    val messagesState = viewModel.currentComposeMessagesState

    val loading = remember { mutableStateOf(messagesState.isLoading) }
    val isLoading by loading

    Log.e("apex","状态改变重新计算")

    when {
        isLoading -> {
            loadingContent()
        }
        (messagesState.messages.isNotEmpty()) -> ComposeMessages(
            modifier = modifier,
            contentPadding = contentPadding,
            messagesState = messagesState,
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
    itemIndex:Int,
    viewModel: MessageListViewModel,
    messageListItem: ComposeMessageListItemState,
    onLongItemClick: (Int, ComposeMessageListItemState) -> Unit,
) {
    MessageContainer(
        itemIndex = itemIndex,
        viewModel = viewModel,
        messageListItem = messageListItem,
        onLongItemClick = onLongItemClick,
    )
}