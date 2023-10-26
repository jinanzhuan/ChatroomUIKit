package io.agora.chatroom

import android.content.Context
import android.text.TextUtils
import android.util.Log
import io.agora.ChatRoomChangeListener
import io.agora.MessageListener
import io.agora.chat.CustomMessageBody
import io.agora.chatroom.model.UIChatroomInfo
import io.agora.chatroom.model.UIConstant
import io.agora.chatroom.model.UserInfoProtocol
import io.agora.chatroom.model.toUser
import io.agora.chatroom.service.ChatClient
import io.agora.chatroom.service.ChatCustomMessageBody
import io.agora.chatroom.service.ChatException
import io.agora.chatroom.service.ChatMessage
import io.agora.chatroom.service.ChatMessageType
import io.agora.chatroom.service.ChatOptions
import io.agora.chatroom.service.ChatType
import io.agora.chatroom.service.ChatroomChangeListener
import io.agora.chatroom.service.ChatroomDestroyedListener
import io.agora.chatroom.service.GiftEntityProtocol
import io.agora.chatroom.service.GiftReceiveListener
import io.agora.chatroom.service.UserEntity
import io.agora.chatroom.service.cache.UIChatroomCacheManager
import io.agora.chatroom.utils.GsonTools
import org.json.JSONObject

class ChatroomUIKitClient : MessageListener, ChatRoomChangeListener {
    private var currentRoomContext:UIChatroomContext = UIChatroomContext()
    private var chatroomUser:UIChatroomUser = UIChatroomUser()
    private var eventListeners = mutableListOf<ChatroomChangeListener>()
    private var giftListeners = mutableListOf<GiftReceiveListener>()
    private lateinit var roomListener : ChatroomDestroyedListener
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
        ChatClient.getInstance().chatManager().addMessageListener(this)
        ChatClient.getInstance().chatroomManager().addChatRoomChangeListener(this)
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

    fun insertJoinedMessage(roomId:String,userId:String):ChatMessage{
        val joinedUserInfo = chatroomUser.getUserInfo(userId)
        val joinedMessage:ChatMessage
        if (userId == getCurrentUser().userId){
            joinedMessage = ChatMessage.createSendMessage(ChatMessageType.CUSTOM)
            joinedMessage.to = roomId
        }else{
            joinedMessage = ChatMessage.createReceiveMessage(ChatMessageType.CUSTOM)
            joinedMessage.from = userId
            joinedMessage.to = roomId
        }
        val customMessageBody = ChatCustomMessageBody(UIConstant.CHATROOM_UIKIT_USER_JOIN)
        joinedMessage.addBody(customMessageBody)
        val jsonString = GsonTools.beanToString(joinedUserInfo)
        joinedMessage.setAttribute(UIConstant.CHATROOM_UIKIT_USER_INFO, jsonString?.let { JSONObject(it) })
        return joinedMessage
    }

