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
import io.agora.chatroom.viewmodel.messages.MessagesViewModelFactory

class UIChatRoomViewTest : FrameLayout, ChatroomChangeListener, GiftReceiveListener {
    private val mRoomViewBinding = ActivityUiChatroomTestBinding.inflate(LayoutInflater.from(context))
    private val closeMemberSheet: MutableState<Boolean> = mutableStateOf(false)
    private lateinit var service:UIChatroomService
    private lateinit var roomId:String
    private val isLoginRoom by lazy { mutableStateOf(true) }
    private val listViewModel by lazy {
        ViewModelProvider(context as ComponentActivity,
            factory = MessagesViewModelFactory(context = context, roomId = roomId, service = service))[MessageListViewModel::class.java]
    }

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
        roomId = service.getRoomInfo().roomId

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


    private fun joinChatroom(roomId:String, onSuccess: OnSuccess = {}, onError: OnError = { _, _ ->}){
        Log.e("apex","joinChatroom roomId: $roomId userId: ${ChatClient.getInstance().currentUser}")
        service.getChatService().joinChatroom(roomId, ChatClient.getInstance().currentUser
            , onSuccess = { chatroom->
                Log.e("apex","joinChatroom  $roomId onSuccess")
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
                Log.e("apex","joinChatroom  $roomId onError $errorCode $result")
                onError.invoke(errorCode,result)
            }
        )
    }

}