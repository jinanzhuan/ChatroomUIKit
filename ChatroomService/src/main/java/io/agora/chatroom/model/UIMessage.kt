package io.agora.chatroom.model

data class UIMessage(
    var msgId:String?=null,
    var content:String?=null,
    var to:String?=null,
    var msgType:UIMessageType? = UIMessageType.Text,
    var msgStatus:UIMessageStatus? = UIMessageStatus.Fail
)

enum class UIMessageType{
    Text,
    Custom
}

enum class UIMessageStatus{
    Success,
    Fail,
}