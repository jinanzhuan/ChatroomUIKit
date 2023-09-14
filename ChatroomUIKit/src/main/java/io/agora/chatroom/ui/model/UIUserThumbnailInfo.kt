package io.agora.chatroom.ui.model

import java.io.Serializable

data class UIUserThumbnailInfo(
    var userId:String,
    var userName:String,
    var userAvatar:String,
): Serializable
