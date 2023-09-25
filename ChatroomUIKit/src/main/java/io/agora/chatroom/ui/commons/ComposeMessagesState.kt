package io.agora.chatroom.ui.commons

import androidx.compose.ui.unit.IntSize
import io.agora.chatroom.model.UserInfoProtocol
import io.agora.chatroom.ui.compose.ComposeMessageDirectionState
import io.agora.chatroom.ui.compose.ComposeMessageListItemState
import io.agora.chatroom.ui.compose.ComposeSelectedMessageState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * UI representation of the Conversation/Messages screen. Holds all the data required to show messages.
 *
 * @param messageItems Message items to represent in the list.
 * @param currentUser The data of the current user, required for various UI states.
 * @param messageDirectionState The state that represents any new messages.
 */
public data class ComposeMessagesState(
    val isLoading: Boolean = false,
    val isLoadingMore: Boolean = false,
    val endOfMessages: Boolean = false,
    val messageItems: List<ComposeMessageListItemState> = emptyList(),
    val selectedMessageState: ComposeSelectedMessageState? = null,
    val currentUser: UserInfoProtocol? = null,
    val parentMessageId: String? = null,
    val messageDirectionState: ComposeMessageDirectionState? = null,
    val unreadCount: Int = 0,
) {

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
