package io.agora.chatroom

import io.agora.chatroom.model.UICommonConfig
import io.agora.chatroom.model.UserInfoProtocol
import io.agora.chatroom.service.cache.UIChatroomCacheManager

class UIChatroomContext{
    private lateinit var mCommonConfig: UICommonConfig

    companion object {
        const val TAG = "ChatroomUIKitClient"
        private var instance: UIChatroomContext? = null

        fun getInstance(): UIChatroomContext {
            if (instance == null) {
                synchronized(UIChatroomContext::class) {
                    if (instance == null) {
                        instance = UIChatroomContext()
                    }
                }
            }
            return instance!!
        }
    }

    fun setCommonConfig(config: UICommonConfig) {
        mCommonConfig = config
    }

    fun getCommonConfig(): UICommonConfig {
        return mCommonConfig
    }

    fun setCurrentTheme(isDark:Boolean){
        UIChatroomCacheManager.getInstance(mCommonConfig.context).setCurrentTheme(isDark)
    }

    fun getCurrentTheme():Boolean{
        return UIChatroomCacheManager.getInstance(mCommonConfig.context).getCurrentTheme()
    }

    fun setUseGiftsInList(use:Boolean){
        UIChatroomCacheManager.getInstance(mCommonConfig.context).setUseGiftsInMsg(use)
    }

    fun getUseGiftsInMsg():Boolean{
        return UIChatroomCacheManager.getInstance(mCommonConfig.context).getUseGiftsInMsg()
    }

    fun getUserInfo(userId:String): UserInfoProtocol {
       return UIChatroomCacheManager.getInstance(mCommonConfig.context).getUserInfo(userId)
    }

    fun setUserInfo(userId:String,userInfo:UserInfoProtocol){
        UIChatroomCacheManager.getInstance(mCommonConfig.context).saveUserInfo(userId,userInfo)
    }
}