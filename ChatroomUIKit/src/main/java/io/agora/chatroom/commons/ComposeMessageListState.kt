package io.agora.chatroom.commons

import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.unit.IntSize
import io.agora.chatroom.compose.chatmessagelist.ComposeMessageListItemState
import io.agora.chatroom.compose.chatmessagelist.ComposeSelectedMessageState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * UI representation of the Conversation/Messages screen. Holds all the data required to show messages.
 *
 * @param messageItems Message items to represent in the list.
 */
data class ComposeMessageListState(
    var isLoading: Boolean = false,
    private val messageItems: List<ComposeMessageListItemState> = emptyList(),
    private val selectedMessageState: ComposeSelectedMessageState? = null,
) {
    private val _messages: MutableList<ComposeMessageListItemState> = messageItems.toMutableStateList()
    val messages: List<ComposeMessageListItemState> = _messages

    fun addMessage(msg: ComposeMessageListItemState) {
        _messages.add(0, msg)
    }
    fun removeMessage(msg: ComposeMessageListItemState){
        if (_messages.contains(msg)  ){
            _messages.remove(msg)
        }
    }

    fun clearMessage(){
        _messages.clear()
    }

    /**
     * Notifies the UI of the calculated message offset to center it on the screen.
     */
    private val _focusedMessageOffset: MutableStateFlow<Int?> = MutableStateFlow(null)
    val focusedMessageOffset: StateFlow<Int?> = _focusedMessageOffset

    /**
     * Calculates the message offset needed for the message to center inside the list on scroll.
     *
     * @param parentSize The size of the list which contains the message.
     * @param focusedMessageSize The size of the message item we wish to bring to the center and focus.
     */
    public fun calculateMessageOffset(parentSize: IntSize, focusedMessageSize: IntSize) {
        val sizeDiff = parentSize.height - focusedMessageSize.height
        if (sizeDiff > 0) {
            _focusedMessageOffset.value = -sizeDiff / 2
        } else {
            _focusedMessageOffset.value = -sizeDiff
        }
    }
}