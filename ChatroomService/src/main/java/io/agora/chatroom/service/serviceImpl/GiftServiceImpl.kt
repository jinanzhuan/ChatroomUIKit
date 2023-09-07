package io.agora.chatroom.service.serviceImpl

import io.agora.chatroom.service.CallbackImpl
import io.agora.chatroom.service.ChatCustomMessageBody
import io.agora.chatroom.service.ChatMessage
import io.agora.chatroom.service.ChatMessageType
import io.agora.chatroom.service.ChatType
import io.agora.chatroom.service.GiftEntity
import io.agora.chatroom.service.GiftReceiveListener
import io.agora.chatroom.service.GiftService
import io.agora.chatroom.service.OnError
import io.agora.chatroom.service.OnSuccess

class GiftServiceImpl: GiftService {
    companion object {
        @JvmStatic val GIFT_EVENT = "chatroom_UIKit_gift"
        @JvmStatic val GIFT_KEY = "gift"
    }
    private val listeners = mutableListOf<GiftReceiveListener>()
    override fun bindGiftListener(listener: GiftReceiveListener) {
        if (listeners.contains(listener)) listeners.add(listener)
    }

    override fun unbindGiftListener(listener: GiftReceiveListener) {
        if (listeners.contains(listener)) listeners.remove(listener)
    }

    override fun sendGift(gift: GiftEntity, onSuccess: OnSuccess, onError: OnError) {
        val message = ChatMessage.createSendMessage(ChatMessageType.CUSTOM)
        val customBody = ChatCustomMessageBody(GIFT_EVENT)
        // todo: 将 gift 对象转为 json 格式
        customBody.params[GIFT_KEY] = gift.toString()
        message.chatType = ChatType.ChatRoom
        message.body = customBody
        message.setMessageStatusCallback(CallbackImpl(onSuccess, onError))
    }
}