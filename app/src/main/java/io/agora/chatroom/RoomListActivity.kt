package io.agora.chatroom

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import io.agora.chatroom.compose.input.InputField
import io.agora.chatroom.compose.list.LazyColumnList
import io.agora.chatroom.service.ChatCallback
import io.agora.chatroom.service.ChatClient
import io.agora.chatroom.service.ChatPageResult
import io.agora.chatroom.service.ChatValueCallback
import io.agora.chatroom.service.Chatroom
import io.agora.chatroom.service.OnError
import io.agora.chatroom.service.OnValueSuccess
import io.agora.chatroom.theme.ChatroomUIKitTheme
import io.agora.chatroom.ui.UIChatroomActivity
import io.agora.chatroom.viewmodel.RequestListViewModel

class RoomListActivity: ComponentActivity() {
    private val hideLogin by lazy { mutableStateOf(false) }
    private val chatRoomListViewModel by lazy { RoomListViewModel() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ChatroomUIKitTheme {
                if (hideLogin.value) {
                    // show room list
                    showRoomList()
                } else {
                    if (ChatClient.getInstance().options.autoLogin && ChatClient.getInstance().isLoggedInBefore) {
                        hideLogin.value = true
                        showRoomList()
                    } else {
                        login()
                    }
                }

            }
        }
    }

    @Composable
    fun showRoomList() {
        chatRoomListViewModel.fetchPublicRoom()
        LazyColumnList(
            modifier = Modifier.fillMaxSize(),
            viewModel = chatRoomListViewModel,
            onScrollChange = { listState ->
                if (listState.isScrollInProgress && !listState.canScrollForward) {
                    chatRoomListViewModel.fetchMorePublicRoom()
                }
            },
            contentPadding = PaddingValues(10.dp),
            ) { index, item ->

            Surface(
                modifier = Modifier.padding(vertical = 10.dp).clickable{
                                                                       skipToTarget(item)
                },
                shape = RoundedCornerShape(10.dp)
            ) {
                Column(modifier = Modifier
                    .fillMaxWidth()
                    .background(ChatroomUIKitTheme.colors.background)
                    .height(50.dp)
                    .padding(horizontal = 20.dp), horizontalAlignment = Alignment.Start) {
                    Text(text = item.name)
                    Text(text = item.id)
                }
            }

        }
    }

    private fun skipToTarget(
        chatroom: Chatroom
    ) {
        Log.e("skipToTarget", "skipToTarget: id: ${chatroom.id} owner: ${chatroom.owner}")
        startActivity(UIChatroomActivity.createIntent(context = this,
            roomId = chatroom.id,
            ownerId = chatroom.owner)
        )
    }

    @Composable
    fun login() {
        Column(modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally) {

            var userId by rememberSaveable { mutableStateOf("") }
            var password by rememberSaveable { mutableStateOf("") }
            var passwordHidden by rememberSaveable { mutableStateOf(true) }

            InputField(
                value = userId,
                onValueChange = { newValue->
                    userId = newValue
                },
                placeholder = {
                    if (userId.isBlank()) {
                        Text(text = "UserId",
                            color = ChatroomUIKitTheme.colors.inputHint,
                            style = ChatroomUIKitTheme.typography.bodyLarge)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .padding(horizontal = 20.dp))

            Spacer(modifier = Modifier.height(20.dp))

            InputField(
                value = password,
                onValueChange = { newValue->
                    password = newValue
                },
                visualTransformation =
                if (passwordHidden) PasswordVisualTransformation() else VisualTransformation.None,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                placeholder = {
                    if (password.isBlank()) {
                        Text(text = "Password",
                            color = ChatroomUIKitTheme.colors.inputHint,
                            style = ChatroomUIKitTheme.typography.bodyLarge)
                    }
                },
                trailingIcon = {
                    IconButton(onClick = { passwordHidden = !passwordHidden }) {
                        val visibilityIcon =
                            if (passwordHidden) Icons.Filled.MailOutline else Icons.Filled.Email
                        // Please provide localized description for accessibility services
                        val description = if (passwordHidden) "Show password" else "Hide password"
                        Icon(imageVector = visibilityIcon, contentDescription = description)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .padding(horizontal = 20.dp))

            Button(onClick = { login(userId, password) }, modifier = Modifier
                .padding(20.dp)
                .fillMaxWidth()) {
                Text(text = "Login")
            }

        }

    }

    private fun login(userId: String, password: String) {
        ChatClient.getInstance().login(userId, password, object : ChatCallback {
            override fun onSuccess() {
                // login success
                hideLogin.value = true
            }

            override fun onError(error: Int, errorMsg: String) {
                // login failed
            }
        })
    }
}

class RoomListViewModel(
    private val pageSize: Int = 50
): RequestListViewModel<Chatroom>() {

    private val pageNum by lazy { mutableIntStateOf(1) }
    private val hasMore by lazy { mutableStateOf(false) }

    fun fetchPublicRoom(onValueSuccess: OnValueSuccess<ChatPageResult<Chatroom>> = {}, onError: OnError = { _, _ ->}) {
        loading()
        clear()
        pageNum.intValue = 1
        ChatClient.getInstance().chatroomManager().asyncFetchPublicChatRoomsFromServer(pageNum.intValue, pageSize, object : ChatValueCallback<ChatPageResult<Chatroom>> {
            override fun onSuccess(value: ChatPageResult<Chatroom>) {
                hasMore.value = value.data.size >= pageSize
                add(value.data)
                onValueSuccess.invoke(value)
            }

            override fun onError(error: Int, errorMsg: String?) {
                error(error, errorMsg)
                onError.invoke(error, errorMsg)
            }
        })
    }

    fun fetchMorePublicRoom(onValueSuccess: OnValueSuccess<ChatPageResult<Chatroom>> = {}, onError: OnError = { _, _ ->}) {
        if (hasMore.value) {
            onValueSuccess.invoke(ChatPageResult())
            return
        }
        loadMore()
        pageNum.intValue ++
        ChatClient.getInstance().chatroomManager().asyncFetchPublicChatRoomsFromServer(pageNum.intValue, pageSize, object : ChatValueCallback<ChatPageResult<Chatroom>> {
            override fun onSuccess(value: ChatPageResult<Chatroom>) {
                hasMore.value = value.data.size >= pageSize
                addMore(value.data)
                onValueSuccess.invoke(value)
            }

            override fun onError(error: Int, errorMsg: String?) {
                error(error, errorMsg)
                onError.invoke(error, errorMsg)
            }
        })
    }


}



