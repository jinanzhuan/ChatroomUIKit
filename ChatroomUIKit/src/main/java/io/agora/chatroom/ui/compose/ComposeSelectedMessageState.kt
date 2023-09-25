package io.agora.chatroom.ui.compose

import io.agora.chatroom.service.ChatMessage

/**
 * Represents a state when a message or its reactions were selected.
 *
 * @param message The selected message.
 * @param ownCapabilities Set of capabilities the user is given for the current channel.
 */
public sealed class
ComposeSelectedMessageState(public val message: ChatMessage, public val ownCapabilities: Set<String>)

/**
 * Represents a state when a message was selected.
 */
public class SelectedMessageOptionsState(message: ChatMessage, ownCapabilities: Set<String>) :
    ComposeSelectedMessageState(message, ownCapabilities)

