package io.agora.chatroom.viewmodel.gift

import io.agora.chatroom.ChatroomUIKitClient
import io.agora.chatroom.compose.gift.ComposeGiftItemState
import io.agora.chatroom.compose.gift.ComposeGiftListItemState
import io.agora.chatroom.model.UIConstant
import io.agora.chatroom.model.UserInfoProtocol
import io.agora.chatroom.service.ChatMessage
import io.agora.chatroom.service.GiftEntityProtocol
import io.agora.chatroom.service.GiftReceiveListener
import io.agora.chatroom.ui.UIChatroomService
import io.agora.chatroom.utils.GsonTools
import io.agora.chatroom.viewmodel.ComposeBaseListViewModel

class ComposeGiftListViewModel(
    private val giftItems: List<ComposeGiftItemState> = emptyList(),
    private val service: UIChatroomService,
): ComposeBaseListViewModel<ComposeGiftListItemState>(
    contentList = giftItems
), GiftReceiveListener {

    fun registerGiftListener() {
        service.getGiftService().bindGiftListener(this)
    }

    private fun unregisterGiftListener() {
        service.getGiftService().unbindGiftListener(this)
    }

    override fun onCleared() {
        super.onCleared()
        unregisterGiftListener()
    }

    override fun onGiftReceived(roomId: String, gift: GiftEntityProtocol?, message: ChatMessage) {
        super.onGiftReceived(roomId, gift, message)
        if (!ChatroomUIKitClient.getInstance().getContext().getUseGiftsInMsg()){
            gift?.let { giftEntity ->
                ChatroomUIKitClient.getInstance().parseUserInfo(message)?.let {
                    giftEntity.sendUser = it
                }
                addDateToIndex(data = ComposeGiftItemState(giftEntity))
            }
        }
    }

//    private fun checkUserInfo(message: ChatMessage):UserInfoProtocol?{
//        if (message.ext().containsKey(UIConstant.CHATROOM_UIKIT_USER_INFO)) {
//            val jsonObject = message.getStringAttribute(UIConstant.CHATROOM_UIKIT_USER_INFO)
//            return GsonTools.toBean(jsonObject.toString(), UserInfoProtocol::class.java)
//        }
//        return null
//    }
}