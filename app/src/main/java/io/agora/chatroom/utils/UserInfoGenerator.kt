package io.agora.chatroom.utils

import android.content.Context
import io.agora.chatroom.R
import io.agora.chatroom.http.baseAvatarUrl
import kotlin.random.Random

object UserInfoGenerator {
    fun generateUserId(): String {
        return generateRandomChars(16, "abcdefghijklmnopqrstuvwxyz0123456789".toCharArray(), Random)
    }

    private fun generateRandomChars(count: Int, chars: CharArray, random: Random): String {
        val result = StringBuilder()
        for (i in 1..count) {
            result.append(chars[random.nextInt(chars.size)])
        }
        return result.toString()
    }

    fun randomAvatarUrl(context: Context): String {
        return "$baseAvatarUrl${randomArray(context.resources.getStringArray(R.array.avatar_url_end))}"
    }

    fun randomNickname(context: Context): String {
        return randomArray(context.resources.getStringArray(R.array.nickname_array))
    }

    private fun randomArray(source:Array<String>): String {
        source.let {
            return it[Random.nextInt(it.size)]
        }
    }

}