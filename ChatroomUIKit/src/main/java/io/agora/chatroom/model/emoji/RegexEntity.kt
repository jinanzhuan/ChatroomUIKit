package io.agora.chatroom.model.emoji

data class RegexEntity(
    val startIndex:Int,
    val endIndex:Int,
    val emojiTag:String,
    val emojiIcon:Int,
    var count:Int = 1
)