    override fun onMessageReceived(messages: MutableList<ChatMessage>?) {
        messages?.forEach {
            if (it.type == ChatMessageType.TXT) {
                try {
                    for (listener in eventListeners) {
                        listener.onMessageReceived(it)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            // 先判断是否自定义消息
            if (it.type == ChatMessageType.CUSTOM) {
                val body = it.body as CustomMessageBody
                val event = body.event()
                val msgType: UICustomMsgType? = getCustomMsgType(event)

                // 再排除单聊
                if (it.chatType != ChatType.Chat){
                    val username: String = it.to
                    // 判断是否同一个聊天室或者群组 并且 event不为空
                    if (TextUtils.equals(username,currentRoomContext.getCurrentRoomInfo().roomId) && !TextUtils.isEmpty(event)) {
                        when (msgType) {
                            UICustomMsgType.CHATROOMUIKITUSERJOIN -> {
                                try {
                                    for (listener in eventListeners) {
                                        parseJoinedMsg(it)?.let { userInfo->
                                            chatroomUser.setUserInfo(it.from,userInfo.toUser())
                                        }
                                        listener.onUserJoined(it.conversationId(),it.from)
                                    }
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                            }
                            UICustomMsgType.CHATROOMUIKITGIFT -> {
                                try {
                                    for (listener in giftListeners) {
                                        val giftEntity = parseGiftMsg(it)
                                        listener.onGiftReceived(
                                            roomId = currentRoomContext.getCurrentRoomInfo().roomId,
                                            gift = giftEntity,
                                            message = it
                                        )
                                    }
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                            }
                            else -> {}
                        }
                    }
                }
            }
        }
    }

    fun subscribeRoomDestroyed(listener:ChatroomDestroyedListener){
        this.roomListener = listener
    }

    /**
     * 获取自定义消息类型
     * @param event
     * @return
     */
    private fun getCustomMsgType(event: String?): UICustomMsgType? {
        return if (TextUtils.isEmpty(event)) {
            null
        } else UICustomMsgType.fromName(event)
    }

    private fun parseGiftMsg(msg: ChatMessage): GiftEntityProtocol? {
        if (msg.ext().containsKey(UIConstant.CHATROOM_UIKIT_GIFT_INFO)){
            return try {
                val jsonObject = msg.getJSONObjectAttribute(UIConstant.CHATROOM_UIKIT_GIFT_INFO)
                GsonTools.toBean(jsonObject.toString(), GiftEntityProtocol::class.java)
            }catch (e:ChatException){
                null
            }
        }
        return null
    }

    private fun parseJoinedMsg(msg: ChatMessage):UserInfoProtocol? {
        if (msg.ext().containsKey(UIConstant.CHATROOM_UIKIT_USER_INFO)){
            return try {
                val jsonObject = msg.getStringAttribute(UIConstant.CHATROOM_UIKIT_USER_INFO)
                GsonTools.toBean(jsonObject.toString(), UserInfoProtocol::class.java)
            }catch (e:ChatException){
                null
            }
        }
        return null
    }


    fun updateChatroomChangeListener(listener:MutableList<ChatroomChangeListener>){
        this.eventListeners = listener
    }

    fun updateChatroomGiftListener(listener:MutableList<GiftReceiveListener>){
        this.giftListeners = listener
    }

    override fun onChatRoomDestroyed(roomId: String, roomName: String) {
        eventListeners.clear()
        giftListeners.clear()
        roomListener.onRoomDestroyed(roomId,roomName)
    }

    override fun onMemberJoined(roomId: String?, participant: String?) {}

    override fun onMemberExited(roomId: String, roomName: String, participant: String) {
        try {
            for (listener in eventListeners) {
                listener.onUserLeft(roomId,roomName)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onRemovedFromChatRoom(
        reason: Int,
        roomId: String,
        roomName: String,
        participant: String
    ) {
        try {
            for (listener in eventListeners) {
                listener.onUserBeKicked(roomId,roomName)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onMuteListAdded(
        chatRoomId: String,
        mutes: MutableList<String>,
        expireTime: Long
    ) {
        try {
            for (listener in eventListeners) {
                if (mutes.size > 0){
                    for (mute in mutes) {
                        listener.onUserMuted(chatRoomId,mute)
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onMuteListRemoved(chatRoomId: String, mutes: MutableList<String>) {
        try {
            for (listener in eventListeners) {
                if (mutes.size > 0){
                    for (mute in mutes) {
                        listener.onUserUnmuted(chatRoomId,mute)
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onWhiteListAdded(chatRoomId: String?, whitelist: MutableList<String>?) {}

    override fun onWhiteListRemoved(chatRoomId: String?, whitelist: MutableList<String>?) {}

    override fun onAllMemberMuteStateChanged(chatRoomId: String?, isMuted: Boolean) {}

    override fun onAdminAdded(chatRoomId: String, admin: String) {
        try {
            for (listener in eventListeners) {
                listener.onAdminAdded(chatRoomId,admin)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onAdminRemoved(chatRoomId: String, admin: String) {
        try {
            for (listener in eventListeners) {
                listener.onAdminRemoved(chatRoomId,admin)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onOwnerChanged(chatRoomId: String?, newOwner: String?, oldOwner: String?) {}

    override fun onAnnouncementChanged(chatRoomId: String, announcement: String) {
        try {
            for (listener in eventListeners) {
                listener.onAnnouncementUpdated(chatRoomId,announcement)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


}