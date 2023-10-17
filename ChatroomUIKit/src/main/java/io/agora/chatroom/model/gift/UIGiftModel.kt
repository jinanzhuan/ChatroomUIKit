package io.agora.chatroom.model.gift

import android.os.Build
import androidx.annotation.RequiresApi
import com.google.gson.annotations.SerializedName
import io.agora.chatroom.service.GiftEntity
import java.io.Serializable

data class AUIGiftTabInfo constructor(
    @SerializedName("tabId") val tabId: Int,
    @SerializedName("displayName") val tabName: String,
    @SerializedName("gifts") val gifts: List<GiftEntity>
): Serializable

private val selectMap = mutableMapOf<String, Boolean>()
internal var GiftEntity.selected: Boolean
    @RequiresApi(Build.VERSION_CODES.N)
    get() = selectMap.getOrDefault(giftId, false)
    set(value) = selectMap.set(giftId, value)