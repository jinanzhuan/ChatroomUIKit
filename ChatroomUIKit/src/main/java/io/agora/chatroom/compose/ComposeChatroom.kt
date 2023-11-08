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
import io.agora.chatroom.model.UIChatroomInfo
import io.agora.chatroom.model.UIComposeSheetItem
import io.agora.chatroom.service.GiftEntityProtocol
import io.agora.chatroom.service.UserEntity
import io.agora.chatroom.ui.UIChatroomService
import io.agora.chatroom.uikit.R
import io.agora.chatroom.viewmodel.UIRoomViewModel
import io.agora.chatroom.viewmodel.gift.ComposeGiftListViewModel
import io.agora.chatroom.viewmodel.gift.ComposeGiftSheetViewModel
import io.agora.chatroom.viewmodel.member.MemberListViewModel
import io.agora.chatroom.viewmodel.member.MembersBottomSheetViewModel
import io.agora.chatroom.viewmodel.member.MutedListViewModel
import io.agora.chatroom.viewmodel.menu.MessageMenuViewModel
import io.agora.chatroom.viewmodel.menu.RoomMemberMenuViewModel
import io.agora.chatroom.viewmodel.messages.MessageChatBarViewModel
import io.agora.chatroom.viewmodel.messages.MessageListViewModel
import io.agora.chatroom.viewmodel.report.ComposeReportViewModel

@Composable
fun ComposeChatroom(
    roomId:String,
    roomOwner:String,
    service: UIChatroomService = UIChatroomService(UIChatroomInfo(roomId,UserEntity(userId = roomOwner))),
    roomViewModel:UIRoomViewModel = viewModel(UIRoomViewModel::class.java,
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
    giftListViewModel: ComposeGiftListViewModel = viewModel(ComposeGiftListViewModel::class.java,
        factory = defaultMessageListViewModelFactory(LocalContext.current, service.getRoomInfo().roomId, service)),
    reportViewModel: ComposeReportViewModel = viewModel(
        ComposeReportViewModel::class.java,
        factory = defaultReportViewModelFactory(LocalContext.current,service)),
    membersBottomSheetViewModel: MembersBottomSheetViewModel = viewModel(MembersBottomSheetViewModel::class.java,
        factory = defaultMembersViewModelFactory(service.getRoomInfo().roomId, service,
            ChatroomUIKitClient.getInstance().isCurrentRoomOwner(service.getRoomInfo().roomOwner?.userId))),
    memberListViewModel: MemberListViewModel = viewModel(
        MemberListViewModel::class.java,
        factory = defaultMembersViewModelFactory(service.getRoomInfo().roomId, service,
            ChatroomUIKitClient.getInstance().isCurrentRoomOwner(service.getRoomInfo().roomOwner?.userId))),
    memberMenuViewModel: RoomMemberMenuViewModel = viewModel(
        RoomMemberMenuViewModel::class.java,
        factory = defaultMenuViewModelFactory()),
    muteViewModel:MutedListViewModel = viewModel( MutedListViewModel::class.java, factory = defaultMuteListViewModelFactory(
        roomId = service.getRoomInfo().roomId, service = service,
        ChatroomUIKitClient.getInstance().isCurrentRoomOwner(service.getRoomInfo().roomOwner?.userId)) ),
    onMemberSheetSearchClick: ((String) -> Unit)? = null,
    onMessageMenuClick: ((Int, UIComposeSheetItem) -> Unit)? = null,
    onGiftBottomSheetItemClick: ((GiftEntityProtocol) -> Unit) = {},
    chatBackground:Painter = if (roomViewModel.getTheme)
        painterResource(R.drawable.icon_chatroom_bg_dark)
        else painterResource(R.drawable.icon_chatroom_bg_light)
) {
    val roomId = roomViewModel.getRoomService.getRoomInfo().roomId

    if (roomViewModel.isShowLoading.value) {
        loginToRoom()
        service.joinRoom(
            onSuccess = {
                roomViewModel.isShowLoading.value = false
                messageListViewModel.addJoinedMessageByIndex(
                    message = ChatroomUIKitClient.getInstance().insertJoinedMessage(
                        roomId, ChatroomUIKitClient.getInstance().getCurrentUser().userId
                    )
                )
                muteViewModel.fetchMuteList { code, error ->  }
            }
        )
    } else {

        if (roomViewModel.isShowBg.value){
            Image(
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
                painter = chatBackground,
                contentDescription = "bg",
            )
        }

        ComposeChatScreen(
            roomId = roomId,
            service = roomViewModel.getRoomService,
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
            onGiftBottomSheetItemClick = onGiftBottomSheetItemClick
        )

        LaunchedEffect(roomViewModel.closeMemberSheet.value) {
            if (roomViewModel.closeMemberSheet.value){
                membersBottomSheetViewModel.closeDrawer()
            }
        }
    }
}

@Composable
private fun loginToRoom() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        LoadingIndicator()
    }
}


