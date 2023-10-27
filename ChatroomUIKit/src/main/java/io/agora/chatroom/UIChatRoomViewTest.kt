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
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import io.agora.CallBack
import io.agora.chatroom.compose.chatbottombar.ComposeChatBottomBar
import io.agora.chatroom.compose.chatmessagelist.ComposeChatMessageList
import io.agora.chatroom.compose.dialog.SimpleDialog
import io.agora.chatroom.compose.chatmessagelist.ComposeMessageItemState
import io.agora.chatroom.compose.drawer.ComposeMenuBottomSheet
import io.agora.chatroom.compose.gift.ComposeGiftBottomSheet
import io.agora.chatroom.compose.gift.ComposeGiftItemState
import io.agora.chatroom.compose.gift.ComposeGiftList
import io.agora.chatroom.compose.member.ComposeMembersBottomSheet
import io.agora.chatroom.compose.report.ComposeMessageReport
import io.agora.chatroom.data.LanguageType
import io.agora.chatroom.data.initialLongClickMenu
import io.agora.chatroom.data.reportTagList
import io.agora.chatroom.model.report.UIReportEntity
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
import io.agora.chatroom.uikit.R
import io.agora.chatroom.uikit.databinding.ActivityUiChatroomTestBinding
import io.agora.chatroom.viewmodel.dialog.DialogViewModel
import io.agora.chatroom.viewmodel.gift.ComposeGiftListViewModel
import io.agora.chatroom.viewmodel.gift.ComposeGiftSheetViewModel
import io.agora.chatroom.viewmodel.member.MemberListViewModel
import io.agora.chatroom.viewmodel.member.MemberViewModelFactory
import io.agora.chatroom.viewmodel.member.MembersBottomSheetViewModel
import io.agora.chatroom.viewmodel.member.MutedListViewModel
import io.agora.chatroom.viewmodel.menu.MenuViewModel
import io.agora.chatroom.viewmodel.menu.MenuViewModelFactory
import io.agora.chatroom.viewmodel.menu.RoomMemberMenuViewModel
import io.agora.chatroom.viewmodel.messages.MessageChatBarViewModel
import io.agora.chatroom.viewmodel.messages.MessageListViewModel
import io.agora.chatroom.viewmodel.messages.MessagesViewModelFactory
import io.agora.chatroom.viewmodel.report.ComposeReportViewModel

