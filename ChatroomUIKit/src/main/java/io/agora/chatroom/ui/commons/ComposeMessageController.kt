package io.agora.chatroom.ui.commons

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import io.agora.chatroom.service.ChatMessage
import io.agora.chatroom.service.ChatMessageType
import io.agora.chatroom.service.cache.UIChatroomCacheManager
import io.agora.chatroom.service.manager.ChatroomManager
import io.agora.chatroom.ui.compose.ComposeMessageItemState
import io.agora.chatroom.ui.compose.ComposeMessageListItemState
import io.agora.chatroom.ui.compose.GiftMessageState
import io.agora.chatroom.ui.compose.JoinedMessageState
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

open class ComposeMessageController{
    val scopeMain = MainScope()

    /**
     * State of the screen.
     */
     var messagesState: ComposeMessagesState by mutableStateOf(ComposeMessagesState())

    init {
//        messagesState = ComposeMessagesState()
        observeMessages()

    }

    val getMessageStatus: ComposeMessagesState
        get() = messagesState


    private fun observeMessages() {
        messagesState = messagesState.copy(messageItems = getMsgList())
    }

    private fun getMsgList():MutableList<ComposeMessageListItemState> {
        val currentMsgList = mutableListOf<ComposeMessageListItemState>()
        val data = UIChatroomCacheManager.cacheManager.getCurrentMsgList
        Log.e("apex","data currentMsgList ${data.size}")
        data.let {
            if (it.size > 0) {
                it.forEach { msg ->
                    if (msg.type == ChatMessageType.TXT) {
                        currentMsgList.add(ComposeMessageItemState(msg))
                    } else if (msg.type == ChatMessageType.CUSTOM) {
                        if (ChatroomManager().checkJoinedMsg(msg)) {
                            currentMsgList.add(JoinedMessageState(msg))
                        } else {
                            currentMsgList.add(GiftMessageState(msg))
                        }
                    }
                }
            }
        }
        Log.e("apex","return currentMsgList ${currentMsgList}")
        return currentMsgList
    }


    fun refreshMessage(message:ChatMessage){
        scopeMain.launch {
            UIChatroomCacheManager.cacheManager.addMessage(message)
            Log.e("apex","refreshMessage bef ${messagesState.messageItems.size}  - cache: ${UIChatroomCacheManager.cacheManager.getCurrentMsgList?.size}")
            messagesState = messagesState.copy(messageItems = getMsgList())
            Log.e("apex","refreshMessage after ${messagesState.messageItems.size}")
        }
    }


    fun updateMessages(messages: List<ComposeMessageListItemState>) {
        val msgList = ArrayList<ChatMessage>()
        messages.forEach {
            when (it) {
                is ComposeMessageItemState -> {
                    msgList.add(it.message)
                }

                is JoinedMessageState -> {
                    msgList.add(it.message)
                }

                is GiftMessageState -> {
                    msgList.add(it.message)
                }

                else -> {}
            }
        }
        UIChatroomCacheManager.cacheManager.updateMessage(msgList)
        messagesState = messagesState.copy(messageItems = messages)
    }
}