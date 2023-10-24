package io.agora.chatroom.viewmodel.messages

import androidx.lifecycle.ViewModel
import io.agora.chatroom.service.ChatMessage
import io.agora.chatroom.commons.ComposeChatListController
import io.agora.chatroom.commons.ComposeMessageListState
import io.agora.chatroom.compose.chatmessagelist.ComposeMessageListItemState
import io.agora.chatroom.service.GiftEntityProtocol

class MessageListViewModel(
    private val isDarkTheme: Boolean? = false,
    private val showDateSeparators: Boolean = true,
    private val showLabel: Boolean = true,
    private val showAvatar: Boolean = true,
    private val composeChatListController: ComposeChatListController
    ): ViewModel() {

    fun addTextMessage(message:ChatMessage){
        composeChatListController.addTextMessage(message)
    }

    fun addTextMessageByIndex(index:Int = 0,message:ChatMessage){
        composeChatListController.addTextMessage(index = index,message = message)
    }

    fun addGiftMessageByIndex(index:Int = 0,message:ChatMessage,gift:GiftEntityProtocol){
        composeChatListController.addGiftMessage(index = index,message = message, gift = gift)
    }

    fun addGiftMessage(message:ChatMessage,gift:GiftEntityProtocol){
        composeChatListController.addGiftMessage(message,gift)
    }

    fun addJoinedMessageByIndex(index:Int = 0,message:ChatMessage){
        composeChatListController.addJoinedMessage(index = index, message = message)
    }

    fun addJoinedMessage(message:ChatMessage){
        composeChatListController.addJoinedMessage(message)
    }

    fun removeMessage(message: ComposeMessageListItemState){
        composeChatListController.removeMessage(message)
    }

    fun clearMessage(){
        composeChatListController.clearMessage()
    }

    val currentComposeMessageListState: ComposeMessageListState
        get() = composeChatListController.currentComposeMessageListState


    val getTheme: Boolean?
        get() = isDarkTheme

    val isShowDateSeparators:Boolean
        get() = showDateSeparators

    val isShowLabel:Boolean
        get() = showLabel

    val isShowAvatar:Boolean
        get() = showAvatar

}