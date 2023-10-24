package io.agora.chatroom.compose.member

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.agora.chatroom.compose.list.LazyColumnList
import io.agora.chatroom.compose.list.LazyColumnListState
import io.agora.chatroom.service.UserEntity
import io.agora.chatroom.service.cache.UIChatroomCacheManager
import io.agora.chatroom.viewmodel.member.MemberListViewModel

@Composable
fun MemberList(
    viewModel: MemberListViewModel,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    showRole: Boolean = false,
    onScrollChange: (LazyColumnListState) -> Unit = {},
    onItemClick: ((UserEntity) -> Unit)? = null,
    onExtendClick: ((UserEntity) -> Unit)? = null,
    itemContent: @Composable (Int, UserEntity) -> Unit = { index, item ->
        val user = UIChatroomCacheManager.getInstance().getUserInfo(item.userId)
        DefaultMemberItem(
            user = user,
            showRole = showRole,
            onItemClick = onItemClick,
            onExtendClick = onExtendClick
        )
    }
) {
    LazyColumnList(
        viewModel = viewModel,
        modifier = modifier,
        contentPadding = contentPadding,
        onScrollChange = onScrollChange,
        itemContent = itemContent)
}