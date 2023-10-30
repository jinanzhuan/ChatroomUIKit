package io.agora.chatroom.compose

import android.content.Context
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.lifecycle.viewmodel.compose.viewModel
import io.agora.chatroom.ChatroomUIKitClient
import io.agora.chatroom.compose.chatbottombar.ComposeChatBottomBar
import io.agora.chatroom.compose.chatmessagelist.ComposeChatMessageList
import io.agora.chatroom.compose.chatmessagelist.ComposeMessageItemState
import io.agora.chatroom.compose.dialog.SimpleDialog
import io.agora.chatroom.compose.drawer.ComposeMenuBottomSheet
import io.agora.chatroom.compose.gift.ComposeGiftBottomSheet
import io.agora.chatroom.compose.gift.ComposeGiftItemState
import io.agora.chatroom.compose.gift.ComposeGiftList
import io.agora.chatroom.compose.member.ComposeMembersBottomSheet
import io.agora.chatroom.compose.report.ComposeMessageReport
import io.agora.chatroom.model.UIComposeSheetItem
import io.agora.chatroom.service.ChatLog
import io.agora.chatroom.service.ChatMessage
import io.agora.chatroom.theme.ChatroomUIKitTheme
import io.agora.chatroom.ui.UIChatroomService
import io.agora.chatroom.uikit.R
import io.agora.chatroom.viewmodel.dialog.DialogViewModel
import io.agora.chatroom.viewmodel.gift.ComposeGiftListViewModel
import io.agora.chatroom.viewmodel.gift.ComposeGiftSheetViewModel
import io.agora.chatroom.viewmodel.member.MemberListViewModel
import io.agora.chatroom.viewmodel.member.MemberViewModelFactory
import io.agora.chatroom.viewmodel.member.MembersBottomSheetViewModel
import io.agora.chatroom.viewmodel.menu.MenuViewModelFactory
import io.agora.chatroom.viewmodel.menu.MessageMenuViewModel
import io.agora.chatroom.viewmodel.menu.RoomMemberMenuViewModel
import io.agora.chatroom.viewmodel.messages.MessageChatBarViewModel
import io.agora.chatroom.viewmodel.messages.MessageListViewModel
import io.agora.chatroom.viewmodel.messages.MessagesViewModelFactory
import io.agora.chatroom.viewmodel.report.ComposeReportViewModel
import io.agora.chatroom.viewmodel.report.ReportViewModelFactory

private const val TAG = "ComposeChatScreen"

/**
 * This is a compose screen that shows the chat screen.
 * @param roomId The room id of the chat room.
 * @param service The chat room service.
 * @param messageListViewModel The view model for the message list.
 * @param chatBottomBarViewModel The view model for the chat bottom bar.
 * @param messageItemMenuViewModel The view model for the message item menu.
 * @param giftBottomSheetViewModel The view model for the gift bottom sheet.
 * @param giftListViewModel The view model for the gift list.
 * @param reportViewModel The view model for the report.
 * @param membersBottomSheetViewModel The view model for the members bottom sheet.
 * @param memberListViewModel The view model for the member list.
 * @param memberMenuViewModel The view model for the member menu.
 * @param onMemberSheetSearchClick The callback for the member sheet search click.
 * @param onMessageMenuClick The callback for the message menu click.
 */
