package io.agora.chatroom.viewmodel

import android.app.Application
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import io.agora.chatroom.ChatroomUIKitClient
import io.agora.chatroom.bean.LoginRes
import io.agora.chatroom.http.ChatroomHttpManager
import io.agora.chatroom.http.LoginReq
import io.agora.chatroom.http.toLoginReq
import io.agora.chatroom.model.UserInfoProtocol
import io.agora.chatroom.service.OnError
import io.agora.chatroom.service.OnValueSuccess
import io.agora.chatroom.utils.SPUtils
import io.agora.chatroom.utils.UserInfoGenerator
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SplashViewModel(
    private val context: Application
):ViewModel() {
    private val _loading = mutableStateOf(true)

    fun login(onValueSuccess: OnValueSuccess<LoginRes> = {}, onError: OnError = { _, _ -> }) {
        _loading.value = true
        var loginReq = LoginReq("", "", "")
        SPUtils.getInstance(context).getUerInfo()?.let {
            loginReq = it.toLoginReq()
        }
        Log.e("SplashViewModel", "login loginReq: $loginReq")
        if (loginReq.username.isEmpty()) {
            loginReq = LoginReq(UserInfoGenerator.generateUserId(),
                UserInfoGenerator.randomNickname(context),
                UserInfoGenerator.randomAvatarUrl(context))
            SPUtils.getInstance(context).saveUserInfo(loginReq)
        }
        val call = ChatroomHttpManager.getService().login(loginReq)
        call.enqueue(object : Callback<LoginRes> {
            override fun onResponse(call: Call<LoginRes>, response: Response<LoginRes>) {
                val body = response.body()
                _loading.value = false
                if (body != null) {
                    Log.e("SplashViewModel", "onResponse: $body")
                    SPUtils.getInstance(context).saveToken(body.access_token)
                    ChatroomUIKitClient.getInstance().login(
                        UserInfoProtocol(loginReq.username, loginReq.nickname, body.icon_key),
                        body.access_token,
                        onSuccess = {
                            onValueSuccess.invoke(body)
                    }, onError= { code, msg ->
                            onError.invoke(code, msg)
                        Log.e("SplashViewModel", "onError: $code $msg")
                    })
                } else {
                    onError.invoke(-1, "response body is null")
                }
            }

            override fun onFailure(call: Call<LoginRes>, t: Throwable) {
                Log.e("SplashViewModel", "onFailure: ${t.message}")
                _loading.value = false
                onError.invoke(-1, t.message)
            }
        })
    }

    fun isLoading(): Boolean {
        return _loading.value
    }
}