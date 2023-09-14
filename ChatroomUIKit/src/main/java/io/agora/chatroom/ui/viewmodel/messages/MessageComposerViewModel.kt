package io.agora.chatroom.ui.viewmodel.messages

import androidx.lifecycle.ViewModel
import io.agora.chatroom.model.UIMessage
import io.agora.chatroom.ui.commons.MessageComposerController
import io.agora.chatroom.ui.commons.MessageComposerState
import io.agora.chatroom.ui.model.UIUserThumbnailInfo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class MessageComposerViewModel(
    private val messageComposerController: MessageComposerController,
) : ViewModel(){

    /**
     * The full UI state that has all the required data.
     */
    public val messageComposerState: StateFlow<MessageComposerState> = messageComposerController.state

    /**
     * UI state of the current composer input.
     */
    public val input: MutableStateFlow<String> = messageComposerController.input


    /**
     * Autocompletes the current text input with the mention from the selected user.
     *
     * @param user The user that is used to autocomplete the mention.
     */
    public fun selectMention(user: UIUserThumbnailInfo): Unit = messageComposerController.selectMention(user)


    /**
     * Sends a given message using our Stream API. Based on the internal state, we either edit an existing message,
     * or we send a new message, using our API.
     *
     * It also dismisses any current message actions.
     *
     * @param message The message to send.
     */
    public fun sendMessage(message: UIMessage): Unit = messageComposerController.sendMessage(message)

    /**
     * Builds a new [UIMessage] to send to our API. Based on the internal state, we use the current action's message and
     * apply the given changes.
     *
     * If we're not editing a message, we'll fill in the required data for the message.
     *
     * @param message Message text.
     *
     * @return [UIMessage] object, with all the data required to send it to the API.
     */
    public fun buildNewMessage(
        message: String,
    ): UIMessage = messageComposerController.buildNewMessage(message)

    /**
     * Called when the input changes and the internal state needs to be updated.
     *
     * @param value Current state value.
     */
    public fun setMessageInput(value: String): Unit = messageComposerController.setMessageInput(value)


    /**
     * Clears the input and the current state of the composer.
     */
    public fun clearData(): Unit = messageComposerController.clearData()


    /**
     * Disposes the inner [MessageComposerController].
     */
    override fun onCleared() {
        super.onCleared()
    }




}