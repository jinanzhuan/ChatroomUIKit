package io.agora.chatroom.commons

import android.content.Context
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.style.ImageSpan
import android.util.Log
import io.agora.chatroom.model.UserInfoProtocol
import io.agora.chatroom.service.ChatroomService
import io.agora.chatroom.compose.utils.DispatcherProvider
import io.agora.chatroom.compose.utils.ExpressionUtils
import io.agora.chatroom.data.emojiMap
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class ComposerChatBarController(
    private val context: Context,
    private val roomId: String,
    private val chatService: ChatroomService,
    private val capabilities: Set<String> = setOf(),
){

    /**
     * Full message composer state holding all the required information.
     */
    val state: MutableStateFlow<ComposerInputMessageState> = MutableStateFlow(ComposerInputMessageState())

    /**
     * UI state of the current composer input.
     */
    val input: MutableStateFlow<String> = MutableStateFlow("")

    private val emoji: MutableStateFlow<SpannableStringBuilder> = MutableStateFlow(SpannableStringBuilder())

    private val ownCapabilities = MutableStateFlow<Set<String>>(capabilities)


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
    val validationErrors: MutableStateFlow<List<UIValidationError>> = MutableStateFlow(emptyList())


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

    /**
     * Called when the input changes and the internal state needs to be updated.
     *
     * @param value Current state value.
     */
    fun setMessageInput(value: String) {
        this.input.value = value
    }

    fun setEmojiInput(value: String){
        this.emoji.value = checkEmoji(value)
    }

    private fun checkEmoji(selectEmoji:String):SpannableStringBuilder{
        val spBuild = SpannableStringBuilder(selectEmoji)
        val emojiRes = emojiMap[selectEmoji]
        val emojiDrawable = emojiRes?.let { context.getDrawable(it) }
        emojiDrawable?.setBounds(0, 0, emojiDrawable.intrinsicWidth, emojiDrawable.intrinsicHeight)
        val imageSpan = emojiDrawable?.let { ImageSpan(it, ImageSpan.ALIGN_BOTTOM) }
        val startIndex = selectEmoji.indexOf(selectEmoji)
        if (startIndex >= 0) {
            val endIndex: Int = startIndex + selectEmoji.length
            spBuild.setSpan(
                imageSpan,
                startIndex,
                endIndex,
                SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }
        return spBuild
    }

    fun clearData() {
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

        emoji.onEach { input ->
            state.value = state.value.copy(emoji = input)

        }.debounce(ComputeMentionSuggestionsDebounceTime).launchIn(scope)

        validationErrors.onEach { validationErrors ->
            state.value = state.value.copy(validationErrors = validationErrors)
        }.launchIn(scope)

        ownCapabilities.onEach { ownCapabilities ->
            state.value = state.value.copy(ownCapabilities = ownCapabilities)
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