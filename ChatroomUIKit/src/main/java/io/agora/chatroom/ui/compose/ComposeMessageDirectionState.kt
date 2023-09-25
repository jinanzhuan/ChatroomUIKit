package io.agora.chatroom.ui.compose

/**
 * Represents the state when a new message arrives to the channel.
 */
public sealed class ComposeMessageDirectionState

/**
 * If the message is our own (we sent it), we scroll to the bottom of the list.
 */
public object MyOwn : ComposeMessageDirectionState() { override fun toString(): String = "Send" }

/**
 * If the message is someone else's (we didn't send it), we show a "New message" bubble.
 */
public object Other : ComposeMessageDirectionState() { override fun toString(): String = "Receive" }
