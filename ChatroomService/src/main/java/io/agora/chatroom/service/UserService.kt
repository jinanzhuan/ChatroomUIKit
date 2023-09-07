package io.agora.chatroom.service


interface UserService {
    fun bindUserStateChangeListener(listener: UserStateChangeListener)
    fun unbindUserStateChangeListener(listener: UserStateChangeListener)

    fun getUserInfo(userId: String, onSuccess: OnValueSuccess<UserEntity>, onError: OnError)

    fun getUserInfoList(userIdList: List<String>, onSuccess: OnValueSuccess<List<UserEntity>>, onError: OnError)

    fun updateUserInfo(userEntity: UserEntity, onSuccess: OnSuccess, onError: OnError)

    fun login(userId: String, token: String, onSuccess: OnSuccess, onError: OnError)

    fun logout(userId: String, onSuccess: OnSuccess, onFailure: OnError)
}

interface UserStateChangeListener {

}

data class UserEntity(val userId: String,
    val nickname: String,
    val avatar: String,
    val gender: Int,
    val identify: String)