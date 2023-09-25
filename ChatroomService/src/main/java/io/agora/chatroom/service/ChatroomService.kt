package io.agora.chatroom.service

enum class UserOperationType {
    /**
     * Add an admin.
     */
    ADD_ADMIN,

    /**
     * Remove an admin.
     */
    REMOVE_ADMIN,

    /**
     * Mute a user.
     */
    MUTE,

    /**
     * Unmute a user.
     */
    UNMUTE,

    /**
     * Block a user.
     */
    BLOCK,

    /**
     * Unblock a user.
     */
    UNBLOCK
}
interface ChatroomService: MessageHandleService {

    /**
     * Bind a listener to the chatroom.
     * @param listener The listener to bind.
     */
    fun bindListener(listener: ChatroomChangeListener)

    /**
     * Unbind a listener from the chatroom.
     * @param listener The listener to unbind.
     */
    fun unbindListener(listener: ChatroomChangeListener)

    /**
     * Join a chatroom.
     * @param roomId The id of the chatroom.
     * @param userId The id of the user.
     * @param onSuccess The callback to indicate the user joined the chatroom successfully.
     * @param onError The callback to indicate the user failed to join the chatroom.
     */
    fun joinChatroom(roomId: String,
                    userId: String,
                    onSuccess: OnValueSuccess<Chatroom>,
                    onError: OnError)

    /**
     * Leave a chatroom.
     * @param roomId The id of the chatroom
     */
    fun leaveChatroom(roomId: String,
                      userId: String,
                      onSuccess: OnSuccess,
                      onError: OnError)

    fun getAnnouncement(roomId: String,
                       onSuccess: OnValueSuccess<String?>,
                       onError: OnError)

    fun updateAnnouncement(roomId: String,
                           announcement: String,
                           onSuccess: OnSuccess,
                           onError: OnError)

    fun operateUser(roomId: String,
                    userId: String,
                    operation: UserOperationType,
                    onSuccess: OnValueSuccess<Chatroom>,
                    onError: OnError)
}

interface ChatroomChangeListener: MessageListener {

    /**
     * Callback when the user joined the chatroom.
     * @param roomId The id of the chatroom.
     * @param userId The id of the user.
     */
    fun onUserJoined(roomId: String, userId: String){}

    /**
     * Callback when the user left the chatroom.
     * @param roomId The id of the chatroom.
     * @param userId The id of the user.
     */
    fun onUserLeft(roomId: String, userId: String){}

    /**
     * Callback when the user is kicked.
     * @param roomId The id of the chatroom.
     * @param userId The id of the user.
     */
    fun onUserBeKicked(roomId: String, userId: String){}

    /**
     * Callback when the user is muted.
     * @param roomId The id of the chatroom.
     * @param userId The id of the user.
     */
    fun onUserMuted(roomId: String, userId: String){}

    /**
     * Callback when the user is unmuted.
     * @param roomId The id of the chatroom.
     * @param userId The id of the user.
     */
    fun onUserUnmuted(roomId: String, userId: String){}

    /**
     * Callback when the user is been added to admins.
     * @param roomId The id of the chatroom.
     * @param userId The id of the user.
     */
    fun onAdminAdded(roomId: String, userId: String){}

    /**
     * Callback when the user is been removed from admins.
     * @param roomId The id of the chatroom.
     * @param userId The id of the user.
     */
    fun onAdminRemoved(roomId: String, userId: String){}

    /**
     * Callback when the announcement of the chatroom is updated.
     * @param roomId The id of the chatroom.
     * @param announcement The announcement of the chatroom.
     */
    fun onAnnouncementUpdated(roomId: String, announcement: String){}
}