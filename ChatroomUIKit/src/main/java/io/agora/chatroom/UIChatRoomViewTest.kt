package io.agora.chatroom

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.activity.ComponentActivity
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import io.agora.CallBack
import io.agora.chat.ChatClient
import io.agora.chatroom.service.ChatMessage
import io.agora.chatroom.service.ChatroomChangeListener
import io.agora.chatroom.ui.UIChatroomService
import io.agora.chatroom.compose.chatbottombar.ComposeChatBottomBar
import io.agora.chatroom.compose.chatmessagelist.ComposeChatMessageList
import io.agora.chatroom.compose.drawer.ComposeMenuBottomSheet
import io.agora.chatroom.compose.gift.ComposeGiftBottomSheet
import io.agora.chatroom.compose.gift.ComposeGiftItemState
import io.agora.chatroom.compose.gift.ComposeGiftList
import io.agora.chatroom.compose.member.ComposeMembersBottomSheet
import io.agora.chatroom.model.UIComposeSheetItem
import io.agora.chatroom.service.GiftEntity
import io.agora.chatroom.service.GiftReceiveListener
import io.agora.chatroom.service.cache.UIChatroomCacheManager
import io.agora.chatroom.theme.ChatroomUIKitTheme
import io.agora.chatroom.theme.primaryColor80
import io.agora.chatroom.theme.secondaryColor80
import io.agora.chatroom.uikit.R
import io.agora.chatroom.viewmodel.messages.MessageChatBarViewModel
import io.agora.chatroom.viewmodel.messages.MessageListViewModel
import io.agora.chatroom.viewmodel.messages.MessagesViewModelFactory
import io.agora.chatroom.uikit.databinding.ActivityUiChatroomTestBinding
import io.agora.chatroom.viewmodel.gift.ComposeGiftListViewModel
import io.agora.chatroom.viewmodel.gift.ComposeGiftSheetViewModel
import io.agora.chatroom.viewmodel.member.MemberListViewModel
import io.agora.chatroom.viewmodel.member.MemberViewModelFactory
import io.agora.chatroom.viewmodel.member.MembersBottomSheetViewModel
import io.agora.chatroom.viewmodel.menu.MenuViewModel
import io.agora.chatroom.viewmodel.menu.MenuViewModelFactory
import io.agora.chatroom.viewmodel.menu.RoomMemberMenuViewModel

