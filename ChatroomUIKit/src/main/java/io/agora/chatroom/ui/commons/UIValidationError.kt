package io.agora.chatroom.ui.commons


/**
 * Represents a validation error for the user input.
 */
public sealed class UIValidationError {
    /**
     * Represents a validation error that happens when the message length in the message input
     * exceed the maximum allowed message length.
     *
     * @param messageLength The current message length in the message input.
     * @param maxMessageLength The maximum allowed message length that we exceeded.
     */
    public data class MessageLengthExceeded(
        val messageLength: Int,
        val maxMessageLength: Int,
    ) : UIValidationError()

}
