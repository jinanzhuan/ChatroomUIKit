package io.agora.chatroom.model

import io.agora.chat.UserInfo
import io.agora.chatroom.service.UserEntity

fun UserInfoProtocol.transfer() = UserInfo().run {
    this.userId = userId
    this.nickname = nickname
    this.avatarUrl = avatarUrl
    this.gender = gender
    this.ext = identify
    this
}

fun UserInfoProtocol.innerTransfer() = UserEntity(userId, nickname, avatarUrl, gender, identify)
data class UserInfoProtocol(
    val userId: String,
    val nickname: String? = "",
    val avatarUrl: String? = "",
    val gender: Int? = 0,
    var identify:String? = ""
)