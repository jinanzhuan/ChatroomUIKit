package io.agora.chatroom

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.KeyEvent
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.core.view.WindowCompat
import androidx.lifecycle.ViewModelProvider
import io.agora.chatroom.compose.ComposeChat
import io.agora.chatroom.compose.VideoPlayerCompose
import io.agora.chatroom.compose.utils.WindowConfigUtils
import io.agora.chatroom.model.UIChatroomInfo
import io.agora.chatroom.model.UIConstant
import io.agora.chatroom.theme.ChatroomUIKitTheme
import io.agora.chatroom.ui.UIChatroomService
import io.agora.chatroom.ui.UISearchActivity
import io.agora.chatroom.viewmodel.ChatroomFactory
import io.agora.chatroom.viewmodel.ChatroomViewModel
import io.agora.chatroom.viewmodel.gift.ComposeGiftListViewModel
import io.agora.chatroom.viewmodel.messages.MessagesViewModelFactory

class UIChatroomActivity : ComponentActivity(), ChatroomResultListener {

    private lateinit var roomId:String
    private lateinit var service: UIChatroomService

    private val roomViewModel by lazy {
        ViewModelProvider(this@UIChatroomActivity as ComponentActivity,
            factory = ChatroomFactory(service = service)
        )[ChatroomViewModel::class.java]
    }

    private val giftViewModel by lazy {
        ViewModelProvider(this@UIChatroomActivity as ComponentActivity,
            factory = MessagesViewModelFactory(context = this@UIChatroomActivity, roomId = roomId, service = service))[ComposeGiftListViewModel::class.java]
    }

    private val launcherToSearch: ActivityResultLauncher<Intent> =
        (this@UIChatroomActivity as ComponentActivity).registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            roomViewModel.closeMemberSheet.value = result.resultCode == Activity.RESULT_OK
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        roomId = intent.getStringExtra(KEY_ROOM_ID)?: return
        val ownerId = intent.getStringExtra(KEY_OWNER_ID)?: return
        val type = intent.getStringExtra(KEY_TYPE)?: UIConstant.CHATROOM_LIVE_TYPE

        val uiChatroomInfo = UIChatroomInfo(
            roomId,
            ChatroomUIKitClient.getInstance().getChatroomUser().getUserInfo(ownerId)
        )

        service = UIChatroomService(uiChatroomInfo)
        ChatroomUIKitClient.getInstance().registerRoomResultListener(this)

        val commonConfig = ChatroomUIKitClient.getInstance().getContext().getCommonConfig()
        if (commonConfig.isOpenAutoClearGiftList){
            giftViewModel.openAutoClear()
        }else{
            giftViewModel.closeAutoClear()
        }

        giftViewModel.setAutoClearTime(commonConfig.autoClearTime)

        setContent {
            ChatroomUIKitTheme{
                WindowConfigUtils(
                    statusBarColor = Color.Transparent,
                )
                Surface(
                    modifier = Modifier.fillMaxHeight()
                ){

                    if (type == UIConstant.CHATROOM_PROMOTION_LIVE_TYPE){
                        roomViewModel.hideBg()
                        VideoPlayerCompose(Uri.parse("android.resource://$packageName/${R.raw.video_example}"))
                    }

                    ComposeChat(
                        roomViewModel = roomViewModel,
                        giftListViewModel = giftViewModel,
                        service = service,
                        onMemberSheetSearchClick = {
                                tab->
                            launcherToSearch.launch(
                                UISearchActivity.createIntent(
                                    this@UIChatroomActivity,
                                    roomId,
                                    tab
                                )
                            )
                        }
                    )
                }
            }
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.action == KeyEvent.ACTION_DOWN) {
            finish()
            service.getChatService().leaveChatroom(
                roomId = ChatroomUIKitClient.getInstance().getContext().getCurrentRoomInfo().roomId,
                userId = ChatroomUIKitClient.getInstance().getCurrentUser().userId,
                onSuccess = {
                    //
                },
                onError = {code, error ->  }
            )
            return true
        }
        return super.onKeyDown(keyCode, event)
    }


    companion object {
        private const val KEY_ROOM_ID = "roomId"
        private const val KEY_OWNER_ID = "ownerId"
        private const val KEY_TYPE = "roomType"

        fun createIntent(
            context: Context,
            roomId: String,
            ownerId:String,
            roomType:String = UIConstant.CHATROOM_LIVE_TYPE,
        ): Intent {
            return Intent(context, UIChatroomActivity::class.java).apply {
                putExtra(KEY_ROOM_ID, roomId)
                putExtra(KEY_OWNER_ID, ownerId)
                putExtra(KEY_TYPE,roomType)
            }
        }
    }

    override fun onEventResult(event: ChatroomResultEvent, errorCode: Int, errorMessage: String?) {
        if (event == ChatroomResultEvent.DESTROY_ROOM){
            ChatroomUIKitClient.getInstance().unregisterRoomResultListener(this)
            roomViewModel.destroyRoom()
            finish()
        }
    }
}