package io.agora.chatroom.service.serviceImpl

import android.util.Log
import io.agora.chatroom.service.CallbackImpl
import io.agora.chatroom.service.ChatClient
import io.agora.chatroom.service.ChatCustomMessageBody
import io.agora.chatroom.service.ChatMessage
import io.agora.chatroom.service.ChatMessageType
import io.agora.chatroom.service.ChatType
import io.agora.chatroom.service.GiftEntity
import io.agora.chatroom.service.GiftReceiveListener
import io.agora.chatroom.service.GiftService
import io.agora.chatroom.service.OnError
import io.agora.chatroom.service.OnSuccess
import io.agora.chatroom.utils.GsonTools

class GiftServiceImpl: GiftService {
    private val chatManager by lazy { ChatClient.getInstance().chatManager() }
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

    override fun sendGift(data: GiftEntity, onSuccess: OnSuccess, onError: OnError) {
        val message = ChatMessage.createSendMessage(ChatMessageType.CUSTOM)
        val customBody = ChatCustomMessageBody(GIFT_EVENT)
        data.sendUserId = "apex1"
        val gift = GsonTools.beanToString(data)
        Log.e("apex","sendGift $gift")
        customBody.params[GIFT_KEY] = gift
        message.chatType = ChatType.ChatRoom
        message.body = customBody
        message.to = "193314355740675"
        message.setMessageStatusCallback(CallbackImpl(onSuccess, onError))
        chatManager.sendMessage(message)
    }
}