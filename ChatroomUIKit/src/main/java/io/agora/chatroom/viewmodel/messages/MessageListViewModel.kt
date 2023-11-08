package io.agora.chatroom.viewmodel.messages

import androidx.lifecycle.ViewModel
import io.agora.chatroom.ChatroomUIKitClient
import io.agora.chatroom.service.ChatMessage
import io.agora.chatroom.commons.ComposeChatListController
import io.agora.chatroom.commons.ComposeMessageListState
import io.agora.chatroom.compose.chatmessagelist.ComposeMessageListItemState
import io.agora.chatroom.service.ChatroomChangeListener
import io.agora.chatroom.service.GiftEntityProtocol
import io.agora.chatroom.service.GiftReceiveListener
import io.agora.chatroom.service.OnError
import io.agora.chatroom.service.OnSuccess
import io.agora.chatroom.service.OnValueSuccess
import io.agora.chatroom.ui.UIChatroomService

class MessageListViewModel(
    private val isDarkTheme: Boolean? = false,
    private val showDateSeparators: Boolean = true,
    private val showLabel: Boolean = true,
    private val showAvatar: Boolean = true,
    private val roomId: String,
    private val chatService: UIChatroomService,
    private val composeChatListController: ComposeChatListController
    ): ViewModel(), ChatroomChangeListener, GiftReceiveListener {

    /**
     * Register chatroom change listener
     */
    fun registerChatroomChangeListener() {
        chatService.getChatService().bindListener(this)
        chatService.getGiftService().bindGiftListener(this)
    }


    /**
     * Unregister chatroom change listener
     */
    private fun unRegisterChatroomChangeListener() {
        chatService.getChatService().unbindListener(this)
        chatService.getGiftService().unbindGiftListener(this)
    }

    /**
     * Register chatroom gift listener
     */
    fun registerChatroomGiftListener() {
        chatService.getGiftService().bindGiftListener(this)
    }

    /**
     * Unregister chatroom gift listener
     */
    private fun unRegisterChatroomGiftListener() {
        chatService.getGiftService().unbindGiftListener(this)
    }

    override fun onCleared() {
        super.onCleared()
        unRegisterChatroomChangeListener()
        unRegisterChatroomGiftListener()
    }

    override fun onMessageReceived(message: ChatMessage) {
        super.onMessageReceived(message)
        addTextMessageByIndex(message = message)
    }

    override fun onGiftReceived(roomId: String, gift: GiftEntityProtocol?, message: ChatMessage) {
        super.onGiftReceived(roomId, gift, message)
        if (ChatroomUIKitClient.getInstance().getUseGiftsInMsg()){
            gift?.let { giftEntity ->
                ChatroomUIKitClient.getInstance().parseUserInfo(message)?.let{
                    giftEntity.sendUser = it
                }
                addGiftMessageByIndex(message = message, gift = giftEntity)
            }
        }
    }

    /**
     * Send gift message
     */
    fun sendGift(gift: GiftEntityProtocol, onSuccess: OnValueSuccess<ChatMessage>, onError: OnError) {
        chatService.getGiftService().sendGift(gift = gift, onSuccess = {
            message ->
            onSuccess.invoke(message)
        }, onError)
    }

    /**
     * Send a text message.
     */
    fun sendTextMessage(message: String,
                        onSuccess: (ChatMessage) -> Unit = {},
                        onError: OnError = {_, _ ->}) {
        chatService.getChatService().sendTextMessage(message, roomId, onSuccess = {
            msg ->
            addTextMessageByIndex(message = msg)
            onSuccess.invoke(msg)
        }, onError)
    }

    fun translateMessage(message: ChatMessage) {
        chatService.getChatService().translateTextMessage(message, onSuccess = {
                msg ->
            updateTextMessage(message = msg)
        }, onError = {code, error ->})
    }

    fun removeMessage(message: ChatMessage?, onSuccess: OnSuccess, onError: OnError) {
        chatService.getChatService().recallMessage(message, onSuccess = {
            removeMessage(message = message!!)
            onSuccess.invoke()
        }, onError)
    }

    fun addTextMessage(message:ChatMessage){
        composeChatListController.addTextMessage(message)
    }

    fun addTextMessageByIndex(index:Int = 0,message:ChatMessage){
        composeChatListController.addTextMessage(index = index,message = message)
    }

    fun addGiftMessageByIndex(index:Int = 0,message:ChatMessage,gift:GiftEntityProtocol){
        composeChatListController.addGiftMessage(index = index,message = message, gift = gift)
    }

    fun addGiftMessage(message:ChatMessage,gift:GiftEntityProtocol){
        composeChatListController.addGiftMessage(message,gift)
    }

    fun addJoinedMessageByIndex(index:Int = 0,message:ChatMessage){
        composeChatListController.addJoinedMessage(index = index, message = message)
    }

    fun addJoinedMessage(message:ChatMessage){
        composeChatListController.addJoinedMessage(message)
    }

    fun removeMessage(message: ComposeMessageListItemState){
        composeChatListController.removeMessage(message)
    }

    fun removeMessage(message: ChatMessage): Boolean {
        composeChatListController.getMessage(message.msgId)?.let {
            composeChatListController.removeMessage(it)
            return true
        }
        return false
    }

    fun removeMessageByIndex(index: Int){
        composeChatListController.removeMessageByIndex(index)
    }

    fun updateTextMessage(message: ChatMessage){
        composeChatListController.updateTextMessage(message = message)
    }

    fun clearMessage(){
        composeChatListController.clearMessage()
    }

    val currentComposeMessageListState: ComposeMessageListState
        get() = composeChatListController.currentComposeMessageListState


    val getTheme: Boolean?
        get() = isDarkTheme

    val isShowDateSeparators:Boolean
        get() = showDateSeparators

    val isShowLabel:Boolean
        get() = showLabel

    val isShowAvatar:Boolean
        get() = showAvatar

}