package io.agora.chatroom.ui.commons

import io.agora.chatroom.model.UserInfoProtocol
import io.agora.chatroom.ui.model.UICapabilities

/**
 * Represents the state within the message input.
 *
 * @param inputValue The current text value that's within the input.
 * @param currentUser The currently logged in user.
 */

public data class ComposerMessageState(
    val inputValue: String = "",
    val ownCapabilities: Set<String> = setOf(UICapabilities.SEND_MESSAGE),
    val validationErrors: List<UIValidationError> = emptyList(),
    val currentUser: UserInfoProtocol? = null,
)
