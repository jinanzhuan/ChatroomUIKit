package io.agora.chatroom.model

import android.content.Context

data class UICommonConfig(
    val context: Context,
    val languageList:List<String> = mutableListOf(),


)