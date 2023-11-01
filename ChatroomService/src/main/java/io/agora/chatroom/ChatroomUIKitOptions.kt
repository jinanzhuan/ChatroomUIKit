package io.agora.chatroom

data class ChatroomUIKitOptions(
    var chatOptions: ChatSDKOptions = ChatSDKOptions()
)

/**
 * The options for Chatroom UIKit.
 * @param enableDebug Whether to enable debug mode.
 * @param autoLogin Whether to automatically log in.
 * @param useUserProperties Whether to use user properties.
 */
data class ChatSDKOptions(
    var enableDebug: Boolean = false,
    val autoLogin: Boolean = false,
    val useUserProperties: Boolean = true
)