package io.agora.chatroom.ui

import io.agora.chatroom.ChatroomUIKitClient
import io.agora.chatroom.model.UIChatroomInfo
import io.agora.chatroom.service.Chatroom
import io.agora.chatroom.service.ChatroomService
import io.agora.chatroom.service.GiftService
import io.agora.chatroom.service.OnValueSuccess
import io.agora.chatroom.service.UserService
import io.agora.chatroom.service.serviceImpl.ChatroomServiceImpl
import io.agora.chatroom.service.serviceImpl.GiftServiceImpl
import io.agora.chatroom.service.serviceImpl.UserServiceImpl

class UIChatroomService constructor(
    private var roomInfo: UIChatroomInfo
) {
    private val TAG = "UIChatroomService"

    private val chatImpl: ChatroomService by lazy { ChatroomServiceImpl() }

    private val giftImpl: GiftService by lazy { GiftServiceImpl() }

    private val userImpl:UserService by lazy { UserServiceImpl() }


    fun getGiftService() = giftImpl
    fun getChatService() = chatImpl
    fun getUserService() = userImpl
    fun getRoomInfo() = roomInfo

    fun joinRoom(onSuccess: OnValueSuccess<Chatroom>, onFailure: (Int,String?) -> Unit){
        val userId = ChatroomUIKitClient.getInstance().getCurrentUser().userId
        chatImpl.joinChatroom( roomInfo.roomId,userId,
            onSuccess = { onSuccess.invoke(it)},
            onError = { code, error ->
                onFailure.invoke(code,error)
            }
        )
    }
}
