package io.agora.chatroom.compose.gift

import io.agora.chatroom.service.ChatMessage

sealed class ComposeGiftListItemState

data class ComposeGiftItemState(
    val message: ChatMessage
): ComposeGiftListItemState()