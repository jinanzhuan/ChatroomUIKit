package io.agora.chatroom.service.serviceImpl

import io.agora.chatroom.service.CallbackImpl
import io.agora.chatroom.service.ChatClient
import io.agora.chatroom.service.ChatUserInfo
import io.agora.chatroom.service.ChatValueCallback
import io.agora.chatroom.service.OnError
import io.agora.chatroom.service.OnSuccess
import io.agora.chatroom.service.OnValueSuccess
import io.agora.chatroom.service.UserEntity
import io.agora.chatroom.service.UserService
import io.agora.chatroom.service.UserStateChangeListener

fun ChatUserInfo.transfer() = UserEntity(userId, nickname, avatarUrl, gender, ext)
fun UserEntity.transfer(): ChatUserInfo {
    val user = ChatUserInfo()
    user.userId = userId
    user.nickname = nickname
    user.avatarUrl = avatar
    user.gender = gender
    user.ext = identify
    return user
}

class UserServiceImpl: UserService {
    private val listeners = mutableListOf<UserStateChangeListener>()
    private val userInfoManager by lazy { ChatClient.getInstance().userInfoManager() }
    override fun bindUserStateChangeListener(listener: UserStateChangeListener) {
        if (listeners.contains(listener)) listeners.add(listener)
    }

    override fun unbindUserStateChangeListener(listener: UserStateChangeListener) {
        if (listeners.contains(listener)) listeners.remove(listener)
    }

    override fun getUserInfo(userId: String,
                             onSuccess: OnValueSuccess<UserEntity>,
                             onError: OnError
    ) {
        getUserInfoList(arrayListOf(userId), onSuccess = { onSuccess.invoke(it[0]) }, onError)
    }

    override fun getUserInfoList(
        userIdList: List<String>,
        onSuccess: OnValueSuccess<List<UserEntity>>,
        onError: OnError
    ) {
        userInfoManager.fetchUserInfoByUserId(userIdList.toTypedArray(), object :ChatValueCallback<Map<String, ChatUserInfo>> {
            override fun onSuccess(value: Map<String, ChatUserInfo>?) {
                val userEntities = value?.map {
                    it.value.transfer()
                } ?: listOf()
                onSuccess.invoke(userEntities)
            }

            override fun onError(error: Int, errorMsg: String?) {
                onError.invoke(error, errorMsg)
            }
        })
    }

    override fun updateUserInfo(
        userEntity: UserEntity,
        onSuccess: OnSuccess,
        onError: OnError
    ) {
        userInfoManager.updateOwnInfo(userEntity.transfer(), object :ChatValueCallback<String> {
            override fun onSuccess(value: String?) {
                onSuccess.invoke()
            }

            override fun onError(error: Int, errorMsg: String?) {
                onError.invoke(error, errorMsg)
            }
        })
    }

    override fun login(userId: String, token: String, onSuccess: OnSuccess, onError: OnError) {
        ChatClient.getInstance().loginWithAgoraToken(userId, token, CallbackImpl(onSuccess, onError))
    }

    override fun logout(userId: String, onSuccess: OnSuccess, onError: OnError) {
        ChatClient.getInstance().logout(true, CallbackImpl(onSuccess, onError))
    }
}