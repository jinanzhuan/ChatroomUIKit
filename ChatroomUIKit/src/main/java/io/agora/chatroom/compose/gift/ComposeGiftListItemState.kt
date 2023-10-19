package io.agora.chatroom.compose.gift

import io.agora.chatroom.service.GiftEntity

sealed class ComposeGiftListItemState

data class ComposeGiftItemState(
    val gift: GiftEntity
): ComposeGiftListItemState()