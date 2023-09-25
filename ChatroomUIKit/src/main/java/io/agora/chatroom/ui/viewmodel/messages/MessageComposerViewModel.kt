package io.agora.chatroom.ui.viewmodel.messages

import androidx.lifecycle.ViewModel
import io.agora.chatroom.service.ChatMessage
import io.agora.chatroom.ui.commons.ComposerChatBarController
import io.agora.chatroom.ui.commons.ComposerMessageState
import io.agora.chatroom.ui.model.UIChatBarMenuItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class MessageComposerViewModel(
    private val isDarkTheme:Boolean,
    private val composerChatBarController: ComposerChatBarController,
    private val menuItemResource: List<UIChatBarMenuItem>,
) : ViewModel(){

    /**
     * The full UI state that has all the required data.
     */
    public val composerMessageState: StateFlow<ComposerMessageState> = composerChatBarController.state

    /**
     * UI state of the current composer input.
     */
    public val input: MutableStateFlow<String> = composerChatBarController.input

    fun updateInputValue(){
        composerChatBarController.updateInputValue()
    }

    val getTheme: Boolean
        get() = isDarkTheme

    val getMenuItem:List<UIChatBarMenuItem>
        get() = menuItemResource

    fun refreshMessage(message:ChatMessage){
        composerChatBarController.refreshMessage(message = message)
    }


    /**
     * Builds a new [ChatMessage] to send to our API. Based on the internal state, we use the current action's message and
     * apply the given changes.
     *
     * If we're not editing a message, we'll fill in the required data for the message.
     *
     * @param message Message text.
     *
     * @return [ChatMessage] object, with all the data required to send it to the API.
     */
    public fun buildNewMessage(
        message: String,
    ): ChatMessage = composerChatBarController.buildNewMessage(message)

    /**
     * Called when the input changes and the internal state needs to be updated.
     *
     * @param value Current state value.
     */
    public fun setMessageInput(value: String): Unit = composerChatBarController.setMessageInput(value)


    /**
     * Clears the input and the current state of the composer.
     */
    public fun clearData(): Unit = composerChatBarController.clearData()


    /**
     * Disposes the inner [ComposerChatBarController].
     */
    override fun onCleared() {
        super.onCleared()
    }




}