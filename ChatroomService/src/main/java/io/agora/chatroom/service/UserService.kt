package io.agora.chatroom.service

import io.agora.chatroom.model.UserInfoProtocol


interface UserService {
    fun bindUserStateChangeListener(listener: UserStateChangeListener)
    fun unbindUserStateChangeListener(listener: UserStateChangeListener)

    fun getUserInfo(userId: String, onSuccess: OnValueSuccess<UserInfoProtocol>, onError: OnError)

    fun getUserInfoList(userIdList: List<String>, onSuccess: OnValueSuccess<List<UserInfoProtocol>>, onError: OnError)

    fun updateUserInfo(userEntity: UserInfoProtocol, onSuccess: OnSuccess, onError: OnError)

    fun login(userId: String, token: String, onSuccess: OnSuccess, onError: OnError)

    fun login(user: UserInfoProtocol,token: String,userProperties: Boolean = true,onSuccess: OnSuccess, onError: OnError)

    fun logout(onSuccess: OnSuccess, onFailure: OnError)
}

interface UserStateChangeListener {

}

data class UserEntity(
    val userId: String,
    val nickname: String? = "",
    val avatar: String? = "",
    val gender: Int? = 0,
    val identify: String? = "",
    val role: ROLE = ROLE.MEMBER
)

enum class ROLE {
    OWNER,
    ADMIN,
    MEMBER
}

fun ChatUserInfo.transfer() = UserEntity(userId, nickname, avatarUrl, gender,ext )
fun UserEntity.transfer(): UserInfoProtocol = UserInfoProtocol(userId, nickname, avatar, gender, identify)