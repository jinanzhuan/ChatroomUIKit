package io.agora.chatroom

import android.app.Application
import io.agora.chatroom.commons.GlobalConfig
import io.agora.chatroom.model.UICommonConfig
import java.util.Random

class ChatroomApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        //初始化配置信息

        UICommonConfig(
            context = applicationContext,
            languageList = listOf(GlobalConfig.targetLanguage.code)
        )

        ChatroomUIKitClient.getInstance().setUp(this,BuildConfig.CHATROOM_APP_KEY)
    }

    private fun randomAvatar(): String {
        val randomValue = Random().nextInt(8) + 1
        return "https://accktvpic.oss-cn-beijing.aliyuncs.com/pic/sample_avatar/sample_avatar_${randomValue}.png"
    }

    private fun randomUserName(): String {
        val userNames = arrayListOf(
            "安迪",
            "路易",
            "汤姆",
            "杰瑞",
            "杰森",
            "布朗",
            "吉姆",
            "露西",
            "莉莉",
            "韩梅梅",
            "李雷",
            "张三",
            "李四",
            "小红",
            "小明",
            "小刚",
            "小霞",
            "小智",
        )
        val randomValue = Random().nextInt(userNames.size) + 1
        return userNames[randomValue % userNames.size]
    }
}