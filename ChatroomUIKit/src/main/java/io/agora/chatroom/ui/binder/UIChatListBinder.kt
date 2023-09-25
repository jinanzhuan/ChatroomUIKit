package io.agora.chatroom.ui.binder

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.lifecycle.viewmodel.compose.viewModel
import io.agora.chatroom.service.ChatMessage
import io.agora.chatroom.service.ChatroomChangeListener
import io.agora.chatroom.ui.UIChatroomService
import io.agora.chatroom.ui.compose.chatmessagelist.ComposeChatMessageList
import io.agora.chatroom.ui.compose.utils.rememberMessageListState
import io.agora.chatroom.ui.theme.ChatroomUIKitTheme
import io.agora.chatroom.ui.viewmodel.messages.MessageListViewModel
import io.agora.chatroom.ui.viewmodel.messages.MessagesViewModelFactory

class UIChatListBinder(
    private val chatList: ComposeView,
    private val service: UIChatroomService,
    private val factory: MessagesViewModelFactory,
):UIBinder, ChatroomChangeListener {
    lateinit var listViewModel:MessageListViewModel

    init {

        chatList.setContent {
            ChatroomUIKitTheme{

                listViewModel = viewModel(MessageListViewModel::class.java, factory = factory)
                val currentState = listViewModel.currentMessagesState

                ComposeChatMessageList(
                    viewModel = listViewModel,
                    modifier = Modifier
                        .fillMaxSize(),
                    lazyListState = rememberMessageListState(parentMessageId = currentState.parentMessageId),
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
        listViewModel.refreshMessage(message)
    }

}