package io.agora.chatroom.http

import io.agora.chatroom.bean.BroadcastReq
import io.agora.chatroom.bean.LoginRes
import io.agora.chatroom.bean.RequestListResp
import io.agora.chatroom.bean.RoomDetailBean
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.GET
import retrofit2.http.POST

interface ChatroomInterface {
    @POST("liverooms/user/login")
    fun login(@Body req: Any): Call<LoginRes>

    @POST("liverooms")
    fun createRoom(@Body req: Any): Call<RoomDetailBean>

    @GET("liverooms")
    fun fetchRoomList(): Call<RequestListResp<RoomDetailBean>>

    @POST("liverooms/broadcast")
    fun broadcast(@Body req: Any): Call<BroadcastReq>

    @POST("room/leave")
    fun destroyRoom(): Call<String>
}