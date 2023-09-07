package io.agora.chatroom.service

// manager
typealias ChatClient = io.agora.chat.ChatClient
typealias ChatRoomManager = io.agora.chat.ChatRoomManager
typealias ChatUserInfoManager = io.agora.chat.UserInfoManager

// callback
typealias ChatCallback = io.agora.CallBack
typealias ChatValueCallback<T> = io.agora.ValueCallBack<T>

typealias ChatException = io.agora.exceptions.ChatException
typealias ChatError =  io.agora.Error
// java bean
typealias Chatroom =  io.agora.chat.ChatRoom
typealias ChatUserInfo = io.agora.chat.UserInfo

// ChatMessage
typealias ChatMessage = io.agora.chat.ChatMessage
typealias ChatType = io.agora.chat.ChatMessage.ChatType
typealias ChatMessageType = io.agora.chat.ChatMessage.Type
typealias ChatTextMessageBody = io.agora.chat.TextMessageBody
typealias ChatCustomMessageBody = io.agora.chat.CustomMessageBody