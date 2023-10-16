package io.agora.chatroom.commons

import io.agora.chatroom.service.ChatMessage
import io.agora.chatroom.service.ChatroomService
import io.agora.chatroom.compose.chatmessagelist.ComposeMessageItemState
import io.agora.chatroom.compose.chatmessagelist.ComposeMessageListItemState
import io.agora.chatroom.compose.chatmessagelist.GiftMessageState
import io.agora.chatroom.compose.chatmessagelist.JoinedMessageState

class ComposeChatListController(
    private val roomId: String,
    private val messageState:ComposeMessageListState,
    private val chatService: ChatroomService
){

    val currentComposeMessageListState: ComposeMessageListState
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