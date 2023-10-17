package io.agora.chatroom.service

interface GiftService : GiftMessageHandleService {

    fun bindGiftListener(listener: GiftReceiveListener)

    fun unbindGiftListener(listener: GiftReceiveListener)

}

data class GiftEntity(
    val giftId: String,
    val giftName: String,
    val gitPrice: Double,
    val giftCount: Int,
    val giftIcon: String,
    val giftEffect: String,
//    val selected: Boolean,
    val sendUserId: String
)