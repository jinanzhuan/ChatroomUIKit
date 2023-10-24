package io.agora.chatroom.viewmodel.gift

import io.agora.chatroom.compose.gift.ComposeGiftItemState
import io.agora.chatroom.compose.gift.ComposeGiftListItemState
import io.agora.chatroom.viewmodel.ComposeBaseListViewModel

class ComposeGiftListViewModel(
    private val giftItems: List<ComposeGiftItemState> = emptyList(),
): ComposeBaseListViewModel<ComposeGiftListItemState>(
    contentList = giftItems
) {


}