@Composable
fun ComposeChatScreen(
    roomId: String,
    service: UIChatroomService,
    messageListViewModel: MessageListViewModel = viewModel(MessageListViewModel::class.java,
        factory = defaultMessageListViewModelFactory(LocalContext.current, roomId, service)),
    chatBottomBarViewModel: MessageChatBarViewModel = viewModel(MessageChatBarViewModel::class.java,
        factory = defaultMessageListViewModelFactory(LocalContext.current, roomId, service)),
    messageItemMenuViewModel: MessageMenuViewModel = viewModel(MessageMenuViewModel::class.java,
        factory = defaultMenuViewModelFactory()),
    giftBottomSheetViewModel: ComposeGiftSheetViewModel = viewModel(ComposeGiftSheetViewModel::class.java,
        factory = defaultMessageListViewModelFactory(LocalContext.current, roomId, service)),
    giftListViewModel: ComposeGiftListViewModel = ComposeGiftListViewModel(service = service),
    reportViewModel: ComposeReportViewModel = viewModel(ComposeReportViewModel::class.java,
        factory = defaultReportViewModelFactory(service)),
    membersBottomSheetViewModel: MembersBottomSheetViewModel = viewModel(MembersBottomSheetViewModel::class.java,
        factory = defaultMembersViewModelFactory(roomId, service, ChatroomUIKitClient.getInstance().isCurrentRoomOwner())),
    memberListViewModel: MemberListViewModel = viewModel(MemberListViewModel::class.java,
        factory = defaultMembersViewModelFactory(roomId, service, ChatroomUIKitClient.getInstance().isCurrentRoomOwner())),
    memberMenuViewModel: RoomMemberMenuViewModel = viewModel(RoomMemberMenuViewModel::class.java,
        factory = defaultMenuViewModelFactory()),
    onMemberSheetSearchClick: ((String) -> Unit)? = null,
    onMessageMenuClick: ((Int, UIComposeSheetItem) -> Unit)? = null
) {
    messageListViewModel.registerChatroomChangeListener()
    messageListViewModel.registerChatroomGiftListener()
    giftListViewModel.registerGiftListener()
    giftListViewModel.openAutoClear()
    giftListViewModel.setAutoClearTime(3000L)
    ChatroomUIKitClient.getInstance().getContext().setUseGiftsInList(true)

    val context = LocalContext.current

    Box(modifier = Modifier.fillMaxSize()) {

        val dialogViewModel = DialogViewModel()

        val isShowInput by lazy { mutableStateOf(false) }

        ConstraintLayout(modifier = Modifier
            .fillMaxSize()
            .clickable {
                isShowInput.value = false
            }
        ) {
            val defaultBottomSheetHeight = LocalConfiguration.current.screenHeightDp/2
            val (giftList, msgList, bottomBar) = createRefs()
            ComposeGiftBottomSheet(
                modifier = Modifier
                    .height((LocalConfiguration.current.screenHeightDp/2).dp),
                viewModel = giftBottomSheetViewModel,
                containerColor = ChatroomUIKitTheme.colors.background,
                screenContent = {},
                onGiftItemClick = {
                    messageListViewModel.sendGift(it, onSuccess = {
                            message ->
                            run {
                                giftListViewModel.addDateToIndex(data = ComposeGiftItemState(it))
                            }
                        }, onError = {_, _ ->}
                    )
                },
                onDismissRequest = {
                    giftBottomSheetViewModel.closeDrawer()
                }
            )

            ShowComposeMenuDrawer(
                menuViewModel = messageItemMenuViewModel,
                memberListViewModel = memberListViewModel,
                reportViewModel = reportViewModel,
                messageListViewModel = messageListViewModel,
                onMessageMenuClick = onMessageMenuClick)

            ComposeMessageReport(
                modifier = Modifier.height((LocalConfiguration.current.screenHeightDp/2).dp),
                containerColor = ChatroomUIKitTheme.colors.background,
                viewModel = reportViewModel,
                onConfirmClick = {
                    reportViewModel.reportMessageToServer(it)
                    reportViewModel.closeDrawer()
                },
                onCancelClick = {
                    reportViewModel.closeDrawer()
                },
                onDismissRequest = {
                    reportViewModel.closeDrawer()
                }
            )

            ComposeMembersBottomSheet(
                modifier = Modifier.height(defaultBottomSheetHeight.dp),
                viewModel = membersBottomSheetViewModel,
                onDismissRequest = {
                    membersBottomSheetViewModel.closeDrawer()
                },
                onExtendClick = { tab, user ->
                    memberMenuViewModel.user = user
                    memberMenuViewModel.setMenuList(context, tab)
                    memberMenuViewModel.openDrawer()
                    membersBottomSheetViewModel.closeDrawer()
                },
                onSearchClick = onMemberSheetSearchClick
            )

            ComposeMenuBottomSheet(
                viewModel = memberMenuViewModel,
                onListItemClick = { index,item ->
                    ChatLog.d(TAG," default item: $index ${item.title}")
                    when(index){
                        0 -> {
                            if (item.title == context.getString(R.string.menu_item_mute)){
                                memberListViewModel.muteUser(memberMenuViewModel.user.userId,
                                    onSuccess = {
                                        memberMenuViewModel.closeDrawer()
                                    },
                                    onError = {code, error ->
                                        memberMenuViewModel.closeDrawer()
                                    }
                                )
                            }else if (item.title == context.getString(R.string.menu_item_unmute)){
                                memberListViewModel.unmuteUser(memberMenuViewModel.user.userId,
                                    onSuccess = {
                                        memberMenuViewModel.closeDrawer()
                                    },
                                    onError = {code, error ->
                                        memberMenuViewModel.closeDrawer()
                                    }
                                )
                            }
                        }
                        1 -> {
                            if (item.title == context.getString(R.string.menu_item_remove)){
                                dialogViewModel.title = context.getString(R.string.dialog_title_remove_user, memberMenuViewModel.user.nickName)
                                dialogViewModel.showCancel = true
                                dialogViewModel.showDialog()
                            }
                        }
                    }
                },
                onDismissRequest = {
                    memberMenuViewModel.closeDrawer()
                }
            )

            SimpleDialog(
                viewModel = dialogViewModel,
                onConfirmClick = {
                    memberListViewModel.removeUser(memberMenuViewModel.user.userId,
                        onSuccess = {
                            memberMenuViewModel.closeDrawer()
                        },
                        onError = {code, error ->
                            memberMenuViewModel.closeDrawer()
                        }
                    )
                    dialogViewModel.dismissDialog()
                },
                onCancelClick = {
                    dialogViewModel.dismissDialog()
                },
                properties = DialogProperties(
                    dismissOnClickOutside = false,
                    dismissOnBackPress = false
                )
            )

            ComposeGiftList(
                modifier = Modifier
                    .wrapContentWidth()
                    .height(84.dp)
                    .padding(bottom = 4.dp)
                    .constrainAs(giftList) {
                        bottom.linkTo(msgList.top)
                    },
                viewModel = giftListViewModel,
            )

            ComposeChatMessageList(
                viewModel = messageListViewModel,
                modifier = Modifier
                    .constrainAs(msgList) {
                        bottom.linkTo(bottomBar.top)
                    }
                    .size(296.dp, 164.dp),
                onLongItemClick = { index,item->
                    ChatLog.d(TAG,"onLongItemClick $index $item")
                    if (item is ComposeMessageItemState){
                        reportViewModel.setReportMsgId(item.message.msgId)
                        messageItemMenuViewModel.setSelectedBean(item.message)
                        messageItemMenuViewModel.openDrawer()
                    }
                }
            )

            ComposeChatBottomBar(
                modifier = Modifier
                    .constrainAs(bottomBar) {
                        bottom.linkTo(parent.bottom)
                    }
                    .fillMaxWidth()
                    .wrapContentHeight(),
                viewModel = chatBottomBarViewModel,
                showInput = isShowInput.value,
                onSendMessage = { input->
                    ChatLog.d(TAG,"onSendMessage")
                    messageListViewModel.sendTextMessage(input)
                },
                onMenuClick = {
                    ChatLog.d(TAG,"onMenuClick: tag: $it")
                    if (it == 0){
                        giftBottomSheetViewModel.openDrawer()
                    } else if (it == 1){
                        membersBottomSheetViewModel.openDrawer()
                    }
                },
                onInputClick = {
                    ChatLog.d(TAG,"onInputClick: ")
                    isShowInput.value = true
                }
            )

        }
    }
}

