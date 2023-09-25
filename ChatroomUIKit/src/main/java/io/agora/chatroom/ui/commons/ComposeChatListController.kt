package io.agora.chatroom.ui.commons

import io.agora.chatroom.service.ChatroomService

class ComposeChatListController(
    private val roomId: String,
    private val chatService: ChatroomService
):ComposeMessageController(){


}