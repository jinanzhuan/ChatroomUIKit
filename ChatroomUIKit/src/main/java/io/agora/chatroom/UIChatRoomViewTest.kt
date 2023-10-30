package io.agora.chatroom

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import io.agora.CallBack
import io.agora.chatroom.compose.ComposeChatScreen
import io.agora.chatroom.compose.LoadingIndicator
import io.agora.chatroom.compose.gift.ComposeGiftItemState
import io.agora.chatroom.service.ChatClient
import io.agora.chatroom.service.ChatLog
import io.agora.chatroom.service.ChatMessage
import io.agora.chatroom.service.ChatroomChangeListener
import io.agora.chatroom.service.GiftEntityProtocol
import io.agora.chatroom.service.GiftReceiveListener
import io.agora.chatroom.service.OnError
import io.agora.chatroom.service.OnSuccess
import io.agora.chatroom.service.UserEntity
import io.agora.chatroom.theme.ChatroomUIKitTheme
import io.agora.chatroom.ui.UIChatroomService
import io.agora.chatroom.ui.UISearchActivity
import io.agora.chatroom.uikit.databinding.ActivityUiChatroomTestBinding
import io.agora.chatroom.viewmodel.gift.ComposeGiftListViewModel
import io.agora.chatroom.viewmodel.member.MemberViewModelFactory
import io.agora.chatroom.viewmodel.member.MembersBottomSheetViewModel
import io.agora.chatroom.viewmodel.member.MutedListViewModel
import io.agora.chatroom.viewmodel.messages.MessageListViewModel

class UIChatRoomViewTest : FrameLayout, ChatroomChangeListener, GiftReceiveListener {
    private val mRoomViewBinding = ActivityUiChatroomTestBinding.inflate(LayoutInflater.from(context))
    private val closeMemberSheet: MutableState<Boolean> = mutableStateOf(false)
    private val isLoginRoom by lazy { mutableStateOf(true) }
    private lateinit var listViewModel:MessageListViewModel
    private lateinit var service:UIChatroomService
    private val launcherToSearch: ActivityResultLauncher<Intent>   =
        (context as ComponentActivity).registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            closeMemberSheet.value = result.resultCode == Activity.RESULT_OK
        }

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        addView(mRoomViewBinding.root)
    }

    fun bindService(service: UIChatroomService?){
        if (service == null) return
        this.service = service
        val roomId = service.getRoomInfo().roomId

        service.getChatService().bindListener(this)
        service.getGiftService().bindGiftListener(this)

        joinChatroom(roomId, onSuccess = {
            isLoginRoom.value = false
            ChatLog.e("apex","joinChatroom onSuccess")
        }, onError = { code, error ->
            Log.e("apex","joinChatroom onError $code $error")
        })

        loadComponent(roomId, service)

    }

    @Composable
    private fun loginToRoom() {
        ChatroomUIKitTheme {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                LoadingIndicator()
            }
        }
    }

    private fun loadComponent(
        roomId: String,
        service: UIChatroomService
    ) {
        mRoomViewBinding.composeChatroom.setContent {

            ChatroomUIKitTheme{

                if (isLoginRoom.value) {
                    loginToRoom()
                } else {
                    val membersBottomSheetViewModel = viewModel(MembersBottomSheetViewModel::class.java,
                        factory = MemberViewModelFactory(roomId = roomId, service = service,
                            isRoomAdmin = ChatroomUIKitClient.getInstance().getContext().getCurrentRoomInfo().roomOwner?.userId == ChatClient.getInstance().currentUser))

                    ComposeChatScreen(
                        roomId = roomId,
                        service = service,
                        membersBottomSheetViewModel = membersBottomSheetViewModel,
                        onMemberSheetSearchClick = {
                                tab->
                            launcherToSearch.launch(UISearchActivity.createIntent(context, roomId, tab))
                        }
                    )

                    LaunchedEffect(closeMemberSheet.value) {
                        if (closeMemberSheet.value){
                            membersBottomSheetViewModel.closeDrawer()
                        }
                    }
                }
            }
        }
    }
