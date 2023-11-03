package io.agora.chatroom.model

data class UICommonConfig(
    val languageList:List<String> = mutableListOf(),
    val isOpenAutoClearGiftList:Boolean = false,
    val autoClearTime:Long = 3000L,
)