package io.agora.chatroom

import android.content.Context
import android.util.Log
import io.agora.chatroom.model.UIChatroomInfo
import io.agora.chatroom.model.UICommonConfig

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
        Log.e("apex","setCurrentRoomInfo")
        mChatroomInfo = info
    }

    fun isCurrentOwner(ownerId:String?):Boolean{
        if (ownerId?.isEmpty() == true){
            mChatroomInfo.roomOwner?.let {
                return it.userId == ChatroomUIKitClient.getInstance().getCurrentUser().userId
            }
        }else{
            return ChatroomUIKitClient.getInstance().getCurrentUser().userId == ownerId
        }
        return false
    }

    fun getCurrentRoomInfo(): UIChatroomInfo{
        Log.e("apex","getCurrentRoomInfo")
        return mChatroomInfo
    }

    fun setCurrentTheme(isDark:Boolean){
        ChatroomUIKitClient.getInstance().getCacheManager().setCurrentTheme(isDark)
    }

    fun getCurrentTheme():Boolean{
        return ChatroomUIKitClient.getInstance().getCacheManager().getCurrentTheme()
    }

    fun setUseGiftsInList(use:Boolean){
        ChatroomUIKitClient.getInstance().getCacheManager().setUseGiftsInMsg(use)
    }

    fun getUseGiftsInMsg():Boolean{
        return ChatroomUIKitClient.getInstance().getCacheManager().getUseGiftsInMsg()
    }

}