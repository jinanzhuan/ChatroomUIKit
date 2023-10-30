package io.agora.chatroom.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.widget.ImageView
import androidx.activity.ComponentActivity
import androidx.core.view.WindowCompat
import io.agora.chatroom.ChatroomUIKitClient
import io.agora.chatroom.UIChatRoomViewTest
import io.agora.chatroom.service.ChatroomDestroyedListener
import io.agora.chatroom.uikit.R

class UIChatroomActivity : ComponentActivity(), ChatroomDestroyedListener {

    private val roomView: UIChatRoomViewTest by lazy { findViewById(R.id.room_view) }
    private var service:UIChatroomService? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContentView(R.layout.activity_chatroom)
        val roomBg = findViewById<ImageView>(R.id.room_bg)
        roomBg.scaleType = ImageView.ScaleType.CENTER_CROP

        val roomId = intent.getStringExtra(KEY_ROOM_ID)?: return
        val ownerId = intent.getStringExtra(KEY_OWNER_ID)?: return
        ChatroomUIKitClient.getInstance().initRoom(roomId,ownerId)

        val isDarkTheme = ChatroomUIKitClient.getInstance().getContext().getCurrentTheme()

        if (isDarkTheme){
            roomBg.setImageResource(R.drawable.icon_chatroom_bg_dark)
        }else{
            roomBg.setImageResource(R.drawable.icon_chatroom_bg_light)
        }

        val chatRoomInfo = ChatroomUIKitClient.getInstance().getContext().getCurrentRoomInfo()
        chatRoomInfo.let {
            service = UIChatroomService(it)
            // 注册监听
            ChatroomUIKitClient.getInstance().subscribeRoomDestroyed(this)
        }
        roomView.bindService(service)

    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.action == KeyEvent.ACTION_DOWN) {
            // 处理虚拟返回键的按下事件
            service?.getUserService()?.logout(
                onSuccess = {
                    ChatroomUIKitClient.getInstance().clear()
                    finish()
                },
                onFailure = {code, error ->  }
            )
            return true
        }
        return super.onKeyDown(keyCode, event)
    }


    companion object {
        private const val KEY_ROOM_ID = "roomId"
        private const val KEY_OWNER_ID = "ownerId"

        fun createIntent(
            context: Context,
            roomId: String,
            ownerId:String
        ): Intent {
            return Intent(context, UIChatroomActivity::class.java).apply {
                putExtra(KEY_ROOM_ID, roomId)
                putExtra(KEY_OWNER_ID, ownerId)
            }
        }
    }

    override fun onRoomDestroyed(roomId: String, roomName: String) {
        ChatroomUIKitClient.getInstance().clear()
        finish()
    }
}