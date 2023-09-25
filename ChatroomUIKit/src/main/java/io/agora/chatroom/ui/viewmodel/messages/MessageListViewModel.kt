package io.agora.chatroom.ui.viewmodel.messages

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.agora.chatroom.service.ChatMessage
import io.agora.chatroom.ui.commons.ComposeChatListController
import io.agora.chatroom.ui.commons.ComposeMessagesState
import io.agora.chatroom.ui.compose.ComposeMessageItemState
import io.agora.chatroom.ui.compose.ComposeSelectedMessageState
import io.agora.chatroom.ui.compose.SelectedMessageOptionsState
import io.agora.chatroom.ui.compose.chatmessagelist.MessageFocusRemoved
import io.agora.chatroom.ui.compose.chatmessagelist.MessageFocused
import io.agora.chatroom.ui.theme.primaryColor8
import io.agora.chatroom.ui.theme.secondaryColor8
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

@Suppress("TooManyFunctions", "LargeClass", "TooManyFunctions")
class MessageListViewModel(
    private val isDarkTheme: Boolean = false,
    private val messageLimit: Int = DefaultMessageLimit,
    private val showDateSeparators: Boolean = true,
    private val showLabel: Boolean = true,
    private val showGift: Boolean = true,
    private val showAvatar: Boolean = true,
    private val dateSeparatorColor: Color = secondaryColor8,
    private val nickNameColor: Color = primaryColor8,
    private val dateSeparatorThresholdMillis: Long = TimeUnit.HOURS.toMillis(DateSeparatorDefaultHourThreshold),
    private val composeChatListController: ComposeChatListController
    ): ViewModel() {

    /**
     * State of the screen.
     */
//    private var messagesState: ComposeMessagesState = composeChatListController.getMessageStatus

    private var messagesState:  ComposeMessagesState by mutableStateOf(composeChatListController.getMessageStatus)


    val getTheme: Boolean
        get() = isDarkTheme

    val isShowDateSeparators:Boolean
        get() = showDateSeparators

    val isShowLabel:Boolean
        get() = showLabel

    val isShowGift:Boolean
        get() = showGift

    val isShowAvatar:Boolean
        get() = showAvatar

    val getDateSeparatorColor:Color
        get() = dateSeparatorColor

    val getNickNameColor:Color
        get() = nickNameColor

    /**
     * Represents the message we wish to scroll to.
     */
    private var scrollToMessage: ChatMessage? = null


    /**
     * Holds information about the abilities the current user
     * is able to exercise in the given channel.
     *
     * e.g. send messages, delete messages, etc...
     */
    private val ownCapabilities: StateFlow<Set<String>> = MutableStateFlow(mutableSetOf())


    fun refreshMessage(message:ChatMessage){
        viewModelScope.launch {
            composeChatListController.refreshMessage(message)
        }
    }


    /**
     * Triggered when the user long taps on and selects a message.
     *
     * @param message The selected message.
     */
    public fun selectMessage(message: ChatMessage?) {
        changeSelectMessageState(
            message?.let {
                SelectedMessageOptionsState(
                    message = it,
                    ownCapabilities = ownCapabilities.value
                )
            }
        )
    }

    /**
     * Triggered when the user loads more data by reaching the end of the current messages.
     */
    public fun isShowLoadMore() {
        messagesState = messagesState.copy(isLoadingMore = true)
    }


    /**
     * If there's an error, we just set the current state to be empty - 'isLoading' as false and
     * 'messages' as an empty list.
     */
    private fun showEmptyState() {
        messagesState = messagesState.copy(isLoading = false, messageItems = emptyList())
    }

    /**
     * Clears the [ComposeMessagesState] from our UI state, after the user taps on the "Scroll to bottom"
     * or "New Message" actions in the list or simply scrolls to the bottom.
     */
    public fun clearMessageState() {
        messagesState = messagesState.copy(messageDirectionState = null)
    }

    /**
     * Resets the [ComposeMessagesState]s, to remove the message overlay, by setting 'selectedMessage' to null.
     */
    public fun removeOverlay() {
        messagesState = messagesState.copy(selectedMessageState = null)
    }

    /**
     * Changes the state of [messagesState] depending
     * on the thread mode.
     *
     * @param selectedMessageState The selected message state.
     */
    private fun changeSelectMessageState(selectedMessageState: ComposeSelectedMessageState?) {
        messagesState = messagesState.copy(selectedMessageState = selectedMessageState)
    }

    /**
     * State handler for the UI, which holds all the information the UI needs to render messages.
     *
     * It chooses between [messagesState] based on if we're in a thread or not.
     */
    public val currentMessagesState: ComposeMessagesState
        get() = messagesState

    /**
     * Loads the selected message we wish to scroll to when the message can't be found in the current list.
     *
     * @param message The selected message we wish to scroll to.
     */
    private fun loadMessage(message: ChatMessage) {

    }


    /**
     * Scrolls to message if in list otherwise get the message from backend.
     *
     * @param message The message we wish to scroll to.
     */
    public fun scrollToSelectedMessage(message: ChatMessage) {
        val isMessageInList = currentMessagesState.messageItems.firstOrNull {
            it is ComposeMessageItemState && it.message.msgId == message.msgId
        } != null

        if (isMessageInList) {
            focusMessage(message.msgId)
        } else {
            scrollToMessage = message
            loadMessage(message = message)
        }
    }

    /**
     * Sets the focused message to be the message with the given ID, after which it removes it from
     * focus with a delay.
     *
     * @param messageId The ID of the message.
     */
    public fun focusMessage(messageId: String) {
        val messages = currentMessagesState.messageItems.map {
            if (it is ComposeMessageItemState && it.message.msgId == messageId) {
                it.copy(focusState = MessageFocused)
            } else {
                it
            }
        }

        viewModelScope.launch {
            composeChatListController.updateMessages(messages)
            delay(RemoveMessageFocusDelay)
            removeMessageFocus(messageId)
        }
    }

    /**
     * Removes the focus from the message with the given ID.
     *
     * @param messageId The ID of the message.
     */
    private fun removeMessageFocus(messageId: String) {
        val messages = currentMessagesState.messageItems.map {
            if (it is ComposeMessageItemState && it.message.msgId == messageId) {
                it.copy(focusState = MessageFocusRemoved)
            } else {
                it
            }
        }

        if (scrollToMessage?.msgId == messageId) {
            scrollToMessage = null
        }

        composeChatListController.updateMessages(messages)
    }

    internal companion object {
        /**
         * The default threshold for showing date separators. If the message difference in hours is equal to this
         * number, then we show a separator, if it's enabled in the list.
         */
        internal const val DateSeparatorDefaultHourThreshold: Long = 4

        /**
         * The default limit for messages count in requests.
         */
        internal const val DefaultMessageLimit: Int = 30

        /**
         * Time in millis, after which the focus is removed.
         */
        private const val RemoveMessageFocusDelay: Long = 2000

    }
}