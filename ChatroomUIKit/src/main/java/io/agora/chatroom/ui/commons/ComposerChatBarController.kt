package io.agora.chatroom.ui.commons

import io.agora.chat.TextMessageBody
import io.agora.chatroom.model.UserInfoProtocol
import io.agora.chatroom.service.ChatMessage
import io.agora.chatroom.service.ChatMessageType
import io.agora.chatroom.service.ChatType
import io.agora.chatroom.service.ChatroomService
import io.agora.chatroom.ui.compose.utils.DispatcherProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class ComposerChatBarController(
    private val roomId: String,
    private val chatService: ChatroomService
):ComposeMessageController(){

    /**
     * Full message composer state holding all the required information.
     */
    public val state: MutableStateFlow<ComposerMessageState> = MutableStateFlow(ComposerMessageState())

    /**
     * UI state of the current composer input.
     */
    public val input: MutableStateFlow<String> = MutableStateFlow("")

    /**
     * Creates a [CoroutineScope] that allows us to cancel the ongoing work when the parent
     * ViewModel is disposed.
     *
     * We use the [DispatcherProvider.Immediate] variant here to make sure the UI updates don't go through the
     * process of dispatching events. This fixes several bugs where the input state breaks when deleting or typing
     * really fast.
     */
    private val scope = CoroutineScope(DispatcherProvider.Immediate)

    /**
     * Represents the list of validation errors for the current text input and the currently selected attachments.
     */
    public val validationErrors: MutableStateFlow<List<UIValidationError>> = MutableStateFlow(emptyList())


    /**
     * Represents the list of users in the channel.
     */
    private var users: List<UserInfoProtocol> = emptyList()

    /**
     * Represents the maximum allowed message length in the message input.
     */
    private var maxMessageLength: Int = DefaultMaxMessageLength

    /**
     * Gets the current text input in the message composer.
     */
    private val messageText: String
        get() = input.value


    public fun buildNewMessage(
        message: String
    ): ChatMessage {
        val sendMessage = ChatMessage.createSendMessage(ChatMessageType.TXT)
        val textMessageBody = TextMessageBody(message)
        sendMessage.to = roomId
        sendMessage.chatType = ChatType.ChatRoom
        sendMessage.body = textMessageBody
        return sendMessage
    }

    /**
     * Called when the input changes and the internal state needs to be updated.
     *
     * @param value Current state value.
     */
    public fun setMessageInput(value: String) {
        this.input.value = value
    }


    public fun clearData() {
        input.value = ""
        validationErrors.value = emptyList()
    }


    init {
        setupComposerState()
    }
    @OptIn(FlowPreview::class)
    fun updateInputValue(){
        input.onEach { input ->
            state.value = state.value.copy(inputValue = input)

            handleValidationErrors()
        }.debounce(ComputeMentionSuggestionsDebounceTime).launchIn(scope)
    }

    /**
     * Sets up the observing operations for various composer states.
     */
    @OptIn(FlowPreview::class)
    private fun setupComposerState() {
        input.onEach { input ->
            state.value = state.value.copy(inputValue = input)

            handleValidationErrors()
        }.debounce(ComputeMentionSuggestionsDebounceTime).launchIn(scope)

        validationErrors.onEach { validationErrors ->
            state.value = state.value.copy(validationErrors = validationErrors)
        }.launchIn(scope)

    }

    /**
     * Checks the current input for validation errors.
     */
    private fun handleValidationErrors() {
        validationErrors.value = mutableListOf<UIValidationError>().apply {
            val message = input.value
            val messageLength = message.length

            if (messageLength > maxMessageLength) {
                add(
                    UIValidationError.MessageLengthExceeded(
                        messageLength = messageLength,
                        maxMessageLength = maxMessageLength
                    )
                )
            }
        }
    }


    private companion object {
        /**
         * The default allowed number of input message.
         */
        private const val DefaultMaxMessageLength: Int = 300

        /**
         * The default limit for messages count in requests.
         */
        private const val DefaultMessageLimit: Int = 30

        /**
         * The amount of time we debounce computing mention suggestions.
         * We debounce those computations in the case of being unable to find mentions from local data, we will query
         * the BE for members.
         */
        private const val ComputeMentionSuggestionsDebounceTime = 300L

        /**
         * Pagination offset for the member query.
         */
        private const val queryMembersRequestOffset: Int = 0

        /**
         * The upper limit of members the query is allowed to return.
         */
        private const val queryMembersMemberLimit: Int = 30
    }

}