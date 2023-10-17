package io.agora.chatroom.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.activity.ComponentActivity
import androidx.core.view.WindowCompat
import io.agora.chatroom.UIChatRoomViewTest
import io.agora.chatroom.UIChatroomContext
import io.agora.chatroom.model.UIChatroomInfo
import io.agora.chatroom.model.UserInfoProtocol
import io.agora.chatroom.uikit.R

class UIChatroomActivity : ComponentActivity(){

    private val roomView: UIChatRoomViewTest by lazy { findViewById(R.id.room_view) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContentView(R.layout.activity_chatroom)
        val roomBg = findViewById<ImageView>(R.id.room_bg)
        roomBg.scaleType = ImageView.ScaleType.CENTER_CROP

        val roomId = intent.getStringExtra(KEY_ROOM_ID) ?: return
        val isDarkTheme = UIChatroomContext.shared.getCurrentTheme()

        if (isDarkTheme){
            roomBg.setImageResource(R.drawable.icon_chatroom_bg_dark)
        }else{
            roomBg.setImageResource(R.drawable.icon_chatroom_bg_light)
        }

        roomView.bindService(UIChatroomService(UIChatroomInfo(roomId, UserInfoProtocol())))
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