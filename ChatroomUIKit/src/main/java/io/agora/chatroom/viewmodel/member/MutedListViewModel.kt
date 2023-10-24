package io.agora.chatroom.viewmodel.member

import io.agora.chatroom.service.OnValueSuccess
import io.agora.chatroom.service.UserEntity
import io.agora.chatroom.service.cache.UIChatroomCacheManager
import io.agora.chatroom.ui.UIChatroomService

data class MutedListViewModel(
    private val roomId: String,
    private val service: UIChatroomService,
    private val pageSize: Int = 10
): MemberListViewModel(roomId, service, pageSize) {

    /**
     * Fetches the mute list.
     */
    fun fetchMuteList(onSuccess: OnValueSuccess<List<UserEntity>> = {}) {
        loading()
        val muteList = UIChatroomCacheManager.getInstance().getMuteList()
        muteList.map { userId ->
            UIChatroomCacheManager.getInstance().getUserInfo(userId)
        }.let {
            add(it)
            onSuccess.invoke(it)
        }
    }
}