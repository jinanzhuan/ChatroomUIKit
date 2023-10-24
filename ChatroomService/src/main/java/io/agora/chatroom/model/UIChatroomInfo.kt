package io.agora.chatroom.model

import java.io.Serializable

class UIChatroomInfo(
    var roomId:String,
    var roomOwner:UserInfoProtocol?

):UICreateRoomInfo(), Serializable