class UIChatRoomViewTest : FrameLayout, ChatroomChangeListener, GiftReceiveListener {
    private val mRoomViewBinding = ActivityUiChatroomTestBinding.inflate(LayoutInflater.from(context))
    private val inputField: MutableState<Boolean> = mutableStateOf(false)
    private val closeMemberSheet: MutableState<Boolean> = mutableStateOf(false)
    private lateinit var listViewModel:MessageListViewModel
    private lateinit var giftListViewModel:ComposeGiftListViewModel
    private lateinit var bottomBarViewModel:MessageChatBarViewModel
    private lateinit var giftViewModel:ComposeGiftSheetViewModel
    private lateinit var service:UIChatroomService
    private val launcherToSearch: ActivityResultLauncher<Intent>   =
        (context as ComponentActivity).registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            closeMemberSheet.value = result.resultCode == Activity.RESULT_OK
        }

    private val memberMenuViewModel by lazy {
        if (context is ComponentActivity) {
            ViewModelProvider(context as ComponentActivity, MenuViewModelFactory())[RoomMemberMenuViewModel::class.java]
        } else {
            RoomMemberMenuViewModel()
        }
    }
    private var reportViewModel: ComposeReportViewModel = ComposeReportViewModel(reportTag = reportTagList)
    private var msgItemMenuViewModel: MenuViewModel = MenuViewModel(menuList = initialLongClickMenu, isExpanded = true)
    private var longClickIndex:Int = -1
    private lateinit var longClickMsg:ChatMessage
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

        if (ChatClient.getInstance().isLoggedInBefore && ChatClient.getInstance().options.autoLogin) {
            joinChatroom(roomId, onSuccess = {
                ChatLog.e("apex","joinChatroom onSuccess")
            }, onError = { code, error ->
                (context as Activity).finish()
            })
        } else {
            // 测试登录代码
            ChatClient.getInstance().login("apex1","1",object : CallBack {
                override fun onSuccess() {
                    Log.e("apex","login onSuccess")
                    ChatroomUIKitClient.getInstance().getChatroomUser().setUserInfo(
                        "apex1", UserEntity(userId = "apex1", nickName = "大威天龙")
                    )
                    joinChatroom(roomId,onSuccess = {
                        ChatLog.e("apex","joinChatroom onSuccess")
                    }, onError = { code, error ->
                        (context as Activity).finish()
                    })
                }

                override fun onError(code: Int, error: String?) {
                    Log.e("apex","login onError $code  $error")
                    (context as Activity).finish()
                }
            })
        }

        loadComponent(roomId, service)

    }

    private fun loadComponent(
        roomId: String,
        service: UIChatroomService
    ) {
        mRoomViewBinding.composeChatroom.setContent {

            val factory = buildViewModelFactory(
                context = context,
                service = service
            )

            listViewModel = viewModel(MessageListViewModel::class.java, factory = factory)
            bottomBarViewModel = viewModel(MessageChatBarViewModel::class.java, factory = factory)
            giftViewModel = viewModel(ComposeGiftSheetViewModel::class.java, factory = factory)
            giftListViewModel = ComposeGiftListViewModel()
            giftListViewModel.openAutoClear()
            giftListViewModel.setAutoClearTime(3000L)

            ChatroomUIKitClient.getInstance().getContext().setUseGiftsInList(true)

            val membersBottomSheet = MembersBottomSheetViewModel(
                                        roomId = roomId,
                                        roomService = service,
                                        isAdmin = ChatroomUIKitClient.getInstance().getContext().getCurrentRoomInfo().roomOwner?.userId == ChatClient.getInstance().currentUser)
            val memberListViewModel = viewModel(MemberListViewModel::class.java, factory = MemberViewModelFactory(context = LocalContext.current, roomId = roomId, service = service))
            val dialogViewModel = DialogViewModel()

            val isShowInput by inputField


            ChatroomUIKitTheme{
                ConstraintLayout(modifier = Modifier
                    .fillMaxSize()
                    .clickable {
                        inputField.value = false
                    }
                ) {
                    val defaultBottomSheetHeight = LocalConfiguration.current.screenHeightDp/2
                    val (giftList, msgList, bottomBar) = createRefs()
                    ComposeGiftBottomSheet(
                        modifier = Modifier
                            .height((LocalConfiguration.current.screenHeightDp/2).dp),
                        viewModel = giftViewModel,
                        containerColor = ChatroomUIKitTheme.colors.background,
                        screenContent = {},
                        onGiftItemClick = {
                            service.getGiftService().sendGift(it,
                                onSuccess = {msg ->
                                    if (ChatroomUIKitClient.getInstance().getContext().getUseGiftsInMsg()){
                                        listViewModel.addGiftMessageByIndex(message = msg, gift = it)
                                    }else{
                                        giftListViewModel.addDateToIndex(data=ComposeGiftItemState(it))
                                    }
                                },
                                onError = {code, error ->  }
                            )
                        },
                        onDismissRequest = {
                            giftViewModel.closeDrawer()
                        }
                    )
                    ShowComposeMenuDrawer(memberListViewModel,msgItemMenuViewModel,reportViewModel)
                    ComposeMessageReport(
                        modifier = Modifier.height((LocalConfiguration.current.screenHeightDp/2).dp),
                        containerColor = ChatroomUIKitTheme.colors.background,
                        viewModel = reportViewModel,
                        onConfirmClick = {
                            Log.e("apex","com $it")
                            reportMessage(it)
                            reportViewModel.closeDrawer()
                        },
                        onCancelClick = {
                            Log.e("apex"," onCancelClick ")
                            reportViewModel.closeDrawer()
                        },
                        onDismissRequest = {
                            reportViewModel.closeDrawer()
                        }
                    )

                    ComposeMembersBottomSheet(
                        modifier = Modifier.height(defaultBottomSheetHeight.dp),
                        viewModel = membersBottomSheet,
                        onDismissRequest = {
                            membersBottomSheet.closeDrawer()
                        },
                        onExtendClick = { tab, user ->
                            memberMenuViewModel.user = user
                            memberMenuViewModel.setMenuList(context, tab)
                            memberMenuViewModel.openDrawer()
                            membersBottomSheet.closeDrawer()
                        },
                        onSearchClick = { title ->
                            launcherToSearch.launch(UISearchActivity.createIntent(context, roomId, title))
                        }
                    )

                    ComposeMenuBottomSheet(
                        viewModel = memberMenuViewModel,
                        onListItemClick = { index,item ->
                            Log.e("apex"," default item: $index ${item.title}")
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
                        viewModel = listViewModel,
                        modifier = Modifier
                            .constrainAs(msgList) {
                                bottom.linkTo(bottomBar.top)
                            }
                            .size(296.dp, 164.dp),
                        onLongItemClick = { index,item->
                            longClickIndex = index
                            Log.e("apex","onLongItemClick $index $item")
                            if (item is ComposeMessageItemState){
                                reportViewModel.setReportMsgId(item.message.msgId)
                                longClickMsg = item.message
                                msgItemMenuViewModel.openDrawer()
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
                        viewModel = bottomBarViewModel,
                        showInput = isShowInput,
                        onSendMessage = { input->
                            Log.e("apex","onSendMessage")
                            service.getChatService().sendTextMessage(
                                message = input,
                                roomId = roomId,//service.getRoomInfo().roomId
                                onSuccess = {
                                    listViewModel.addTextMessageByIndex(message = it)
                                },
                                onError = {code, error ->

                                })
                        },
                        onMenuClick = {
                            Log.e("apex","onMenuClick: tag: $it")
                            if (it == 0){
                                giftViewModel.openDrawer()
                            } else if (it == 1){
                                membersBottomSheet.openDrawer()
                            }
                        },
                        onInputClick = {
                            Log.e("apex","onInputClick: ")
                            inputField.value = true
                        }
                    )

                    LaunchedEffect(closeMemberSheet.value) {
                        if (closeMemberSheet.value){
                            membersBottomSheet.closeDrawer()
                        }
                    }
                }
            }
        }
    }

    private fun buildViewModelFactory(
        context: Context,
        service: UIChatroomService,
        showDateSeparators: Boolean = true,
        showLabel: Boolean = true,
        showAvatar: Boolean = true,
    ): MessagesViewModelFactory {
        return MessagesViewModelFactory(
            context = context,
            service = service,
            showDateSeparators = showDateSeparators,
            showLabel = showLabel,
            showAvatar = showAvatar,
        )
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()

        service.getChatService().unbindListener(this)
    }

    override fun onMessageReceived(message: ChatMessage) {
        super.onMessageReceived(message)
        listViewModel.addTextMessageByIndex(message = message)
    }

    override fun onGiftReceived(roomId: String, gift: GiftEntityProtocol?, message: ChatMessage) {
        super.onGiftReceived(roomId, gift, message)
        if (ChatroomUIKitClient.getInstance().getContext().getUseGiftsInMsg()){
            gift?.let {
                listViewModel.addGiftMessageByIndex(message = message, gift = it)
            }
        }else{
            gift?.let {
                giftListViewModel.addDateToIndex(data = ComposeGiftItemState(it))
            }
        }
    }

    override fun onUserJoined(roomId: String, userId: String) {
        Log.e("apex","onUserJoined $roomId  - $userId")
        listViewModel.addJoinedMessageByIndex(
            message = ChatroomUIKitClient.getInstance().insertJoinedMessage(roomId,userId)
        )
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
        service.getChatService().joinChatroom(roomId,"apex1"
            , onSuccess = {
//                UIChatroomCacheManager.cacheManager.saveOwner(it.owner)
//                UIChatroomCacheManager.cacheManager.saveAdminList(it.adminList)
                listViewModel.addJoinedMessageByIndex(
                    message = ChatroomUIKitClient.getInstance().insertJoinedMessage(
                        roomId,ChatroomUIKitClient.getInstance().getCurrentUser().userId
                    )
                )
                val viewModel = ViewModelProvider(context as ComponentActivity, MemberViewModelFactory(context = context, roomId = roomId, service = service))[MutedListViewModel::class.java]
                viewModel.fetchMuteList { code, error ->  }
                onSuccess.invoke()
            }
            , onError = {errorCode,result->
                Log.e("apex","joinChatroom  193314355740675 onError $errorCode $result")
                onError.invoke(errorCode,result)
            }
        )
    }

    @Composable
    fun ShowComposeMenuDrawer(
        memberListViewModel: MemberListViewModel,
        menuViewModel:MenuViewModel,
        reportViewModel:ComposeReportViewModel
    ){
        ComposeMenuBottomSheet(
            viewModel = menuViewModel,
            onListItemClick = { index,item ->
                Log.e("apex"," default item: $index ${item.title}")
                when (index) {
                    0 -> { translateMsg() }
                    1 -> {
                        removeMessage()
                        menuViewModel.closeDrawer()
                    }
                    2 -> { muteMember(memberListViewModel) }
                    3 -> { reportViewModel.openDrawer() }
                }
            },
            onDismissRequest = {
                menuViewModel.closeDrawer()
                Log.e("apex"," onClick Cancel ")
            }
        )
    }

    private fun reportMessage(report:UIReportEntity){
        val reportMsgId = reportViewModel.reportMsgId.value
        service.getChatService().reportMessage(
            reportMsgId,report.tag,report.reason,
            onSuccess = {
                Log.e("apex","reportMessage onSuccess $reportMsgId - $report.tag - $report.reason")
            },
            onError = { code, error ->
                Log.e("apex","reportMessage onError $code $error")
            }
        )
    }

    private fun removeMessage(){
        listViewModel.removeMessageByIndex(longClickIndex)
    }

    private fun muteMember(memberListViewModel:MemberListViewModel){
        if (ChatroomUIKitClient.getInstance().isCurrentRoomOwner()){
            memberListViewModel.muteUser(longClickMsg.from,
                onSuccess = {
                    Log.e("apex","muteMember onSuccess ${longClickMsg.from}")
                },
                onError = {code, error ->
                    Log.e("apex","muteMember onError $code $error")
                }
            )
        }else{
            Log.e("apex","muteMember You don't have permission to operate the mute")
        }
    }

    private fun translateMsg(){
        longClickMsg.let {
            service.getChatService().translateTextMessage(it,
                onSuccess = {
                    Log.e("apex","translateTextMessage onSuccess ")
                    listViewModel.updateTextMessage(longClickIndex,longClickMsg)
                },
                onError = { code, error ->
                    Log.e("apex","translateTextMessage onError $code $error ")
                }
            )
        }

    }

}