package io.agora.chatroom.ui.compose.chatmessagelist

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import io.agora.chatroom.ui.commons.ComposeMessagesState
import io.agora.chatroom.ui.compose.ComposeMessageItemState
import io.agora.chatroom.ui.compose.ComposeMessageListItemState
import io.agora.chatroom.ui.compose.ComposeMessagesScrollingOption
import io.agora.chatroom.ui.compose.MyOwn
import io.agora.chatroom.ui.compose.Other
import kotlinx.coroutines.launch
import kotlin.math.abs

@SuppressLint("UnrememberedMutableState")
@Composable
public fun ComposeMessages(
    modifier: Modifier = Modifier,
    messagesState: ComposeMessagesState,
    lazyListState: LazyListState,
    onMessagesStartReached: () -> Unit,
    onScrolledToBottom: () -> Unit,
    contentPadding: PaddingValues = PaddingValues(vertical = 16.dp),
    helperContent: @Composable BoxScope.() -> Unit = { DefaultMessagesHelperContent(messagesState, lazyListState) },
    loadingMoreContent: @Composable () -> Unit = { DefaultMessagesLoadingMoreIndicator() },
    itemContent: @Composable (ComposeMessageListItemState) -> Unit,
) {
    val (_, isLoadingMore, endOfMessages, messages) = messagesState
    var parentSize by remember { mutableStateOf(IntSize(0, 0)) }
    val density = LocalDensity.current

    Box(modifier = modifier) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .onGloballyPositioned {
                    val bottomPadding = contentPadding.calculateBottomPadding()
                    val topPadding = contentPadding.calculateTopPadding()

                    val paddingPixels = with(density) {
                        bottomPadding.roundToPx() + topPadding.roundToPx()
                    }

                    parentSize = IntSize(
                        width = it.size.width,
                        height = it.size.height - paddingPixels
                    )
                },
            state = lazyListState,
            horizontalAlignment = Alignment.Start,
            reverseLayout = true,
            contentPadding = contentPadding
        ){
            itemsIndexed(messages){index, item ->
                val messageItemModifier = if (item is ComposeMessageItemState && item.focusState == MessageFocused) {
                    Modifier.onGloballyPositioned {
                        if (messagesState.focusedMessageOffset.value == null) {
                            messagesState.calculateMessageOffset(parentSize, it.size)
                        }
                    }
                } else {
                    Modifier
                }

                Box(modifier = messageItemModifier) {
                    itemContent(item)

                    if (index == 0 && lazyListState.isScrollInProgress) {
                        onScrolledToBottom()
                    }

                    if (!endOfMessages && index == messages.lastIndex &&
                        messages.isNotEmpty() &&
                        lazyListState.isScrollInProgress
                    ) {
                        onMessagesStartReached()
                    }
                }
            }

            if (isLoadingMore) {
                item {
                    loadingMoreContent()
                }
            }
        }
        // 跳转最底部
        helperContent()
    }
}


@Composable
internal fun BoxScope.DefaultMessagesHelperContent(
    messagesState: ComposeMessagesState,
    lazyListState: LazyListState,
) {
    val (_, _, _, messages, _,_, _, newMessageState) = messagesState
    val coroutineScope = rememberCoroutineScope()

    val firstVisibleItemIndex = lazyListState.firstVisibleItemIndex

    val focusedItemIndex = messages.indexOfFirst { it is ComposeMessageItemState && it.focusState is MessageFocused }

    val offset = messagesState.focusedMessageOffset.collectAsState()

    LaunchedEffect(
        newMessageState,
        firstVisibleItemIndex,
        focusedItemIndex,
        offset.value
    ) {
        if (focusedItemIndex != -1 && !lazyListState.isScrollInProgress) {
            coroutineScope.launch {
                lazyListState.scrollToItem(focusedItemIndex, offset.value ?: 0)
            }
        }

        when {
            !lazyListState.isScrollInProgress && newMessageState == Other &&
                    firstVisibleItemIndex < 3 -> coroutineScope.launch {
                lazyListState.animateScrollToItem(0)
            }

            !lazyListState.isScrollInProgress && newMessageState == MyOwn -> coroutineScope.launch {
                if (firstVisibleItemIndex > 5) {
                    lazyListState.scrollToItem(5)
                }
                lazyListState.animateScrollToItem(0)
            }
        }
    }

    if (abs(firstVisibleItemIndex) >= 3) {
        ComposeMessagesScrollingOption(
            unreadCount = messagesState.unreadCount,
            modifier = Modifier.align(Alignment.BottomEnd),
            onClick = {
                coroutineScope.launch {
                    if (firstVisibleItemIndex > 5) {
                        lazyListState.scrollToItem(5)
                    }
                    lazyListState.animateScrollToItem(0)
                }
            }
        )
    }
}