class UIChatRoomViewTest : FrameLayout, ChatroomChangeListener, GiftReceiveListener {
    private val mRoomViewBinding = ActivityUiChatroomTestBinding.inflate(LayoutInflater.from(context))
    private val inputField: MutableState<Boolean> = mutableStateOf(false)
    private lateinit var listViewModel:MessageListViewModel
    private lateinit var giftListViewModel:ComposeGiftListViewModel
    private lateinit var bottomBarViewModel:MessageChatBarViewModel
    private lateinit var giftViewModel:ComposeGiftSheetViewModel
    private lateinit var service:UIChatroomService
    private val memberMenuViewModel by lazy {
        if (context is AppCompatActivity) {
            ViewModelProvider(context as ComponentActivity, MenuViewModelFactory())[RoomMemberMenuViewModel::class.java]
        } else {
            RoomMemberMenuViewModel()
        }
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

    fun bindService(service: UIChatroomService){
        this.service = service
        val roomId = service.getRoomInfo().roomId

        service.getChatService().bindListener(this)
        service.getGiftService().bindGiftListener(this)

        if (!ChatroomUIKitClient.shared.isLoginBefore()){
            ChatClient.getInstance().login("apex1","1",object : CallBack {
                override fun onSuccess() {
                    Log.e("apex","login onSuccess")
                    joinChatroom(roomId)
                }

                override fun onError(code: Int, error: String?) {
                    Log.e("apex","login onError $code  $error")
                }

            })
        }else{
            joinChatroom(roomId)
        }

        mRoomViewBinding.composeChatroom.setContent {

            val factory = buildViewModelFactory(
                context = context,
                service = service
            )

            listViewModel = viewModel(MessageListViewModel::class.java, factory = factory)
            bottomBarViewModel = viewModel(MessageChatBarViewModel::class.java, factory = factory)
            giftViewModel = viewModel(ComposeGiftSheetViewModel::class.java, factory = factory)
            giftListViewModel = ComposeGiftListViewModel()
            val membersBottomSheet = MembersBottomSheetViewModel(roomId = roomId, roomService = service, isAdmin = true)
            val memberListViewModel = viewModel(MemberListViewModel::class.java, factory = MemberViewModelFactory(context = LocalContext.current, roomId = roomId, service = service))

            val isShowInput by inputField


            ChatroomUIKitTheme{
                ConstraintLayout(modifier = Modifier
                    .fillMaxSize()
                    .clickable {
                        inputField.value = false
                    }
                ) {
                    val (giftList, msgList, bottomBar) = createRefs()
                    ComposeGiftBottomSheet(
                        modifier = Modifier.height((LocalConfiguration.current.screenHeightDp/2).dp),
                        viewModel = giftViewModel,
                        screenContent = {},
                        onGiftItemClick = {
                            Log.e("apex","ComposeGiftList GiftEntity $it")
                            service.getGiftService().sendGift(it,
                                onSuccess = {
                                    Log.e("apex","sendGift onSuccess")
                                    giftListViewModel.addData(ComposeGiftItemState(it))
                                },
                                onError = {code, error ->  }
                            )
                        },
                        onDismissRequest = {
                            giftViewModel.closeDrawer()
                        }
                    )

                    ComposeMembersBottomSheet(
                        modifier = Modifier.height((LocalConfiguration.current.screenHeightDp/2).dp),
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
                            Log.e("apex","ComposeMembersBottomSheet onSearchClick $title")
                            membersBottomSheet.closeDrawer()
                        },
                        onItemClick = { tab, user ->
                            Log.e("apex","ComposeMembersBottomSheet onItemClick $tab $user")}
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
                                        memberListViewModel.removeUser(memberMenuViewModel.user.userId,
                                            onSuccess = {
                                                memberMenuViewModel.closeDrawer()
                                            },
                                            onError = {code, error ->
                                                memberMenuViewModel.closeDrawer()
                                            }
                                        )
                                    }
                                }
                            }
                        },
                        onDismissRequest = {
                            memberMenuViewModel.closeDrawer()
                        }
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
                        onLongItemClick = { index,message->
                            Log.e("apex","onLongItemClick $index $message")
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
                                    listViewModel.addTextMessage(it)
                                },
                                onError = {code, error ->

                                })
                        },
                        onMenuClick = {
                            if (it == 0){
                                giftViewModel.openDrawer()
                                //membersBottomSheet.openDrawer()
                            }
                        },
                        onInputClick = {
                            Log.e("apex","onInputClick: ")
                            inputField.value = true
                        }
                    )
                }
            }
        }
    }

    private fun buildViewModelFactory(
        context: Context,
        service: UIChatroomService,
        showDateSeparators: Boolean = true,
        showLabel: Boolean = true,
        showGift: Boolean = true,
        showAvatar: Boolean = true,
        dateSeparatorColor: Color = secondaryColor80,
        nickNameColor: Color = primaryColor80,
    ): MessagesViewModelFactory {
        return MessagesViewModelFactory(
            context = context,
            service = service,
            showDateSeparators = showDateSeparators,
            showLabel = showLabel,
            showGift = showGift,
            showAvatar = showAvatar,
            dateSeparatorColor = dateSeparatorColor,
            nickNameColor = nickNameColor
        )
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()

        service.getChatService().unbindListener(this)
    }

    override fun onMessageReceived(message: ChatMessage) {
        super.onMessageReceived(message)
        listViewModel.addTextMessage(message)
    }

    override fun onGiftReceived(gift: GiftEntity) {
        super.onGiftReceived(gift)
        giftListViewModel.addData(ComposeGiftItemState(gift))
    }


    fun joinChatroom(roomId:String){
        service.getChatService().joinChatroom(roomId,"apex1"
            , onSuccess = {
                UIChatroomCacheManager.cacheManager.saveOwner(it.owner)
                UIChatroomCacheManager.cacheManager.saveAdminList(it.adminList)
                Log.e("apex","joinChatroom  193314355740675 onSuccess admin: ${it.adminList} owner: ${it.owner}")
            }
            , onError = {errorCode,result->
                Log.e("apex","joinChatroom  193314355740675 onError $errorCode $result")
            }
        )
    }

}