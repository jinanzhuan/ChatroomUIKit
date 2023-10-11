package io.agora.chatroom

import io.agora.chatroom.model.UICommonConfig
import io.agora.chatroom.service.cache.UIChatroomCacheManager

class UIChatroomContext
private constructor() {
    private var mCommonConfig: UICommonConfig = UICommonConfig()

    companion object {
        const val TAG = "ChatroomUIKitClient"
        val shared: UIChatroomContext by lazy { UIChatroomContext() }
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