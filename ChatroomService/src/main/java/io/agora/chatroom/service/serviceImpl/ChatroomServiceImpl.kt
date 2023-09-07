package io.agora.chatroom.service.serviceImpl

import io.agora.chatroom.service.CallbackImpl
import io.agora.chatroom.service.ChatCallback
import io.agora.chatroom.service.ChatClient
import io.agora.chatroom.service.ChatError
import io.agora.chatroom.service.ChatMessage
import io.agora.chatroom.service.ChatTextMessageBody
import io.agora.chatroom.service.ChatType
import io.agora.chatroom.service.Chatroom
import io.agora.chatroom.service.ChatroomChangeListener
import io.agora.chatroom.service.ChatroomService
import io.agora.chatroom.service.OnError
import io.agora.chatroom.service.OnSuccess
import io.agora.chatroom.service.OnValueSuccess
import io.agora.chatroom.service.UserOperationType
import io.agora.chatroom.service.ValueCallbackImpl

class ChatroomServiceImpl: ChatroomService {

    private val listeners = mutableListOf<ChatroomChangeListener>()
    private val chatroomManager by lazy { ChatClient.getInstance().chatroomManager() }
    private val chatManager by lazy { ChatClient.getInstance().chatManager() }
    override fun bindListener(listener: ChatroomChangeListener) {
        if (listeners.contains(listener)) listeners.add(listener)
    }

    override fun unbindListener(listener: ChatroomChangeListener) {
        if (listeners.contains(listener)) listeners.remove(listener)
    }

    override fun joinChatroom(
        roomId: String,
        userId: String,
        onSuccess: OnValueSuccess<Chatroom>,
        onError: OnError
    ) {
        if (userId.isEmpty() or roomId.isEmpty()) {
            onError(ChatError.INVALID_PARAM, "")
            return
        }
        chatroomManager.joinChatRoom(roomId, ValueCallbackImpl(onSuccess, onError))
    }

    override fun leaveChatroom(
        roomId: String,
        userId: String,
        onSuccess: OnSuccess,
        onError: OnError
    ) {
        if (userId.isEmpty() or roomId.isEmpty()) {
            onError(ChatError.INVALID_PARAM, "")
            return
        }
        chatroomManager.leaveChatRoom(roomId)
    }

    override fun getAnnouncement(
        roomId: String,
        onSuccess: OnValueSuccess<String?>,
        onError: OnError
    ) {
        if (roomId.isEmpty()) {
            onError(ChatError.INVALID_PARAM, "")
            return
        }
        chatroomManager.asyncFetchChatRoomAnnouncement(roomId, ValueCallbackImpl<String>(onSuccess, onError))
    }

    override fun updateAnnouncement(
        roomId: String,
        announcement: String,
        onSuccess: OnSuccess,
        onError: OnError
    ) {
        if (roomId.isEmpty()) {
            onError(ChatError.INVALID_PARAM, "")
            return
        }
        chatroomManager.asyncUpdateChatRoomAnnouncement(roomId, announcement, CallbackImpl(onSuccess, onError))
    }

    override fun operateUser(
        roomId: String,
        userId: String,
        operation: UserOperationType,
        onSuccess: OnValueSuccess<Chatroom>,
        onError: OnError
    ) {
        if (userId.isEmpty() or roomId.isEmpty()) {
            onError(ChatError.INVALID_PARAM, "")
            return
        }
        when(operation) {
            UserOperationType.ADD_ADMIN  -> {
                chatroomManager.asyncAddChatRoomAdmin(roomId, userId, ValueCallbackImpl(onSuccess, onError))
            }
            UserOperationType.REMOVE_ADMIN -> {
                chatroomManager.asyncRemoveChatRoomAdmin(roomId, userId, ValueCallbackImpl(onSuccess, onError))
            }
            UserOperationType.MUTE -> {
                chatroomManager.asyncMuteChatRoomMembers(roomId, mutableListOf(userId), Long.MAX_VALUE, ValueCallbackImpl(onSuccess, onError))
            }
            UserOperationType.UNMUTE -> {
                chatroomManager.asyncUnMuteChatRoomMembers(roomId, mutableListOf(userId), ValueCallbackImpl(onSuccess, onError))
            }
            UserOperationType.BLOCK -> {
                chatroomManager.asyncBlockChatroomMembers(roomId, mutableListOf(userId), ValueCallbackImpl(onSuccess, onError))
            }
            UserOperationType.UNBLOCK -> {
                chatroomManager.asyncUnBlockChatRoomMembers(roomId, mutableListOf(userId), ValueCallbackImpl(onSuccess, onError))
            }
        }
    }

    override fun sendTextMessage(
        message: String,
        roomId: String,
        onSuccess: (ChatMessage) -> Unit,
        onError: OnError
    ) {
        if (roomId.isEmpty()) {
            onError(ChatError.INVALID_PARAM, "")
            return
        }
        val textSendMessage = ChatMessage.createTextSendMessage(message, roomId)
        textSendMessage?.chatType = ChatType.ChatRoom
        sendMessage(textSendMessage, onSuccess, onError) {}
    }

    override fun sendTargetTextMessage(
        targetUserIds: List<String>,
        message: String,
        roomId: String,
        onSuccess: (ChatMessage) -> Unit,
        onError: OnError
    ) {
        if (roomId.isEmpty()) {
            onError(ChatError.INVALID_PARAM, "")
            return
        }
    }

    override fun sendTargetCustomMessage(
        targetUserIds: List<String>,
        event: String,
        ext: Map<String, String>,
        roomId: String,
        onSuccess: (ChatMessage) -> Unit,
        onError: OnError
    ) {
        if (roomId.isEmpty()) {
            onError(ChatError.INVALID_PARAM, "")
            return
        }
    }

    override fun sendMessage(
        message: ChatMessage?,
        onSuccess: (ChatMessage) -> Unit,
        onError: OnError,
        onProgress: (Int) -> Unit
    ) {
        if (message == null) {
            onError(ChatError.MESSAGE_INVALID, "")
            return
        }
        message.setMessageStatusCallback(object : ChatCallback {
            override fun onSuccess() {
                onSuccess(message)
            }

            override fun onError(code: Int, error: String?) {
                onError(code, error)
            }

            override fun onProgress(progress: Int, status: String?) {
                onProgress(progress)
            }
        })
        chatManager.sendMessage(message)
    }

    override fun translateTextMessage(
        message: ChatMessage?,
        onSuccess: (ChatMessage) -> Unit,
        onError: OnError
    ) {
        chatManager.translateMessage(message, (message?.body as ChatTextMessageBody).targetLanguages, ValueCallbackImpl<ChatMessage>(onSuccess, onError))
    }
}