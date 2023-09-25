package io.agora.chatroom.service


/**
 * The interface for handling chatroom messages.
 */
interface MessageHandleService {

    /**
     * Send a text message to the chatroom.
     * @param message The text message to send.
     * @param roomId The id of the chatroom.
     * @param onSuccess The callback to indicate the message is sent successfully.
     * @param onError The callback to indicate the message is failed to send.
     */
    fun sendTextMessage(message: String,
                        roomId: String = "",
                        onSuccess: (ChatMessage) -> Unit,
                        onError: OnError)

    /**
     * Send a target text message to the chatroom.
     * @param targetUserIds The target user ids to send the message.
     * @param message The text message to send.
     * @param roomId The id of the chatroom.
     * @param onSuccess The callback to indicate the message is sent successfully.
     * @param onError The callback to indicate the message is failed to send.
     */
    fun sendTargetTextMessage(targetUserIds: List<String>,
                              message: String,
                              roomId: String = "",
                              onSuccess: OnValueSuccess<ChatMessage>,
                              onError: OnError)

    /**
     * Send a custom message to the chatroom.
     * @param event The event of the custom message.
     * @param ext The extension of the custom message.
     * @param roomId The id of the chatroom.
     * @param onSuccess The callback to indicate the message is sent successfully.
     * @param onError The callback to indicate the message is failed to send.
     */
    fun sendTargetCustomMessage(targetUserIds: List<String>,
                                event: String,
                                ext: Map<String, String>,
                                roomId: String = "",
                                onSuccess: (ChatMessage) -> Unit,
                                onError: OnError)

    /**
     * Send a message to the chatroom.
     * @param message The message to send.
     * @param onSuccess The callback to indicate the message is sent successfully.
     * @param onError The callback to indicate the message is failed to send.
     * @param onProgress The callback to indicate the progress of sending the message.
     */
    fun sendMessage(message: ChatMessage?,
                    onSuccess: OnValueSuccess<ChatMessage>,
                    onError: OnError,
                    onProgress: OnProgress = {})

    /**
     * Translate a text message.
     * @param message The text message to translate.
     * @param onSuccess The callback to indicate the message is translated successfully.
     * @param onError The callback to indicate the message is failed to translate.
     */
    fun translateTextMessage(message: ChatMessage?,
                             onSuccess: OnValueSuccess<ChatMessage>,
                             onError: OnError)
}

interface GiftMessageHandleService {
    fun sendGift(gift: GiftEntity, onSuccess: OnSuccess, onError: OnError)
}

interface MessageListener {
    /**
     * The callback to indicate a message is received.
     * @param message The message received.
     */
    fun onMessageReceived(message: ChatMessage){}

    fun refreshMessage(message: ChatMessage){}
}

interface GiftReceiveListener {

    fun onGiftReceived(gift: GiftEntity){}
}