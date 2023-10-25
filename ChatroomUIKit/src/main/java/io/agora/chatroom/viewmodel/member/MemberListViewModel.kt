package io.agora.chatroom.viewmodel.member

import android.util.Log
import io.agora.chatroom.model.toUser
import io.agora.chatroom.service.OnError
import io.agora.chatroom.service.OnValueSuccess
import io.agora.chatroom.service.UserEntity
import io.agora.chatroom.service.UserOperationType
import io.agora.chatroom.service.cache.UIChatroomCacheManager
import io.agora.chatroom.ui.UIChatroomService
import io.agora.chatroom.viewmodel.RequestListViewModel

open class MemberListViewModel(
    private val roomId: String,
    private val service: UIChatroomService,
    private val pageSize: Int = 10
): RequestListViewModel<UserEntity>() {
    private var cursor: String? = null
    private var hasMore: Boolean = true
    fun fetchRoomMembers(
        onSuccess: OnValueSuccess<List<UserEntity>> = {},
        onError: OnError = { _, _ ->}
    ){
        cursor = null
        hasMore = true
        clear()
        fetchMoreRoomMembers(true, onSuccess, onError)
    }

    fun fetchMoreRoomMembers(
        fetchUserInfo: Boolean = false,
        onSuccess: OnValueSuccess<List<UserEntity>> = {},
        onError: OnError = { _, _ ->}
    ){
        loading()
        service.getChatService().fetchMembers(roomId, cursor, pageSize, {cursorResult ->
            hasMore = cursorResult.data.size == pageSize
            cursor = cursorResult.cursor
            val propertyList = cursorResult.data.filter { userId ->
                !UIChatroomCacheManager.getInstance().inCache(userId)
            }
            if (fetchUserInfo && propertyList.isNotEmpty()) {
                fetchUsersInfo(propertyList, { list ->
                    val result = cursorResult.data.map { userId ->
                        UIChatroomCacheManager.getInstance().getUserInfo(userId)
                    }
                    add(result)
                    onSuccess.invoke(result)
                }, { code, error ->
                    val result = cursorResult.data.map { userId ->
                        UIChatroomCacheManager.getInstance().getUserInfo(userId)
                    }
                    add(result)
                    onError.invoke(code, error)
                })
            } else {
                val result = cursorResult.data.map { userId ->
                    UIChatroomCacheManager.getInstance().getUserInfo(userId)
                }
                add(result)
                onSuccess.invoke(result)
            }
        }, {code, error ->
            error(code, error)
        })
    }

    /**
     * Fetches the user information of the chatroom members.
     */
    fun fetchUsersInfo(
        userIdList: List<String>,
        onSuccess: OnValueSuccess<List<UserEntity>> = {},
        onError: OnError = { _, _ ->}
    ) {
        service.getUserService().getUserInfoList(userIdList, { list ->
            val users = list.map {
                it.toUser()
            }
            users.forEach {
                UIChatroomCacheManager.getInstance().saveUserInfo(it.userId, it)
            }
            refresh()
            onSuccess.invoke(users)
        }, { code, error ->
            onError.invoke(code, error)
        })
    }

    /**
     * Returns whether there are more members to fetch.
     */
    fun hasMore(): Boolean {
        return hasMore
    }

    /**
     * Fetches user information based on visible items on the page.
     */
    fun fetchUsersInfo(firstVisibleIndex: Int, lastVisibleIndex: Int) {
        Log.e("apex", "fetchUsersInfo: $firstVisibleIndex, $lastVisibleIndex")
        items.subList(firstVisibleIndex, lastVisibleIndex).filter { user ->
            !UIChatroomCacheManager.getInstance().inCache(user.userId)
        }.let { list ->
            if (list.isNotEmpty()) {
                fetchUsersInfo(list.map { it.userId })
            }
        }
    }

    /**
     * Mutes a user.
     */
    fun muteUser(
        userId: String,
        onSuccess: OnValueSuccess<UserEntity> = {},
        onError: OnError = { _, _ ->}
    ) {
        service.getChatService().operateUser(roomId, userId, UserOperationType.MUTE, { chatroom ->
            onSuccess.invoke(UIChatroomCacheManager.cacheManager.getUserInfo(userId))
        }, { code, error ->
            onError.invoke(code, error)
        })
    }

    /**
     * Unmutes a user.
     */
    fun unmuteUser(
        userId: String,
        onSuccess: OnValueSuccess<UserEntity> = {},
        onError: OnError = { _, _ ->}
    ) {
        service.getChatService().operateUser(roomId, userId, UserOperationType.UNMUTE, { chatroom ->
            onSuccess.invoke(UIChatroomCacheManager.cacheManager.getUserInfo(userId))
        }, { code, error ->
            onError.invoke(code, error)
        })
    }

    /**
     * Kicks a user.
     */
    fun removeUser(
        userId: String,
        onSuccess: OnValueSuccess<UserEntity> = {},
        onError: OnError = { _, _ ->}
    ) {
        service.getChatService().operateUser(roomId, userId, UserOperationType.KICK, { chatroom ->
            onSuccess.invoke(UIChatroomCacheManager.cacheManager.getUserInfo(userId))
        }, { code, error ->
            onError.invoke(code, error)
        })
    }
}
