package io.agora.chatroom

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import io.agora.chatroom.bean.RoomDetailBean
import io.agora.chatroom.compose.ChatroomList
import io.agora.chatroom.compose.avatar.Avatar
import io.agora.chatroom.compose.image.AsyncImage
import io.agora.chatroom.compose.indicator.LoadingIndicator
import io.agora.chatroom.theme.ChatroomUIKitTheme
import io.agora.chatroom.ui.UIChatroomActivity
import io.agora.chatroom.viewmodel.ChatroomListViewModel
import io.agora.chatroom.viewmodel.RequestState

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val isDark = isSystemInDarkTheme()
            var isDarkTheme by rememberSaveable {
                mutableStateOf(isDark)
            }
            val roomListViewModel = viewModel(modelClass = ChatroomListViewModel::class.java)
            roomListViewModel.fetchRoomList()
            ChatroomUIKitTheme(isDarkTheme = isDarkTheme) {
                Scaffold(
                    topBar = {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
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

                                .border(width = 0.5.dp, color = Color(0xFFE3E6E8))

                                .background(ChatroomUIKitTheme.colors.background)
                                .height(66.dp)
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically) {

                            Avatar(
                                imageUrl = "",
                            )

                            Spacer(modifier = Modifier.width(10.dp))

                            Text(
                                text = "Nickname",
                                style = ChatroomUIKitTheme.typography.bodyLarge,
                                color = ChatroomUIKitTheme.colors.onBackground
                            )

                            Spacer(modifier = Modifier.weight(1f))

                            Row(modifier = Modifier
                                .size(width = 102.dp, height = 38.dp)
                                .clip(RoundedCornerShape(28.dp))
                                .background(ChatroomUIKitTheme.colors.primary),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Check,
                                    contentDescription = null,
                                    modifier = Modifier.size(SwitchDefaults.IconSize),
                                )
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
}
@Composable
fun switch(
    checked: Boolean,
    onCheckedChange: ((Boolean) -> Unit)?,
    modifier: Modifier = Modifier,
) {
    var selected by rememberSaveable {
        mutableStateOf(checked)
    }

    Row(modifier = modifier
        .clickable {
            selected = !selected
            Log.e("MainActivity", "switch selected: $selected")
            onCheckedChange?.invoke(selected)
        },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween) {

        val selectedModifier = Modifier
            .size(24.dp)
            .clip(CircleShape)
            .background(ChatroomUIKitTheme.colors.primary)
        val unSelectedModifier = Modifier
            .size(24.dp)

        Spacer(modifier = Modifier.width(2.dp))
        Box(modifier = if (selected) selectedModifier else unSelectedModifier,
            contentAlignment = Alignment.Center) {
            Icon(
                imageVector = Icons.Filled.Check,
                contentDescription = null,
                modifier = Modifier.size(SwitchDefaults.IconSize),
            )
        }

        Box(modifier = if (selected) unSelectedModifier else selectedModifier,
            contentAlignment = Alignment.Center) {
            Icon(
                imageVector = Icons.Filled.Close,
                contentDescription = null,
                modifier = Modifier.size(SwitchDefaults.IconSize),
            )
        }
        Spacer(modifier = Modifier.width(2.dp))
    }
}

@Preview(showBackground = true)
@Composable
fun previewSwitch() {
    ChatroomUIKitTheme {
        ElevatedCard(
            shape = ChatroomUIKitTheme.shapes.large,
            colors = CardDefaults.cardColors(
                containerColor = ChatroomUIKitTheme.colors.background,
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 6.dp
            ),
        ) {
            Row(modifier = Modifier
                .height(100.dp)
                .fillMaxWidth()) {
                AsyncImage(
                    imageUrl = "https://t7.baidu.com/it/u=1595072465,3644073269&fm=193&f=GIF",
                    modifier = Modifier
                        .size(100.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    shape = RoundedCornerShape(8.dp)
                )

                Column(modifier = Modifier.padding(vertical = 12.dp)) {
                    Text(
                        text = "Channel name",
                        style = ChatroomUIKitTheme.typography.bodyLarge,
                        color = ChatroomUIKitTheme.colors.onBackground
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row {
                        Text(
                            text = "Channel name",
                            style = ChatroomUIKitTheme.typography.bodyMedium,
                            color = ChatroomUIKitTheme.colors.onBackground
                        )

                        Spacer(modifier = Modifier.width(10.dp))

                        Text(
                            text = "Channel name",
                            style = ChatroomUIKitTheme.typography.bodyMedium,
                            color = ChatroomUIKitTheme.colors.onBackground
                        )
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    Row {
                        Spacer(modifier = Modifier.weight(1f))

                        Box(
                            modifier = Modifier
                                .size(width = 55.dp, height = 24.dp)
                                .clip(RoundedCornerShape(24.dp))
                                .background(ChatroomUIKitTheme.colors.backgroundHighest),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Enter",
                                style = ChatroomUIKitTheme.typography.labelSmall,
                                color = ChatroomUIKitTheme.colors.onBackground
                            )
                        }
                        
                        Spacer(modifier = Modifier.width(12.dp))
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                }
            }

        }
    }
}
