package io.agora.chatroom.binder

import android.util.Log
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.lifecycle.viewmodel.compose.viewModel
import io.agora.chatroom.service.ChatMessage
import io.agora.chatroom.service.ChatroomChangeListener
import io.agora.chatroom.ui.UIChatroomService
import io.agora.chatroom.compose.chatmessagelist.ComposeChatMessageList
import io.agora.chatroom.theme.ChatroomUIKitTheme
import io.agora.chatroom.viewmodel.messages.MessageListViewModel
import io.agora.chatroom.viewmodel.messages.MessagesViewModelFactory

class UIChatListBinder(
    private val chatList: ComposeView,
    private val service: UIChatroomService,
    private val factory: MessagesViewModelFactory,
): io.agora.chatroom.binder.UIBinder, ChatroomChangeListener {
    lateinit var listViewModel:MessageListViewModel

    init {

        chatList.setContent {
            ChatroomUIKitTheme{

                listViewModel = viewModel(MessageListViewModel::class.java, factory = factory)

                ComposeChatMessageList(
                    viewModel = listViewModel,
                    modifier = Modifier
                        .fillMaxSize(),
                    onLongItemClick = { index,message->
                        Log.e("apex","onLongItemClick $index $message")
                    }
                )
            }
        }
    }

    override fun bind() {
        service.getChatService().bindListener(this)
    }

    override fun unBind() {
        service.getChatService().unbindListener(this)
    }


    override fun onMessageReceived(message: ChatMessage) {
        Log.e("apex","onMessageReceived")
        listViewModel.addTextMessageByIndex(message = message)
    }

    override fun onRefreshMessage(message: ChatMessage) {
        Log.e("apex","refreshMessage")
        listViewModel.addTextMessageByIndex(message = message)
    }

}