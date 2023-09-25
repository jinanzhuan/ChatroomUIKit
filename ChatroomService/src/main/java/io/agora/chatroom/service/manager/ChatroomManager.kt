package io.agora.chatroom.service.manager

import io.agora.chatroom.model.UIConstant
import io.agora.chatroom.service.ChatMessage

class ChatroomManager {

    fun checkJoinedMsg(msg:ChatMessage):Boolean{
        val ext = msg.ext()
        return ext.containsKey(UIConstant.CHATROOM_UIKIT_USER_JOIN)
    }
}