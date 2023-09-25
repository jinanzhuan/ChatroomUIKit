package io.agora.chatroom.model

import io.agora.chatroom.service.ChatUserInfo


data class UserInfoProtocol(
    var identify:String? = ""
): ChatUserInfo()