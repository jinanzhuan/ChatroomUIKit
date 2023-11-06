package io.agora.chatroom.bean

data class RoomDetailBean(
    val affiliations: List<Any>,
    val affiliations_count: Int,
    val created: Long,
    val description: String,
    val ext: Ext,
    val iconKey: String,
    val id: String,
    val maxusers: Int,
    val mute: Boolean,
    val name: String,
    val nickname: String,
    val owner: String,
    val persistent: Boolean,
    val showid: Int,
    val status: String,
    val video_type: String
)

data class Ext(
    val videoUrl: String
)