package io.agora.chatroom.ui.commons

import io.agora.chatroom.ui.model.UIUserThumbnailInfo

/**
 * Represents the state within the message input.
 *
 * @param inputValue The current text value that's within the input.
 * @param type The message type.
 * @param mentionSuggestions The list of users that can be used to autocomplete the mention.
 * @param currentUser The currently logged in user.
 */

public data class MessageComposerState(
    val inputValue: String = "",
    val ownCapabilities: Set<String> = setOf(),
    val UIValidationErrors: List<UIValidationError> = emptyList(),
    val mentionSuggestions: List<UIUserThumbnailInfo> = emptyList(),
    val currentUser: UIUserThumbnailInfo? = null,
)
