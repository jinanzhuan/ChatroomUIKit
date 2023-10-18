package io.agora.chatroom.binder

import android.util.Log
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.viewmodel.compose.viewModel
import io.agora.chatroom.service.ChatroomChangeListener
import io.agora.chatroom.service.ChatroomService
import io.agora.chatroom.ui.UIChatroomService
import io.agora.chatroom.compose.chatbottombar.ComposeChatBottomBar
import io.agora.chatroom.theme.ChatroomUIKitTheme
import io.agora.chatroom.viewmodel.messages.MessageChatBarViewModel
import io.agora.chatroom.viewmodel.messages.MessagesViewModelFactory

class UIChatBottomBarBinder(
    private val baseLayout: ConstraintLayout,
    private val chatBottomBar: ComposeView,
    private val service: UIChatroomService,
    private val factory:MessagesViewModelFactory,
) : io.agora.chatroom.binder.UIBinder, ChatroomChangeListener {
    private val inputField: MutableState<Boolean> = mutableStateOf(false)
    private val chatService: ChatroomService = service.getChatService()

    init {
        chatBottomBar.setContent {
            ChatroomUIKitTheme{

                val composerViewModel = viewModel(MessageChatBarViewModel::class.java, factory = factory)
                val isShowInput by inputField

                ComposeChatBottomBar(
                    modifier = Modifier
                        .fillMaxSize(),
                    viewModel = composerViewModel,
                    showInput = isShowInput,
                    onSendMessage = { input->
                        Log.e("apex","onSendMessage")
                        chatService.sendTextMessage(
                            message = input,
                            roomId = "123",//service.getRoomInfo().roomId
                            onSuccess = {},
                            onError = {code, error ->

                            })
                    },
                    onMenuClick = {
                        Log.e("apex","onMenuClick:  $it")
                    },
                    onInputClick = {
                        Log.e("apex","onInputClick: ")
                        inputField.value = true
                    }
                )
            }
        }
    }

    override fun bind() {
        baseLayout.setOnClickListener {
            inputField.value = false
        }
        chatService.bindListener(this)
    }

    override fun unBind() {

    }


}