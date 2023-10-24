package io.agora.chatroom.commons

import io.agora.chatroom.service.ChatMessage
import io.agora.chatroom.compose.chatmessagelist.ComposeMessageItemState
import io.agora.chatroom.compose.chatmessagelist.ComposeMessageListItemState
import io.agora.chatroom.compose.chatmessagelist.GiftMessageState
import io.agora.chatroom.compose.chatmessagelist.JoinedMessageState
import io.agora.chatroom.service.GiftEntityProtocol

class ComposeChatListController(
    private val roomId: String,
    private val messageState:ComposeMessageListState,
){

    val currentComposeMessageListState: ComposeMessageListState
        get() = messageState

    fun addTextMessage(index:Int,message:ChatMessage){
        if (message.conversationId() == roomId){
            messageState.addMessageByIndex(index,ComposeMessageItemState(message))
        }
    }

    fun addTextMessage(message:ChatMessage){
        if (message.conversationId() == roomId){
            messageState.addMessage(ComposeMessageItemState(message))
        }
    }

    fun updateTextMessage(index: Int,message: ChatMessage){
        if (message.conversationId() == roomId){
            messageState.updateMessage(index,ComposeMessageItemState(message))
        }
    }

    fun addGiftMessage(index:Int,message:ChatMessage,gift:GiftEntityProtocol){
        if (message.conversationId() == roomId){
            messageState.addMessageByIndex(index,GiftMessageState(message,gift))
        }
    }

    fun addGiftMessage(message:ChatMessage,gift:GiftEntityProtocol){
        if (message.conversationId() == roomId){
            messageState.addMessage(GiftMessageState(message,gift))
        }
    }

    fun addJoinedMessage(index:Int,message:ChatMessage){
        if (message.conversationId() == roomId){
            messageState.addMessageByIndex(index,JoinedMessageState(message))
        }
    }

    fun addJoinedMessage(message:ChatMessage){
        if (message.conversationId() == roomId){
            messageState.addMessage(JoinedMessageState(message))
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