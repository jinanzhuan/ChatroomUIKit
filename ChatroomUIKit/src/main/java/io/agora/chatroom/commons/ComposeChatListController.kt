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

    fun updateTextMessage(message: ChatMessage){
        if (message.conversationId() == roomId){
            messageState.updateMessage(ComposeMessageItemState(message))
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

    /**
     * Returns the message with the given id.
     * @param messageId The id of the message to return.
     */
    fun getMessage(messageId: String): ComposeMessageListItemState? {
        return messageState.getMessage(messageId)
    }

    fun removeMessageByIndex(index: Int){
        messageState.removeMessageByIndex(index)
    }

    fun removeMessage(msg: ComposeMessageListItemState){
        if (msg.conversationId == roomId){
            messageState.removeMessage(msg)
        }
    }

    fun clearMessage(){
        messageState.clearMessage()
    }
}