//    private fun loadComponent(
//        roomId: String,
//        service: UIChatroomService
//    ) {
//        mRoomViewBinding.composeChatroom.setContent {
//
//            val factory = buildViewModelFactory(
//                context = context,
//                service = service
//            )
//
//            listViewModel = viewModel(MessageListViewModel::class.java, factory = factory)
//            bottomBarViewModel = viewModel(MessageChatBarViewModel::class.java, factory = factory)
//            giftViewModel = viewModel(ComposeGiftSheetViewModel::class.java, factory = factory)
//            giftListViewModel = ComposeGiftListViewModel()
//            giftListViewModel.openAutoClear()
//            giftListViewModel.setAutoClearTime(3000L)
//
//            ChatroomUIKitClient.getInstance().getContext().setUseGiftsInList(true)
//
//            val membersBottomSheet = MembersBottomSheetViewModel(
//                                        roomId = roomId,
//                                        roomService = service,
//                                        isAdmin = ChatroomUIKitClient.getInstance().getContext().getCurrentRoomInfo().roomOwner?.userId == ChatClient.getInstance().currentUser)
//            val memberListViewModel = viewModel(MemberListViewModel::class.java, factory = MemberViewModelFactory(roomId = roomId, service = service, isRoomAdmin = ChatroomUIKitClient.getInstance().getContext().getCurrentRoomInfo().roomOwner?.userId == ChatClient.getInstance().currentUser))
//            val dialogViewModel = DialogViewModel()
//
//            val isShowInput by inputField
//
//
//            ChatroomUIKitTheme{
//                ConstraintLayout(modifier = Modifier
//                    .fillMaxSize()
//                    .clickable {
//                        inputField.value = false
//                    }
//                ) {
//                    val defaultBottomSheetHeight = LocalConfiguration.current.screenHeightDp/2
//                    val (giftList, msgList, bottomBar) = createRefs()
//                    ComposeGiftBottomSheet(
//                        modifier = Modifier
//                            .height((LocalConfiguration.current.screenHeightDp/2).dp),
//                        viewModel = giftViewModel,
//                        containerColor = ChatroomUIKitTheme.colors.background,
//                        screenContent = {},
//                        onGiftItemClick = {
//                            service.getGiftService().sendGift(it,
//                                onSuccess = {msg ->
//                                    if (ChatroomUIKitClient.getInstance().getContext().getUseGiftsInMsg()){
//                                        listViewModel.addGiftMessageByIndex(message = msg, gift = it)
//                                    }else{
//                                        giftListViewModel.addDateToIndex(data=ComposeGiftItemState(it))
//                                    }
//                                },
//                                onError = {code, error ->  }
//                            )
//                        },
//                        onDismissRequest = {
//                            giftViewModel.closeDrawer()
//                        }
//                    )
//                    ShowComposeMenuDrawer(memberListViewModel,msgItemMenuViewModel,reportViewModel)
//                    ComposeMessageReport(
//                        modifier = Modifier.height((LocalConfiguration.current.screenHeightDp/2).dp),
//                        containerColor = ChatroomUIKitTheme.colors.background,
//                        viewModel = reportViewModel,
//                        onConfirmClick = {
//                            Log.e("apex","com $it")
//                            reportMessage(it)
//                            reportViewModel.closeDrawer()
//                        },
//                        onCancelClick = {
//                            Log.e("apex"," onCancelClick ")
//                            reportViewModel.closeDrawer()
//                        },
//                        onDismissRequest = {
//                            reportViewModel.closeDrawer()
//                        }
//                    )
//
//                    ComposeMembersBottomSheet(
//                        modifier = Modifier.height(defaultBottomSheetHeight.dp),
//                        viewModel = membersBottomSheet,
//                        onDismissRequest = {
//                            membersBottomSheet.closeDrawer()
//                        },
//                        onExtendClick = { tab, user ->
//                            memberMenuViewModel.user = user
//                            memberMenuViewModel.setMenuList(context, tab)
//                            memberMenuViewModel.openDrawer()
//                            membersBottomSheet.closeDrawer()
//                        },
//                        onSearchClick = { title ->
//                            launcherToSearch.launch(UISearchActivity.createIntent(context, roomId, title))
//                        }
//                    )
//
//                    ComposeMenuBottomSheet(
//                        viewModel = memberMenuViewModel,
//                        onListItemClick = { index,item ->
//                            Log.e("apex"," default item: $index ${item.title}")
//                            when(index){
//                                0 -> {
//                                    if (item.title == context.getString(R.string.menu_item_mute)){
//                                        memberListViewModel.muteUser(memberMenuViewModel.user.userId,
//                                            onSuccess = {
//                                                memberMenuViewModel.closeDrawer()
//                                            },
//                                            onError = {code, error ->
//                                                memberMenuViewModel.closeDrawer()
//                                            }
//                                        )
//                                    }else if (item.title == context.getString(R.string.menu_item_unmute)){
//                                        memberListViewModel.unmuteUser(memberMenuViewModel.user.userId,
//                                            onSuccess = {
//                                                memberMenuViewModel.closeDrawer()
//                                            },
//                                            onError = {code, error ->
//                                                memberMenuViewModel.closeDrawer()
//                                            }
//                                        )
//                                    }
//                                }
//                                1 -> {
//                                    if (item.title == context.getString(R.string.menu_item_remove)){
//                                        dialogViewModel.title = context.getString(R.string.dialog_title_remove_user, memberMenuViewModel.user.nickName)
//                                        dialogViewModel.showCancel = true
//                                        dialogViewModel.showDialog()
//                                    }
//                                }
//                            }
//                        },
//                        onDismissRequest = {
//                            memberMenuViewModel.closeDrawer()
//                        }
//                    )
//
//                    SimpleDialog(
//                        viewModel = dialogViewModel,
//                        onConfirmClick = {
//                            memberListViewModel.removeUser(memberMenuViewModel.user.userId,
//                                onSuccess = {
//                                    memberMenuViewModel.closeDrawer()
//                                },
//                                onError = {code, error ->
//                                    memberMenuViewModel.closeDrawer()
//                                }
//                            )
//                            dialogViewModel.dismissDialog()
//                        },
//                        onCancelClick = {
//                            dialogViewModel.dismissDialog()
//                        },
//                        properties = DialogProperties(
//                            dismissOnClickOutside = false,
//                            dismissOnBackPress = false
//                        )
//                    )
//
//                    ComposeGiftList(
//                        modifier = Modifier
//                            .wrapContentWidth()
//                            .height(84.dp)
//                            .padding(bottom = 4.dp)
//                            .constrainAs(giftList) {
//                                bottom.linkTo(msgList.top)
//                            },
//                        viewModel = giftListViewModel,
//                    )
//
//                    ComposeChatMessageList(
//                        viewModel = listViewModel,
//                        modifier = Modifier
//                            .constrainAs(msgList) {
//                                bottom.linkTo(bottomBar.top)
//                            }
//                            .size(296.dp, 164.dp),
//                        onLongItemClick = { index,item->
//                            longClickIndex = index
//                            Log.e("apex","onLongItemClick $index $item")
//                            if (item is ComposeMessageItemState){
//                                reportViewModel.setReportMsgId(item.message.msgId)
//                                longClickMsg = item.message
//                                msgItemMenuViewModel.openDrawer()
//                            }
//                        }
//                    )
//
//                    ComposeChatBottomBar(
//                        modifier = Modifier
//                            .constrainAs(bottomBar) {
//                                bottom.linkTo(parent.bottom)
//                            }
//                            .fillMaxWidth()
//                            .wrapContentHeight(),
//                        viewModel = bottomBarViewModel,
//                        showInput = isShowInput,
//                        onSendMessage = { input->
//                            Log.e("apex","onSendMessage")
//                            service.getChatService().sendTextMessage(
//                                message = input,
//                                roomId = roomId,//service.getRoomInfo().roomId
//                                onSuccess = {
//                                    listViewModel.addTextMessageByIndex(message = it)
//                                },
//                                onError = {code, error ->
//
//                                })
//                        },
//                        onMenuClick = {
//                            Log.e("apex","onMenuClick: tag: $it")
//                            if (it == 0){
//                                giftViewModel.openDrawer()
//                            } else if (it == 1){
//                                membersBottomSheet.openDrawer()
//                            }
//                        },
//                        onInputClick = {
//                            Log.e("apex","onInputClick: ")
//                            inputField.value = true
//                        }
//                    )
//
//                    LaunchedEffect(closeMemberSheet.value) {
//                        if (closeMemberSheet.value){
//                            membersBottomSheet.closeDrawer()
//                        }
//                    }
//                }
//            }
//        }
//    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()

        service.getChatService().unbindListener(this)
    }


    override fun onUserJoined(roomId: String, userId: String) {
        Log.e("apex","onUserJoined $roomId  - $userId")
        ChatroomUIKitClient.getInstance().getCacheManager().saveRoomMemberList(roomId, arrayListOf(userId))
    }

    override fun onUserLeft(roomId: String, userId: String) {
        super.onUserLeft(roomId, userId)
        ChatroomUIKitClient.getInstance().getCacheManager().removeRoomMember(roomId, userId)
    }

    override fun onUserMuted(roomId: String, userId: String) {
        super.onUserMuted(roomId, userId)
        ChatroomUIKitClient.getInstance().getCacheManager().saveRoomMuteList(roomId, arrayListOf(userId))
        ChatroomUIKitClient.getInstance().getCacheManager().removeRoomMember(roomId, userId)
    }

    override fun onUserUnmuted(roomId: String, userId: String) {
        super.onUserUnmuted(roomId, userId)
        ChatroomUIKitClient.getInstance().getCacheManager().removeRoomMuteMember(roomId, userId)
        ChatroomUIKitClient.getInstance().getCacheManager().saveRoomMemberList(roomId, arrayListOf(userId))
    }

    override fun onUserBeKicked(roomId: String, userId: String) {
        super.onUserBeKicked(roomId, userId)
        ChatroomUIKitClient.getInstance().getCacheManager().removeRoomMember(roomId, userId)
    }


    fun joinChatroom(roomId:String, onSuccess: OnSuccess = {}, onError: OnError = {_, _ ->}){
        Log.e("apex","joinChatroom roomId: $roomId userId: ${ChatClient.getInstance().currentUser}")
        service.getChatService().joinChatroom(roomId, ChatClient.getInstance().currentUser
            , onSuccess = {
                listViewModel.addJoinedMessageByIndex(
                    message = ChatroomUIKitClient.getInstance().insertJoinedMessage(
                        roomId,ChatroomUIKitClient.getInstance().getCurrentUser().userId
                    )
                )
                val viewModel = ViewModelProvider(context as ComponentActivity, MemberViewModelFactory( roomId = roomId, service = service, isRoomAdmin = ChatroomUIKitClient.getInstance().isCurrentRoomOwner()))[MutedListViewModel::class.java]
                viewModel.fetchMuteList { code, error ->  }
                onSuccess.invoke()
            }
            , onError = {errorCode,result->
                Log.e("apex","joinChatroom  193314355740675 onError $errorCode $result")
                onError.invoke(errorCode,result)
            }
        )
    }


}