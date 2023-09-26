package io.agora.chatroom.ui.viewmodel.messages

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import io.agora.chatroom.service.ChatMessage
import io.agora.chatroom.ui.commons.ComposeChatListController
import io.agora.chatroom.ui.commons.ComposeMessagesState
import io.agora.chatroom.ui.compose.ComposeMessageListItemState
import io.agora.chatroom.ui.theme.primaryColor8
import io.agora.chatroom.ui.theme.secondaryColor8

class MessageListViewModel(
    private val isDarkTheme: Boolean = false,
    private val showDateSeparators: Boolean = true,
    private val showLabel: Boolean = true,
    private val showGift: Boolean = true,
    private val showAvatar: Boolean = true,
    private val dateSeparatorColor: Color = secondaryColor8,
    private val nickNameColor: Color = primaryColor8,
    private val composeChatListController: ComposeChatListController
    ): ViewModel() {

    init {
        composeChatListController.analysisMessage()
    }

    fun addTextMessage(message:ChatMessage){
        composeChatListController.addTextMessage(message = message)
    }

    fun removeMessage(message: ComposeMessageListItemState){
        composeChatListController.removeMessage(message = message)
    }

    val currentComposeMessagesState: ComposeMessagesState
        get() = composeChatListController.currentComposeMessagesState


    val getTheme: Boolean
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

    internal companion object {
        /**
         * The default threshold for showing date separators. If the message difference in hours is equal to this
         * number, then we show a separator, if it's enabled in the list.
         */
        internal const val DateSeparatorDefaultHourThreshold: Long = 4

        /**
         * The default limit for messages count in requests.
         */
        internal const val DefaultMessageLimit: Int = 30

        /**
         * Time in millis, after which the focus is removed.
         */
        private const val RemoveMessageFocusDelay: Long = 2000

    }
}