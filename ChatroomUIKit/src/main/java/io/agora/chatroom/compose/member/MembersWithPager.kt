package io.agora.chatroom.compose.member

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import io.agora.chatroom.compose.drawer.ComposeBottomSheet
import io.agora.chatroom.compose.search.DefaultSearchBar
import io.agora.chatroom.compose.tabrow.PagerWithTabs
import io.agora.chatroom.service.UserEntity
import io.agora.chatroom.theme.ChatroomUIKitTheme
import io.agora.chatroom.ui.UIChatroomService
import io.agora.chatroom.uikit.R
import io.agora.chatroom.viewmodel.RequestState
import io.agora.chatroom.viewmodel.member.MemberListViewModel
import io.agora.chatroom.viewmodel.member.MemberViewModelFactory
import io.agora.chatroom.viewmodel.member.MembersBottomSheetViewModel
import io.agora.chatroom.viewmodel.member.MutedListViewModel
import io.agora.chatroom.viewmodel.menu.BottomSheetViewModel
import io.agora.chatroom.viewmodel.pager.PagerViewModel
import io.agora.chatroom.viewmodel.pager.TabInfo

@ExperimentalFoundationApi
@Composable
fun MembersWithPager(
    modifier: Modifier = Modifier,
    roomId: String,
    roomService: UIChatroomService,
    isAdmin: Boolean = false,
    onItemClick: ((String, UserEntity) -> Unit)? = null,
    onExtendClick: ((String, UserEntity) -> Unit)? = null,
    onSearchClick: ((String) -> Unit)? = null
) {
    var tabList = mutableListOf<TabInfo>()
    tabList += TabInfo(stringResource(id = R.string.member_management_participant))
    if (isAdmin) {
        tabList += TabInfo(stringResource(id = R.string.member_management_mute))
    }
    val memberViewModel = viewModel(MemberListViewModel::class.java, factory = MemberViewModelFactory(
        LocalContext.current, roomId, roomService))
    val mutedViewModel = viewModel(MutedListViewModel::class.java, factory = MemberViewModelFactory(
        LocalContext.current, roomId, roomService))

    PagerWithTabs(
        viewModel = PagerViewModel(tabList = tabList),
        modifier = modifier,
        tabIndicatorHeight = 4.dp,
        tabIndicatorShape = RoundedCornerShape(4.dp)
    ) {
        page ->

        val tabInfo = tabList[page]
        when(tabInfo.title) {
            stringResource(id = R.string.member_management_participant) -> {
                MembersPage(
                    viewModel = memberViewModel,
                    tabInfo = tabInfo,
                    onItemClick = onItemClick,
                    onExtendClick = onExtendClick,
                    onSearchClick = onSearchClick)
            }
            stringResource(id = R.string.member_management_mute) -> {
                MutedListPage(
                    viewModel = mutedViewModel,
                    tabInfo = tabInfo,
                    onItemClick = onItemClick,
                    onExtendClick = onExtendClick,
                    onSearchClick = onSearchClick)
            }
        }
    }
}

@Composable
fun MembersPage(
    modifier: Modifier = Modifier,
    viewModel: MemberListViewModel,
    tabInfo: TabInfo,
    onItemClick: ((String, UserEntity) -> Unit)? = null,
    onExtendClick: ((String, UserEntity) -> Unit)? = null,
    onSearchClick: ((String) -> Unit)? = null
) {
    val state = viewModel.state
    if (state == RequestState.Idle) {
        viewModel.fetchRoomMembers()
    }
    Column(modifier = modifier) {
        DefaultSearchBar(
            onClick = {
                onSearchClick?.invoke(tabInfo.title)
            }
        )
        MemberList(
            viewModel = viewModel,
            modifier = Modifier
                .fillMaxSize()
                .background(ChatroomUIKitTheme.colors.background),
            showRole = true,
            onItemClick = { user ->
                onItemClick?.invoke(tabInfo.title, user)
            },
            onExtendClick = { user ->
                onExtendClick?.invoke(tabInfo.title, user)
            },
            onScrollChange = { listState ->
                if (listState.isScrollInProgress) {
                    if (!listState.canScrollForward && viewModel.hasMore()) viewModel.fetchMoreRoomMembers()
                } else {
                    viewModel.fetchUsersInfo(listState.firstVisibleIndex, listState.lastVisibleIndex)
                }
            }
        )
    }
}

@Composable
fun MutedListPage(
    modifier: Modifier = Modifier,
    viewModel: MutedListViewModel,
    tabInfo: TabInfo,
    onItemClick: ((String, UserEntity) -> Unit)? = null,
    onExtendClick: ((String, UserEntity) -> Unit)? = null,
    onSearchClick: ((String) -> Unit)? = null
) {
    val state = viewModel.state
    if (state == RequestState.Idle) {
        viewModel.fetchMuteList()
    }
    Column(modifier = modifier) {
        DefaultSearchBar(
            onClick = {
                onSearchClick?.invoke(tabInfo.title)
            }
        )
        MemberList(
            viewModel = viewModel,
            modifier = Modifier
                .fillMaxSize()
                .background(ChatroomUIKitTheme.colors.background),
            showRole = false,
            onItemClick = { user ->
                onItemClick?.invoke(tabInfo.title, user)
            },
            onExtendClick = { user ->
                onExtendClick?.invoke(tabInfo.title, user)
            },
            onScrollChange = { listState ->
                if (!listState.isScrollInProgress) {
                    viewModel.fetchUsersInfo(listState.firstVisibleIndex, listState.lastVisibleIndex)
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun ComposeMembersBottomSheet(
    viewModel: MembersBottomSheetViewModel,
    modifier: Modifier = Modifier,
    onItemClick: ((String, UserEntity) -> Unit)? = null,
    onExtendClick: ((String, UserEntity) -> Unit)? = null,
    onSearchClick: ((String) -> Unit)? = null,
    drawerContent: @Composable () -> Unit = {
        MembersWithPager(
            roomId = viewModel.roomId,
            roomService = viewModel.roomService,
            isAdmin = true,
            onItemClick = onItemClick,
            onExtendClick = onExtendClick,
            onSearchClick = onSearchClick
        )
    },
    screenContent: @Composable () -> Unit = {  },
    onDismissRequest: () -> Unit,
    shape: Shape = ChatroomUIKitTheme.shapes.bottomSheet,
    containerColor: Color = ChatroomUIKitTheme.colors.background,
    contentColor: Color = contentColorFor(containerColor),
    tonalElevation: Dp = BottomSheetDefaults.Elevation,
    scrimColor: Color = BottomSheetDefaults.ScrimColor,
    dragHandle: @Composable (() -> Unit)? = { BottomSheetDefaults.DragHandle() },
    windowInsets: WindowInsets = BottomSheetDefaults.windowInsets,
){
    ComposeBottomSheet(
        modifier = modifier,
        viewModel = viewModel,
        drawerContent = drawerContent,
        screenContent = screenContent,
        onDismissRequest = onDismissRequest,
        shape = shape,
        containerColor = containerColor,
        contentColor = contentColor,
        tonalElevation = tonalElevation,
        scrimColor = scrimColor,
        dragHandle = dragHandle,
        windowInsets = windowInsets
    )
}