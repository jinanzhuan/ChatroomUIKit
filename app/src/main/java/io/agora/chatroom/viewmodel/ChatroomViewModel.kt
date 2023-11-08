package io.agora.chatroom.viewmodel

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


    fun destroyRoom(
        onSuccess: OnSuccess = {},
        onError: OnError = { _, _ ->}
    ){
        val call = ChatroomHttpManager.getService().destroyRoom()
        call.enqueue(object : Callback<String> {
            override fun onResponse(call: Call<String>, response: Response<String>) {
                if (response.isSuccessful) {
                    onSuccess.invoke()
                }else{
                    onError.invoke(-1,"Service exception")
                }
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                onError.invoke(-1, t.message)
            }
        })
    }
}