package io.agora.chatroom.viewmodel

import android.util.Log
import io.agora.chatroom.ChatroomUIKitClient
import io.agora.chatroom.http.ChatroomHttpManager
import io.agora.chatroom.service.OnError
import io.agora.chatroom.service.OnSuccess
import io.agora.chatroom.ui.UIChatroomService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ChatroomViewModel(
    private val service: UIChatroomService,
    private val isDarkTheme:Boolean?,
):UIRoomViewModel(service,isDarkTheme) {

    private fun destroyRoom(
        onSuccess: OnSuccess = {},
        onError: OnError = { _, _ ->}
    ){
        val call = ChatroomHttpManager.getService().destroyRoom()
        call.enqueue(object : Callback<String> {
            override fun onResponse(call: Call<String>, response: Response<String>) {
                if (response.isSuccessful) {
                    onSuccess.invoke()
                    Log.e("apex","destroyRoom onSuccess")
                }else{
                    onError.invoke(-1,"Service exception")
                }
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                onError.invoke(-1, t.message)
            }
        })
    }

    /**
     * Finish living.
     */
    fun endLive(
        onSuccess: OnSuccess = {},
        onError: OnError = { _, _ ->}
    ){
        service.getChatService().destroyChatroom(
            roomId = ChatroomUIKitClient.getInstance().getContext().getCurrentRoomInfo().roomId,
            onSuccess = {
                destroyRoom(onSuccess,onError)
            },
            onError = {code, error ->
                onError.invoke(code, error)
            }
        )
    }

    fun leaveChatroom(
        onSuccess: OnSuccess = {},
        onError: OnError = { _, _ ->}
    ){
        if (!ChatroomUIKitClient.getInstance().isCurrentRoomOwner()){
            service.getChatService().leaveChatroom(
                roomId = ChatroomUIKitClient.getInstance().getContext().getCurrentRoomInfo().roomId,
                userId = ChatroomUIKitClient.getInstance().getCurrentUser().userId,
                onSuccess = {
                    onSuccess.invoke()
                },
                onError = {code, error ->
                    onError.invoke(code, error)
                }
            )
        }
    }
}