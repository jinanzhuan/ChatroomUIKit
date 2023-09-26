package io.agora.chatroom.ui.commons

import io.agora.chatroom.service.ChatMessage
import io.agora.chatroom.service.ChatroomService
import io.agora.chatroom.ui.compose.ComposeMessageItemState
import io.agora.chatroom.ui.compose.ComposeMessageListItemState
import io.agora.chatroom.ui.compose.GiftMessageState
import io.agora.chatroom.ui.compose.JoinedMessageState

class ComposeChatListController(
    private val roomId: String,
    private val messageState:ComposeMessagesState,
    private val chatService: ChatroomService
){

    val currentComposeMessagesState: ComposeMessagesState
        get() = messageState

    fun addTextMessage(message:ChatMessage){
        if (message.conversationId() == roomId){
            messageState.addMessage(ComposeMessageItemState(message))
        }
    }

    fun removeMessage(msg: ComposeMessageListItemState){
        val conversationId:String = when (msg) {
            is JoinedMessageState -> msg.message.conversationId()
            is GiftMessageState -> msg.message.conversationId()
            is ComposeMessageItemState -> msg.message.conversationId()
            else -> { "" }
        }
        if (conversationId == roomId){
            messageState.removeMessage(msg)
        }
    }

    fun clearMessage(){
        messageState.clearMessage()
    }
}