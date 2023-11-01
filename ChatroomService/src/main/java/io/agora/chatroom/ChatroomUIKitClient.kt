package io.agora.chatroom

import android.content.Context
import android.text.TextUtils
import io.agora.chatroom.model.UIChatroomInfo
import io.agora.chatroom.model.UIConstant
import io.agora.chatroom.model.UserInfoProtocol
import io.agora.chatroom.model.toUser
import io.agora.chatroom.service.ChatClient
import io.agora.chatroom.service.ChatConnectionListener
import io.agora.chatroom.service.ChatCustomMessageBody
import io.agora.chatroom.service.ChatError
import io.agora.chatroom.service.ChatException
import io.agora.chatroom.service.ChatMessage
import io.agora.chatroom.service.ChatMessageListener
import io.agora.chatroom.service.ChatMessageType
import io.agora.chatroom.service.ChatOptions
import io.agora.chatroom.service.ChatRoomChangeListener
import io.agora.chatroom.service.ChatType
import io.agora.chatroom.service.ChatroomChangeListener
import io.agora.chatroom.service.ChatroomDestroyedListener
import io.agora.chatroom.service.GiftEntityProtocol
import io.agora.chatroom.service.GiftReceiveListener
import io.agora.chatroom.service.UserEntity
import io.agora.chatroom.service.UserStateChangeListener
import io.agora.chatroom.service.cache.UIChatroomCacheManager
import io.agora.chatroom.utils.GsonTools
import org.json.JSONObject

class ChatroomUIKitClient {
    private var currentRoomContext:UIChatroomContext = UIChatroomContext()
    private var chatroomUser:UIChatroomUser = UIChatroomUser()
    private var eventListeners = mutableListOf<ChatroomChangeListener>()
    private var giftListeners = mutableListOf<GiftReceiveListener>()
    private var userStateChangeListeners = mutableListOf<UserStateChangeListener>()
    private lateinit var roomListener : ChatroomDestroyedListener
    private val cacheManager: UIChatroomCacheManager = UIChatroomCacheManager()
    private val messageListener by lazy { InnerChatMessageListener() }
    private val chatroomChangeListener by lazy { InnerChatroomChangeListener() }
    private val userStateChangeListener by lazy { InnerUserStateChangeListener() }

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

    /**
     * Init the chatroom ui kit
     */
    fun setUp(applicationContext: Context, appKey:String){
        currentRoomContext.setRoomContext(applicationContext)
        val chatOptions = ChatOptions()
        chatOptions.appKey = appKey
        chatOptions.autoLogin = true
        ChatClient.getInstance().init(applicationContext,chatOptions)
        cacheManager.init(applicationContext)
        registerConnectListener()
    }

