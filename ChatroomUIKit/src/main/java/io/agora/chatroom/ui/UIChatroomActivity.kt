package io.agora.chatroom.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import io.agora.CallBack
import io.agora.chat.ChatOptions
import io.agora.chatroom.model.UIChatroomInfo
import io.agora.chatroom.model.UserInfoProtocol
import io.agora.chatroom.service.ChatClient
import io.agora.chatroom.uikit.R

class UIChatroomActivity : ComponentActivity(){

//    private val composeView: ComposeView by lazy { findViewById(R.id.compose_chat_room) }
    private val roomView: UIChatRoomView by lazy { findViewById(R.id.room_view) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chatroom)

        val roomId = intent.getStringExtra(KEY_ROOM_ID) ?: return

        val chatOptions = ChatOptions()
        chatOptions.appKey = "easemob#easeim"
        chatOptions.autoLogin = false
        ChatClient.getInstance().init(applicationContext, chatOptions)
        ChatClient.getInstance().login("apex1","1",object : CallBack {
            override fun onSuccess() {
                Log.e("apex","login onSuccess")
            }

            override fun onError(code: Int, error: String?) {

            }

        })


        roomView.bindService(UIChatroomService(UIChatroomInfo("123", UserInfoProtocol())))

    }


    companion object {
        private const val KEY_ROOM_ID = "roomId"

        fun createIntent(
            context: Context,
            roomId: String,
        ): Intent {
            return Intent(context, UIChatroomActivity::class.java).apply {
                putExtra(KEY_ROOM_ID, roomId)
            }
        }
    }
}