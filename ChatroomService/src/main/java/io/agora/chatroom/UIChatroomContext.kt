package io.agora.chatroom

import io.agora.chatroom.model.UICommonConfig
import io.agora.chatroom.service.cache.UIChatroomCacheManager

class UIChatroomContext {
    private var instance: UIChatroomContext? = null
    private var mCommonConfig: UICommonConfig = UICommonConfig()

    @Synchronized
    fun shared(): UIChatroomContext? {
        if (instance == null) {
            instance = UIChatroomContext()
        }
        return instance
    }

    fun setCommonConfig(config: UICommonConfig) {
        mCommonConfig = config
    }

    fun getCommonConfig(): UICommonConfig {
        return mCommonConfig
    }

    fun setCurrentTheme(isDark:Boolean){
        UIChatroomCacheManager.cacheManager.setCurrentTheme(isDark)
    }

    fun getCurrentTheme():Boolean{
        return UIChatroomCacheManager.cacheManager.getCurrentTheme()
    }
}