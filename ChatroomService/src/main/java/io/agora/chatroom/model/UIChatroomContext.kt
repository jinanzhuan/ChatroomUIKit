package io.agora.chatroom.model

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
}