package io.agora.chatroom.viewmodel

import io.agora.chatroom.bean.RequestListResp
import io.agora.chatroom.bean.RoomDetailBean
import io.agora.chatroom.http.ChatroomHttpManager
import io.agora.chatroom.service.OnError
import io.agora.chatroom.service.OnValueSuccess
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ChatroomListViewModel :RequestListViewModel<RoomDetailBean>() {

    fun fetchRoomList(
        onSuccess: OnValueSuccess<List<RoomDetailBean>> = {},
        onError: OnError = { _, _ ->}
    ){
        clear()
        loading()
        val call = ChatroomHttpManager.getService().fetchRoomList()
        call.enqueue(object : Callback<RequestListResp<RoomDetailBean>> {
            override fun onResponse(
                call: Call<RequestListResp<RoomDetailBean>>,
                response: Response<RequestListResp<RoomDetailBean>>
            ) {
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null) {
                        add(body.entities)
                        onSuccess.invoke(body.entities)
                    } else {
                        error(-1, "response body is null")
                        onError.invoke(-1, "response body is null")
                    }
                } else {
                    error(response.code(), response.message())
                    onError.invoke(response.code(), response.message())
                }
            }

            override fun onFailure(call: Call<RequestListResp<RoomDetailBean>>, t: Throwable) {
                error(-1, t.message)
                onError.invoke(-1, t.message)
            }
        })
    }

}