@Composable
fun ShowComposeMenuDrawer(
    menuViewModel: MessageMenuViewModel,
    memberListViewModel: MemberListViewModel,
    reportViewModel: ComposeReportViewModel,
    messageListViewModel: MessageListViewModel,
    onMessageMenuClick: ((Int, UIComposeSheetItem) -> Unit)? = null
){
    ComposeMenuBottomSheet(
        viewModel = menuViewModel,
        onListItemClick = { index, menu ->
            onMessageMenuClick?.let { it.invoke(index, menu) }
                ?: run {
                    ChatLog.d("apex", " default item: $index ${menu.title}")
                    when (menu.id) {
                        R.id.action_menu_translate -> {
                            (menuViewModel.getSelectedBean() as ChatMessage).let {
                                    message ->
                                messageListViewModel.translateMessage(message)
                            }

                        }
                        R.id.action_menu_delete -> {
                            (menuViewModel.getSelectedBean() as ChatMessage).let {
                                    message ->
                                messageListViewModel.removeMessage(message)
                            }
                            menuViewModel.closeDrawer()
                        }
                        R.id.action_menu_remove -> {
                            (menuViewModel.getSelectedBean() as ChatMessage).let {
                                    message ->
                                if (ChatroomUIKitClient.getInstance().isCurrentRoomOwner()){
                                    memberListViewModel.removeUser(message.from)
                                }
                            }

                        }
                        R.id.action_menu_report -> { reportViewModel.openDrawer() }
                    }
                }
        },
        onDismissRequest = {
            menuViewModel.closeDrawer()
        }
    )
}

fun defaultMessageListViewModelFactory(
    context: Context,
    roomId: String,
    service: UIChatroomService
): MessagesViewModelFactory {
    return MessagesViewModelFactory(context, roomId = roomId, service = service)
}

fun defaultMenuViewModelFactory(
    isDarkTheme: Boolean? = ChatroomUIKitClient.getInstance().getContext().getCurrentTheme(),
    title:String = "",
    menuList: List<UIComposeSheetItem> = emptyList(),
    isShowTitle:Boolean = true,
    isShowCancel:Boolean = true,
): MenuViewModelFactory {
    return MenuViewModelFactory(isDarkTheme, title, menuList, isShowTitle, isShowCancel)
}

fun defaultMembersViewModelFactory(
    roomId: String,
    service: UIChatroomService,
    isRoomAdmin: Boolean,
    pageSize: Int = 10
): MemberViewModelFactory {
    return MemberViewModelFactory(roomId, service, isRoomAdmin, pageSize)
}

fun defaultReportViewModelFactory(
    service: UIChatroomService
): ReportViewModelFactory {
    return ReportViewModelFactory(service = service)
}



