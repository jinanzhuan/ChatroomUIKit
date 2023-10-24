package io.agora.chatroom

import android.content.Context
import io.agora.chatroom.model.UIChatroomInfo
import io.agora.chatroom.model.UICommonConfig
import io.agora.chatroom.service.cache.UIChatroomCacheManager

class UIChatroomContext{
    private lateinit var mCommonConfig: UICommonConfig
    private lateinit var mChatroomInfo: UIChatroomInfo
    var context: Context? = null

    companion object {
        const val TAG = "UIChatroomContext"
    }

    fun setRoomContext(context: Context){
        this.context = context
    }

    fun setCommonConfig(config: UICommonConfig) {
        mCommonConfig = config
    }

    fun getCommonConfig(): UICommonConfig {
        return mCommonConfig
    }

    fun setCurrentRoomInfo(info: UIChatroomInfo){
        mChatroomInfo = info
    }

    fun getCurrentRoomInfo(): UIChatroomInfo{
        return mChatroomInfo
    }

    fun setCurrentTheme(isDark:Boolean){
        UIChatroomCacheManager.getInstance().setCurrentTheme(isDark)
    }

    fun getCurrentTheme():Boolean{
        return UIChatroomCacheManager.getInstance().getCurrentTheme()
    }

    fun setUseGiftsInList(use:Boolean){
        UIChatroomCacheManager.getInstance().setUseGiftsInMsg(use)
    }

    fun getUseGiftsInMsg():Boolean{
        return UIChatroomCacheManager.getInstance().getUseGiftsInMsg()
    }

}