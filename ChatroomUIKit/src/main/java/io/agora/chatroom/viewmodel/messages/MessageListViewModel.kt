package io.agora.chatroom.viewmodel.messages

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import io.agora.chatroom.service.ChatMessage
import io.agora.chatroom.commons.ComposeChatListController
import io.agora.chatroom.commons.ComposeMessagesState
import io.agora.chatroom.compose.chatmessagelist.ComposeMessageListItemState
import io.agora.chatroom.theme.primaryColor80
import io.agora.chatroom.theme.secondaryColor80

class MessageListViewModel(
    private val isDarkTheme: Boolean? = false,
    private val showDateSeparators: Boolean = true,
    private val showLabel: Boolean = true,
    private val showGift: Boolean = true,
    private val showAvatar: Boolean = true,
    private val dateSeparatorColor: Color = secondaryColor80,
    private val nickNameColor: Color = primaryColor80,
    private val composeChatListController: ComposeChatListController
    ): ViewModel() {

    fun addTextMessage(message:ChatMessage){
        composeChatListController.addTextMessage(message = message)
    }

    fun removeMessage(message: ComposeMessageListItemState){
        composeChatListController.removeMessage(message)
    }

    fun clearMessage(){
        composeChatListController.clearMessage()
    }

    val currentComposeMessagesState: ComposeMessagesState
        get() = composeChatListController.currentComposeMessagesState


    val getTheme: Boolean?
        get() = isDarkTheme

    val isShowDateSeparators:Boolean
        get() = showDateSeparators

    val isShowLabel:Boolean
        get() = showLabel

    val isShowGift:Boolean
        get() = showGift

    val isShowAvatar:Boolean
        get() = showAvatar

    val getDateSeparatorColor:Color
        get() = dateSeparatorColor

    val getNickNameColor:Color
        get() = nickNameColor

}