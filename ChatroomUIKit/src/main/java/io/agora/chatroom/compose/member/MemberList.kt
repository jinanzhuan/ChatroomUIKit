package io.agora.chatroom.compose.member

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.agora.chatroom.ChatroomUIKitClient
import io.agora.chatroom.compose.list.LazyColumnList
import io.agora.chatroom.compose.list.LazyColumnListState
import io.agora.chatroom.service.UserEntity
import io.agora.chatroom.viewmodel.LoadMoreState
import io.agora.chatroom.viewmodel.RefreshState
import io.agora.chatroom.viewmodel.RequestState
import io.agora.chatroom.viewmodel.member.MemberListViewModel
import kotlinx.coroutines.launch

@Composable
fun MemberList(
    viewModel: MemberListViewModel,
    modifier: Modifier = Modifier,
    listState: LazyListState = rememberLazyListState(),
    contentPadding: PaddingValues = PaddingValues(0.dp),
    showRole: Boolean = false,
    onScrollChange: (LazyColumnListState) -> Unit = {},
    onItemClick: ((UserEntity) -> Unit)? = null,
    onExtendClick: ((UserEntity) -> Unit)? = null,
    headerContent: @Composable (() -> Unit)? = null,
    bottomContent: @Composable (() -> Unit)? = null,
    itemContent: @Composable (Int, UserEntity) -> Unit = { index, item ->
        val user = ChatroomUIKitClient.getInstance().getCacheManager().getUserInfo(item.userId)
        DefaultMemberItem(
            user = user,
            showRole = showRole,
            onItemClick = onItemClick,
            onExtendClick = onExtendClick
        )
    }
) {
    var canScroll by rememberSaveable { mutableStateOf(false) }
    var targetPosition by rememberSaveable { mutableStateOf(0) }
    val state = viewModel.getState
    val scope = rememberCoroutineScope()
    if (viewModel.isEnableRefresh) {
        headerContent?.let { header ->
            header.invoke()
        }
    }
    LazyColumnList(
        viewModel = viewModel,
        modifier = modifier,
        listState = listState,
        contentPadding = contentPadding,
        onScrollChange = onScrollChange,
        itemContent = itemContent)

    if (viewModel.isEnableLoadMore) {
        bottomContent?.let { bottom ->
            bottom.invoke()
        }
    }

    LaunchedEffect(state) {
        when (state) {
            is RequestState.Loading -> {
                if (viewModel.isEnableRefresh) {
                    viewModel.changeRefreshState(RefreshState.Loading)
                }
            }

            is RequestState.LoadingMore -> {
                if (viewModel.isEnableLoadMore) {
                    viewModel.changeLoadMoreState(LoadMoreState.Loading(viewModel.items.size - 1))
                }
            }

            is RequestState.Success<*> -> {
                if (viewModel.isEnableRefresh && viewModel.getRefreshState is RefreshState.Loading) {
                    viewModel.changeRefreshState(RefreshState.Success)
                }
            }

            is RequestState.SuccessMore<*> -> {
                if (viewModel.isEnableLoadMore && viewModel.getLoadMoreState is LoadMoreState.Loading) {
                    if (viewModel.items.isNotEmpty()) {
                        if (viewModel.getLoadMoreState is LoadMoreState.Loading) {
                            val lastIndex = (viewModel.getLoadMoreState as LoadMoreState.Loading).lastIndex
                            if (viewModel.items.size - 1 > lastIndex) {
                                targetPosition = listState.firstVisibleItemIndex + 1
                                canScroll = true
                            }
                        }
                    }
                    viewModel.changeLoadMoreState(LoadMoreState.Success)
                }
            }

            is RequestState.Error -> {
                if (viewModel.isEnableRefresh && viewModel.getRefreshState is RefreshState.Loading) {
                    viewModel.changeRefreshState(RefreshState.Fail)
                }
                if (viewModel.isEnableLoadMore && viewModel.getLoadMoreState is LoadMoreState.Loading) {
                    viewModel.changeLoadMoreState(LoadMoreState.Fail)
                }
            }

            else -> {
                // do noting
            }
        }
    }

    LaunchedEffect(listState) {
        snapshotFlow { listState.isScrollInProgress }
            .collect { isScrollInProgress ->
                if(!isScrollInProgress) {
                    if (canScroll) {
                        scope.launch {
                            listState.animateScrollToItem(targetPosition)
                        }
                        canScroll = false
                    }
                }
            }
    }
}