package io.agora.chatroom.ui.commons

import io.agora.chatroom.service.ChatMessage
import io.agora.chatroom.service.ChatMessageType
import io.agora.chatroom.service.ChatroomService
import io.agora.chatroom.service.cache.UIChatroomCacheManager
import io.agora.chatroom.service.manager.ChatroomManager
import io.agora.chatroom.ui.compose.ComposeMessageItemState
import io.agora.chatroom.ui.compose.ComposeMessageListItemState
import io.agora.chatroom.ui.compose.GiftMessageState
import io.agora.chatroom.ui.compose.JoinedMessageState

class ComposeChatListController(
    private val roomId: String,
    private val messageState:ComposeMessagesState,
    private val chatService: ChatroomService
){
    fun analysisMessage(){
        val data = UIChatroomCacheManager.cacheManager.getCurrentMsgList
        data.let {
            if (it.isNotEmpty()) {
                it.forEach { msg ->
                    if (msg.type == ChatMessageType.TXT) {
                        messageState.addMessage(ComposeMessageItemState(msg))
                    } else if (msg.type == ChatMessageType.CUSTOM) {
                        if (ChatroomManager().checkJoinedMsg(msg)) {
                            messageState.addMessage(JoinedMessageState(msg))
                        } else {
                            messageState.addMessage(GiftMessageState(msg))
                        }
                    }
                }
            }
        }
    }

    val currentComposeMessagesState: ComposeMessagesState
        get() = messageState

    fun addTextMessage(message:ChatMessage){
        messageState.addMessage(ComposeMessageItemState(message))
    }

    fun removeMessage(message: ComposeMessageListItemState){
        messageState.removeMessage(message)
    }

}