package io.agora.chatroom.viewmodel.gift

import io.agora.chatroom.compose.gift.ComposeGiftListItemState
import io.agora.chatroom.viewmodel.ComposeBaseListViewModel

class ComposeGiftListViewModel(
    private val giftItems: List<ComposeGiftListItemState> = emptyList(),
): ComposeBaseListViewModel<ComposeGiftListItemState>(
    contentList = giftItems
) {


}