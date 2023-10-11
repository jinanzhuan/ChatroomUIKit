package io.agora.chatroom

import android.content.Context
import io.agora.chatroom.model.UIConstant
import io.agora.chatroom.service.ChatClient
import io.agora.chatroom.service.ChatMessage
import io.agora.chatroom.service.ChatOptions

class ChatroomUIKitClient
private constructor() {

    companion object {
        const val TAG = "ChatroomUIKitClient"
        val shared: ChatroomUIKitClient by lazy { ChatroomUIKitClient() }
    }

    fun checkJoinedMsg(msg:ChatMessage):Boolean{
        val ext = msg.ext()
        return ext.containsKey(UIConstant.CHATROOM_UIKIT_USER_JOIN)
    }

    fun setUp(applicationContext: Context, appKey:String){
        val chatOptions = ChatOptions()
        chatOptions.appKey = appKey
        ChatClient.getInstance().init(applicationContext,chatOptions)
    }

    fun isLoginBefore():Boolean{
       return ChatClient.getInstance().isLoggedInBefore
    }

}