package io.agora.chatroom

import android.content.Context
import io.agora.chatroom.model.UIChatroomInfo
import io.agora.chatroom.model.UIConstant
import io.agora.chatroom.model.UserInfoProtocol
import io.agora.chatroom.service.ChatClient
import io.agora.chatroom.service.ChatCustomMessageBody
import io.agora.chatroom.service.ChatMessage
import io.agora.chatroom.service.ChatMessageType
import io.agora.chatroom.service.ChatOptions
import io.agora.chatroom.utils.GsonTools
import org.json.JSONObject

class ChatroomUIKitClient{
    private var currentRoomInfo:UIChatroomInfo? = null
    private var ownerInfo:UserInfoProtocol? = null

    companion object {
        const val TAG = "ChatroomUIKitClient"
        private var shared: ChatroomUIKitClient? = null

        fun getInstance(): ChatroomUIKitClient {
            if (shared == null) {
                synchronized(ChatroomUIKitClient::class.java) {
                    if (shared == null) {
                        shared = ChatroomUIKitClient()
                    }
                }
            }
            return shared!!
        }
    }

    fun checkJoinedMsg(msg:ChatMessage):Boolean{
        val ext = msg.ext()
        return ext.containsKey(UIConstant.CHATROOM_UIKIT_USER_JOIN)
    }

    fun setChatRoomInfo(roomId:String,ownerId:String){
        ownerInfo = UIChatroomContext.getInstance().getUserInfo(ownerId)
        currentRoomInfo = UIChatroomInfo(roomId,ownerInfo)
    }

    fun getChatRoomInfo():UIChatroomInfo?{
        return currentRoomInfo
    }

    fun setUp(applicationContext: Context, appKey:String){
        val chatOptions = ChatOptions()
        chatOptions.appKey = appKey
        chatOptions.autoLogin = false
        ChatClient.getInstance().init(applicationContext,chatOptions)
    }

    fun isLoginBefore():Boolean{
       return ChatClient.getInstance().isLoggedInBefore
    }

    fun getCurrentUser():UserInfoProtocol{
        val currentUser = ChatClient.getInstance().currentUser
        return  UIChatroomContext.getInstance().getUserInfo(currentUser)
    }

    fun getJoinedMessage():ChatMessage{
        val joinedMessage = ChatMessage.createSendMessage(ChatMessageType.CUSTOM)
        val customMessageBody = ChatCustomMessageBody(UIConstant.CHATROOM_UIKIT_USER_JOIN)
        joinedMessage.addBody(customMessageBody)
        joinedMessage.to = currentRoomInfo?.roomId
        val jsonString = GsonTools.beanToString(getCurrentUser())
        joinedMessage.setAttribute(UIConstant.CHATROOM_UIKIT_USER_INFO, jsonString?.let { JSONObject(it) })
        return joinedMessage
    }

}