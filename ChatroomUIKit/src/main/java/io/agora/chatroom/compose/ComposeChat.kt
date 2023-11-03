package io.agora.chatroom.compose

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.lifecycle.viewmodel.compose.viewModel
import io.agora.chatroom.ChatroomUIKitClient
import io.agora.chatroom.compose.indicator.LoadingIndicator
import io.agora.chatroom.model.UIComposeSheetItem
import io.agora.chatroom.theme.ChatroomUIKitTheme
import io.agora.chatroom.ui.SearchScaffold
import io.agora.chatroom.ui.UIChatroomService
import io.agora.chatroom.uikit.R
import io.agora.chatroom.viewmodel.UIRoomViewModel
import io.agora.chatroom.viewmodel.gift.ComposeGiftListViewModel
import io.agora.chatroom.viewmodel.gift.ComposeGiftSheetViewModel
import io.agora.chatroom.viewmodel.member.MemberListViewModel
import io.agora.chatroom.viewmodel.member.MembersBottomSheetViewModel
import io.agora.chatroom.viewmodel.menu.MessageMenuViewModel
import io.agora.chatroom.viewmodel.menu.RoomMemberMenuViewModel
import io.agora.chatroom.viewmodel.messages.MessageChatBarViewModel
import io.agora.chatroom.viewmodel.messages.MessageListViewModel
import io.agora.chatroom.viewmodel.report.ComposeReportViewModel

@Composable
fun ComposeChat(
    service: UIChatroomService,
    viewModel:UIRoomViewModel = viewModel(UIRoomViewModel::class.java,
        factory = defaultMessageListViewModelFactory(LocalContext.current, service.getRoomInfo().roomId, service = service)),
    messageListViewModel: MessageListViewModel = viewModel(
        MessageListViewModel::class.java,
        factory = defaultMessageListViewModelFactory(LocalContext.current, service.getRoomInfo().roomId, service)),
    chatBottomBarViewModel: MessageChatBarViewModel = viewModel(
        MessageChatBarViewModel::class.java,
        factory = defaultMessageListViewModelFactory(LocalContext.current, service.getRoomInfo().roomId, service)),
    messageItemMenuViewModel: MessageMenuViewModel = viewModel(
        MessageMenuViewModel::class.java,
        factory = defaultMenuViewModelFactory()),
    giftBottomSheetViewModel: ComposeGiftSheetViewModel = viewModel(
        ComposeGiftSheetViewModel::class.java,
        factory = defaultMessageListViewModelFactory(LocalContext.current, service.getRoomInfo().roomId, service)),
    giftListViewModel: ComposeGiftListViewModel = ComposeGiftListViewModel(service = service),
    reportViewModel: ComposeReportViewModel = viewModel(
        ComposeReportViewModel::class.java,
        factory = defaultReportViewModelFactory(service)),
    membersBottomSheetViewModel: MembersBottomSheetViewModel = viewModel(MembersBottomSheetViewModel::class.java,
        factory = defaultMembersViewModelFactory(service.getRoomInfo().roomId, service, ChatroomUIKitClient.getInstance().isCurrentRoomOwner())),
    memberListViewModel: MemberListViewModel = viewModel(
        MemberListViewModel::class.java,
        factory = defaultMembersViewModelFactory(service.getRoomInfo().roomId, service, ChatroomUIKitClient.getInstance().isCurrentRoomOwner())),
    memberMenuViewModel: RoomMemberMenuViewModel = viewModel(
        RoomMemberMenuViewModel::class.java,
        factory = defaultMenuViewModelFactory()),
    onMemberSheetSearchClick: ((String) -> Unit)? = null,
    onMessageMenuClick: ((Int, UIComposeSheetItem) -> Unit)? = null,
    chatBackground:Painter = if (viewModel.getTheme)
        painterResource(R.drawable.icon_chatroom_bg_dark)
        else painterResource(R.drawable.icon_chatroom_bg_light)
) {
    val roomId = viewModel.getRoomService.getRoomInfo().roomId
    val roomOwner = ChatroomUIKitClient.getInstance().getContext().getCurrentRoomInfo().roomOwner
    val currentUser = ChatroomUIKitClient.getInstance().getCurrentUser()

    if (viewModel.closeMemberSheet.value) {
        loginToRoom()
    } else {
        ChatroomUIKitTheme{
            Image(
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
                painter = chatBackground,
                contentDescription = "bg",
            )

            if (viewModel.isShowSearch.value){
                showSearch(viewModel)
            }else{
                ComposeChatScreen(
                    roomId = roomId,
                    service = viewModel.getRoomService,
//                    roomViewModel = viewModel,
                    messageListViewModel= messageListViewModel,
                    chatBottomBarViewModel = chatBottomBarViewModel,
                    messageItemMenuViewModel = messageItemMenuViewModel,
                    giftBottomSheetViewModel = giftBottomSheetViewModel,
                    giftListViewModel = giftListViewModel,
                    reportViewModel = reportViewModel,
                    memberListViewModel = memberListViewModel,
                    memberMenuViewModel = memberMenuViewModel,
                    membersBottomSheetViewModel = membersBottomSheetViewModel,
                    onMessageMenuClick = onMessageMenuClick,
                    onMemberSheetSearchClick = onMemberSheetSearchClick,
                )
            }

            LaunchedEffect(viewModel.closeMemberSheet.value) {
                if (viewModel.closeMemberSheet.value){
                    membersBottomSheetViewModel.closeDrawer()
                }
            }
        }
    }
}

@Composable
private fun loginToRoom() {
    ChatroomUIKitTheme {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            LoadingIndicator()
        }
    }
}
@Composable
private fun showSearch(viewModel:UIRoomViewModel,){
    val roomId = viewModel.getRoomService.getRoomInfo().roomId
    val title = viewModel.searchKey.value
//    SearchScaffold(this, roomId, title)
}