    /**
     * Init the chatroom before joining it
     */
    fun initRoom(roomId:String,ownerId:String){
        currentRoomContext.setCurrentRoomInfo(UIChatroomInfo(roomId,chatroomUser.getUserInfo(ownerId)))
        registerMessageListener()
        registerChatroomChangeListener()
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

    fun isCurrentRoomOwner():Boolean{
        return currentRoomContext.isCurrentOwner()
    }

    fun isLoginBefore():Boolean{
       return ChatClient.getInstance().isSdkInited && ChatClient.getInstance().isLoggedInBefore
    }

    fun getTranslationLanguage():List<String>{
        return currentRoomContext.getCommonConfig().languageList
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

    fun subscribeRoomDestroyed(listener:ChatroomDestroyedListener){
        this.roomListener = listener
    }

    fun clear(){
        eventListeners.clear()
        giftListeners.clear()
    }

    fun clearUserStateChangeListener(){
        userStateChangeListeners.clear()
    }

    internal fun updateChatroomChangeListener(listener:MutableList<ChatroomChangeListener>){
        this.eventListeners = listener
    }

    internal fun updateChatroomGiftListener(listener:MutableList<GiftReceiveListener>){
        this.giftListeners = listener
    }

    internal fun updateChatroomUserStateChangeListener(listener:MutableList<UserStateChangeListener>){
        this.userStateChangeListeners = listener
    }

    private fun registerMessageListener() {
        ChatClient.getInstance().chatManager().addMessageListener(messageListener)
    }

    private fun registerChatroomChangeListener() {
        ChatClient.getInstance().chatroomManager().addChatRoomChangeListener(chatroomChangeListener)
    }

    private fun registerConnectListener() {
        ChatClient.getInstance().addConnectionListener(userStateChangeListener)
    }

    private inner class InnerChatroomChangeListener: ChatRoomChangeListener {
        override fun onChatRoomDestroyed(roomId: String, roomName: String) {
            clear()
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

    private inner class InnerUserStateChangeListener: ChatConnectionListener {
        override fun onConnected() {
            try {
                for (listener in userStateChangeListeners) {
                    listener.onConnected()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        override fun onDisconnected(errorCode: Int) {
            // Should listen onLogout in below cases:
            if (errorCode == ChatError.USER_LOGIN_ANOTHER_DEVICE
                || errorCode == ChatError.USER_REMOVED
                || errorCode == ChatError.USER_BIND_ANOTHER_DEVICE
                || errorCode == ChatError.USER_DEVICE_CHANGED
                || errorCode == ChatError.SERVER_SERVICE_RESTRICTED
                || errorCode == ChatError.USER_LOGIN_TOO_MANY_DEVICES
                || errorCode == ChatError.USER_KICKED_BY_CHANGE_PASSWORD
                || errorCode == ChatError.USER_KICKED_BY_OTHER_DEVICE
                || errorCode == ChatError.APP_ACTIVE_NUMBER_REACH_LIMITATION) {
                return
            }
            try {
                for (listener in userStateChangeListeners) {
                    listener.onDisconnected(errorCode)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        override fun onTokenExpired() {
            try {
                for (listener in userStateChangeListeners) {
                    listener.onTokenExpired()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        override fun onTokenWillExpire() {
            try {
                for (listener in userStateChangeListeners) {
                    listener.onTokenWillExpire()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        override fun onLogout(errorCode: Int, info: String?) {
            try {
                for (listener in userStateChangeListeners) {
                    listener.onLogout(errorCode, info)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

    }

    private inner class InnerChatMessageListener: ChatMessageListener {

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
                // Check if it is a custom message first
                if (it.type == ChatMessageType.CUSTOM) {
                    val body = it.body as ChatCustomMessageBody
                    val event = body.event()
                    val msgType: UICustomMsgType? = getCustomMsgType(event)

                    // Then exclude single chat
                    if (it.chatType != ChatType.Chat){
                        val username: String = it.to
                        // Check if it is the same chat room or group and event is not empty
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

        private fun getCustomMsgType(event: String?): UICustomMsgType? {
            return if (TextUtils.isEmpty(event)) {
                null
            } else UICustomMsgType.fromName(event)
        }

        private fun parseGiftMsg(msg: ChatMessage): GiftEntityProtocol? {
            var userEntity:UserInfoProtocol? = null
            if (msg.ext().containsKey(UIConstant.CHATROOM_UIKIT_USER_INFO)){
                val jsonObject = msg.getStringAttribute(UIConstant.CHATROOM_UIKIT_USER_INFO)
                userEntity = GsonTools.toBean(jsonObject.toString(), UserInfoProtocol::class.java)
                userEntity?.let {
                    chatroomUser.setUserInfo(msg.from, it.toUser())
                }
            }
            if (msg.body is ChatCustomMessageBody){
                val customBody = msg.body as ChatCustomMessageBody
                if (customBody.params.containsKey(UIConstant.CHATROOM_UIKIT_GIFT_INFO)){
                    val gift = customBody.params[UIConstant.CHATROOM_UIKIT_GIFT_INFO]
                    val giftEntityProtocol = GsonTools.toBean(gift, GiftEntityProtocol::class.java)
                    userEntity?.let {
                        giftEntityProtocol?.sendUser = it
                    }
                    return giftEntityProtocol
                }
            }
            return null
        }

        private fun parseJoinedMsg(msg: ChatMessage):UserInfoProtocol? {
            if (msg.ext().containsKey(UIConstant.CHATROOM_UIKIT_USER_INFO)){
                return try {
                    val jsonObject = msg.getStringAttribute(UIConstant.CHATROOM_UIKIT_USER_INFO)
                    return GsonTools.toBean(jsonObject.toString(), UserInfoProtocol::class.java)
                }catch (e:ChatException){
                    null
                }
            }
            return null
        }

    }
}