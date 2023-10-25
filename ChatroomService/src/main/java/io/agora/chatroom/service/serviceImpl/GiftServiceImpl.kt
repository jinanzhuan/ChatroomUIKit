package io.agora.chatroom.service.serviceImpl

import io.agora.CallBack
import io.agora.chatroom.ChatroomUIKitClient
import io.agora.chatroom.model.UIConstant
import io.agora.chatroom.service.ChatClient
import io.agora.chatroom.service.ChatCustomMessageBody
import io.agora.chatroom.service.ChatMessage
import io.agora.chatroom.service.ChatMessageType
import io.agora.chatroom.service.ChatType
import io.agora.chatroom.service.GiftEntityProtocol
import io.agora.chatroom.service.GiftReceiveListener
import io.agora.chatroom.service.GiftService
import io.agora.chatroom.service.OnError
import io.agora.chatroom.service.OnValueSuccess
import io.agora.chatroom.utils.GsonTools
import org.json.JSONObject

class GiftServiceImpl: GiftService {
    private val chatManager by lazy { ChatClient.getInstance().chatManager() }
    private val listeners = mutableListOf<GiftReceiveListener>()
    override fun bindGiftListener(listener: GiftReceiveListener) {
        if (!listeners.contains(listener)){
            listeners.add(listener)
            ChatroomUIKitClient.getInstance().updateChatroomGiftListener(listeners)
        }
    }

    override fun unbindGiftListener(listener: GiftReceiveListener) {
        if (listeners.contains(listener)) {
            listeners.remove(listener)
            ChatroomUIKitClient.getInstance().updateChatroomGiftListener(listeners)
        }
    }

    override fun sendGift(entity: GiftEntityProtocol, onSuccess: OnValueSuccess<ChatMessage>, onError: OnError) {
        val message = ChatMessage.createSendMessage(ChatMessageType.CUSTOM)
        val customBody = ChatCustomMessageBody(UIConstant.CHATROOM_UIKIT_GIFT)
        entity.sendUserId = ChatroomUIKitClient.getInstance().getCurrentUser().userId
        val gift = GsonTools.beanToString(entity)
//        customBody.params[UIConstant.CHATROOM_UIKIT_GIFT_INFO] = gift
        message.setAttribute(UIConstant.CHATROOM_UIKIT_GIFT_INFO, gift?.let { JSONObject(it) })
        message.chatType = ChatType.ChatRoom
        message.body = customBody
        message.to = ChatroomUIKitClient.getInstance().getContext().getCurrentRoomInfo().roomId
        message.setMessageStatusCallback(object : CallBack{
            override fun onSuccess() {
                onSuccess.invoke(message)
            }

            override fun onError(code: Int, error: String?) {
                onError.invoke(code,error)
            }
        })
        chatManager.sendMessage(message)
    }
}