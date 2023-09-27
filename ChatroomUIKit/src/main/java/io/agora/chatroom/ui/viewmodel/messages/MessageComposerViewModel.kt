package io.agora.chatroom.ui.viewmodel.messages

import androidx.lifecycle.ViewModel
import io.agora.chatroom.ui.commons.ComposerChatBarController
import io.agora.chatroom.ui.commons.ComposerInputMessageState
import io.agora.chatroom.ui.model.UIChatBarMenuItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class MessageComposerViewModel(
    private val isDarkTheme:Boolean?,
    private val composerChatBarController: ComposerChatBarController,
    private val menuItemResource: List<UIChatBarMenuItem>,
) : ViewModel(){

    /**
     * The full UI state that has all the required data.
     */
    val composerMessageState: StateFlow<ComposerInputMessageState> = composerChatBarController.state

    /**
     * UI state of the current composer input.
     */
    val input: MutableStateFlow<String> = composerChatBarController.input

    fun updateInputValue(){
        composerChatBarController.updateInputValue()
    }

    val getTheme: Boolean?
        get() = isDarkTheme

    val getMenuItem:List<UIChatBarMenuItem>
        get() = menuItemResource

    /**
     * Called when the input changes and the internal state needs to be updated.
     *
     * @param value Current state value.
     */
    fun setMessageInput(value: String): Unit = composerChatBarController.setMessageInput(value)


    /**
     * Clears the input and the current state of the composer.
     */
    fun clearData(): Unit = composerChatBarController.clearData()


    /**
     * Disposes the inner [ComposerChatBarController].
     */
    override fun onCleared() {
        super.onCleared()
    }




}