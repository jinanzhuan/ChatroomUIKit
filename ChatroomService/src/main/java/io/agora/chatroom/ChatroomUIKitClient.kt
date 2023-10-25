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
import io.agora.chatroom.service.UserEntity
import io.agora.chatroom.service.cache.UIChatroomCacheManager
import io.agora.chatroom.utils.GsonTools
import org.json.JSONObject

class ChatroomUIKitClient{
    private var currentRoomContext:UIChatroomContext = UIChatroomContext()
    private var chatroomUser: UIChatroomUser = UIChatroomUser()
    private val cacheManager: UIChatroomCacheManager = UIChatroomCacheManager()

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

    fun getContext():UIChatroomContext{
        return currentRoomContext
    }

    fun getChatroomUser():UIChatroomUser{
        return chatroomUser
    }

    fun getCacheManager():UIChatroomCacheManager{
        return cacheManager
    }

    fun checkJoinedMsg(msg:ChatMessage):Boolean{
        val ext = msg.ext()
        return ext.containsKey(UIConstant.CHATROOM_UIKIT_USER_JOIN)
    }

    fun initRoom(roomId:String,ownerId:String){
        currentRoomContext.setCurrentRoomInfo(UIChatroomInfo(roomId,chatroomUser.getUserInfo(ownerId)))
    }

    fun setUp(applicationContext: Context, appKey:String){
        currentRoomContext.setRoomContext(applicationContext)
        val chatOptions = ChatOptions()
        chatOptions.appKey = appKey
        chatOptions.autoLogin = false
        ChatClient.getInstance().init(applicationContext,chatOptions)
        cacheManager.init(applicationContext)
    }

    fun isLoginBefore():Boolean{
       return ChatClient.getInstance().isLoggedInBefore
    }

    fun getCurrentUser():UserEntity{
        val currentUser = ChatClient.getInstance().currentUser
        return chatroomUser.getUserInfo(currentUser)
    }

    fun getJoinedMessage():ChatMessage{
        val joinedMessage = ChatMessage.createSendMessage(ChatMessageType.CUSTOM)
        val customMessageBody = ChatCustomMessageBody(UIConstant.CHATROOM_UIKIT_USER_JOIN)
        joinedMessage.addBody(customMessageBody)
        joinedMessage.to = currentRoomContext.getCurrentRoomInfo().roomId
        val jsonString = GsonTools.beanToString(getCurrentUser())
        joinedMessage.setAttribute(UIConstant.CHATROOM_UIKIT_USER_INFO, jsonString?.let { JSONObject(it) })
        return joinedMessage
    }

}