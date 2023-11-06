package io.agora.chatroom

import android.app.Application
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import io.agora.chatroom.bean.RoomDetailBean
import io.agora.chatroom.compose.ChatroomList
import io.agora.chatroom.compose.avatar.Avatar
import io.agora.chatroom.compose.image.AsyncImage
import io.agora.chatroom.compose.indicator.LoadingIndicator
import io.agora.chatroom.compose.switch
import io.agora.chatroom.compose.utils.WindowConfigUtils
import io.agora.chatroom.model.UserInfoProtocol
import io.agora.chatroom.theme.ChatroomUIKitTheme
import io.agora.chatroom.ui.UIChatroomActivity
import io.agora.chatroom.utils.SPUtils
import io.agora.chatroom.viewmodel.ChatroomListViewModel
import io.agora.chatroom.viewmodel.RequestState

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            val isDark = SPUtils.getInstance(LocalContext.current.applicationContext as Application).getCurrentThemeStyle()
            var isDarkTheme by rememberSaveable {
                mutableStateOf(isDark)
            }
            val roomListViewModel = viewModel(modelClass = ChatroomListViewModel::class.java)
            roomListViewModel.fetchRoomList()
            val userDetail = SPUtils.getInstance(LocalContext.current.applicationContext as Application).getUerInfo()
            SPUtils.getInstance(LocalContext.current.applicationContext as Application).saveCurrentThemeStyle(isDarkTheme)
            ChatroomUIKitTheme(isDarkTheme = isDarkTheme) {
                WindowConfigUtils(
                    isDarkTheme = !isDarkTheme,
                    statusBarColor = ChatroomUIKitTheme.colors.background,
                    nativeBarColor = ChatroomUIKitTheme.colors.background
                )
                Scaffold(
                    topBar = {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .statusBarsPadding()
                                .background(ChatroomUIKitTheme.colors.background)
                                .height(56.dp)
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically) {

                            Text(
                                text = "Channel list",
                                style = ChatroomUIKitTheme.typography.headlineLarge,
                                color = ChatroomUIKitTheme.colors.onBackground
                            )

                            Spacer(modifier = Modifier.width(10.dp))

                            if (roomListViewModel.getState is RequestState.Loading) {
                                LoadingIndicator()
                            } else {
                                Box(modifier = Modifier.clickable { roomListViewModel.fetchRoomList() }) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.progress),
                                        contentDescription = null,
                                        modifier = Modifier.size(20.dp),
                                        tint = ChatroomUIKitTheme.colors.onBackground
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.weight(1f))

                            switch(
                                checked = !isDark,
                                onCheckedChange = {
                                    isDarkTheme = !it
                                },
                                modifier = Modifier
                                    .size(width = 54.dp, height = 28.dp)
                                    .clip(RoundedCornerShape(28.dp))
                                    .background(ChatroomUIKitTheme.colors.backgroundHighest))

                        }
                    },
                    bottomBar = {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .navigationBarsPadding()
                                .shadow(
                                    elevation = 36.dp,
                                    spotColor = Color(0x26464E53),
                                    ambientColor = Color(0x26464E53)
                                )

                                .shadow(
                                    elevation = 24.dp,
                                    spotColor = Color(0x14171A1C),
                                    ambientColor = Color(0x14171A1C)
                                )

                                .border(
                                    width = 0.5.dp,
                                    color = ChatroomUIKitTheme.colors.background
                                )

                                .background(ChatroomUIKitTheme.colors.background)
                                .height(66.dp)
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically) {

                            Avatar(
                                imageUrl = userDetail?.avatarURL?:"",
                                modifier = Modifier.size(36.dp)
                            )

                            Spacer(modifier = Modifier.width(10.dp))

                            Text(
                                text = userDetail?.nickName?:userDetail?.userId ?: "",
                                style = ChatroomUIKitTheme.typography.bodyLarge,
                                color = ChatroomUIKitTheme.colors.onBackground
                            )

                            Spacer(modifier = Modifier.weight(1f))

                            Row(modifier = Modifier
                                .size(width = 102.dp, height = 38.dp)
                                .clip(RoundedCornerShape(28.dp))
                                .clickable { createRoom(roomListViewModel, userDetail) }
                                .background(ChatroomUIKitTheme.colors.primary),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.video_camera_splus),
                                    contentDescription = null,
                                    modifier = Modifier.size(SwitchDefaults.IconSize),
                                    tint = ChatroomUIKitTheme.colors.background
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Create",
                                    style = ChatroomUIKitTheme.typography.bodyMedium,
                                    color = ChatroomUIKitTheme.colors.background
                                )
                            }

                        }
                    }
                ) { padding ->
                    Surface(modifier = Modifier.padding(padding)) {
                        ChatroomList(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(ChatroomUIKitTheme.colors.backgroundHigh),
                            viewModel = roomListViewModel,
                            onItemClick = { roomDetail: RoomDetailBean ->
                                startActivity(
                                    UIChatroomActivity.createIntent(
                                        context = this@MainActivity,
                                        roomId = roomDetail.id,
                                        ownerId = roomDetail.owner
                                    )
                                )
                            }
                        )
                    }

                }
            }
        }
    }

    private fun createRoom(viewModel: ChatroomListViewModel, userDetail: UserInfoProtocol?) {
        if (userDetail == null) {
            return
        }
        viewModel.createChatroom(
            roomName = resources.getString(R.string.default_room_name, userDetail.nickName),
            owner = userDetail.userId,
            onSuccess = { roomDetail ->
                startActivity(
                    UIChatroomActivity.createIntent(
                        context = this@MainActivity,
                        roomId = roomDetail.id,
                        ownerId = roomDetail.owner
                    )
                )
            },
            onError = { code, message ->
                Log.e("MainActivity", "createChatroom: $code, $message")
            }
        )
    }
}
