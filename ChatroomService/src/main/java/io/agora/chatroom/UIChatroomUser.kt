package io.agora.chatroom

import io.agora.chatroom.service.UserEntity
import io.agora.chatroom.service.cache.UIChatroomCacheManager

class UIChatroomUser {

    fun getUserInfo(userId:String): UserEntity {
        return UIChatroomCacheManager.getInstance().getUserInfo(userId)
    }

    fun setUserInfo(userId:String,userInfo: UserEntity){
        UIChatroomCacheManager.getInstance().saveUserInfo(userId,userInfo)
    }
}