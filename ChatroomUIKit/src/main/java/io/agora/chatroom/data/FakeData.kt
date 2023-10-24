package io.agora.chatroom.data

import android.content.Context
import com.google.gson.Gson
import io.agora.chatroom.model.UIComposeSheetItem
import io.agora.chatroom.model.gift.AUIGiftTabInfo
import java.nio.charset.Charset

val initialLongClickMenu = listOf(
    UIComposeSheetItem(title = "Translate"),
    UIComposeSheetItem(title = "Delete"),
    UIComposeSheetItem(title = "Mute"),
    UIComposeSheetItem(title = "Report", isError = true)
)

val testMenuList = listOf(
    UIComposeSheetItem(title = "Item 1"),
    UIComposeSheetItem(title = "Item 2"),
    UIComposeSheetItem(title = "Item 3")
)

val testMenuList1 = listOf(
    UIComposeSheetItem(title = "Item 1"),
    UIComposeSheetItem(title = "Item 2"),
    UIComposeSheetItem(title = "Item 3"),
    UIComposeSheetItem(title = "Item 4")

)

fun parsingGift(context: Context): List<AUIGiftTabInfo> {
    val assetManager = context.assets
    val jsonFile = "giftEntity.json" // JSON文件名
    // 读取JSON文件到字符串
    val inputStream = assetManager.open(jsonFile)
    val size = inputStream.available()
    val buffer = ByteArray(size)
    inputStream.read(buffer)
    inputStream.close()
    val jsonString = String(buffer, Charset.forName("UTF-8"))

    // 使用Gson解析JSON字符串
    val gson = Gson()
    return gson.fromJson(jsonString, Array<AUIGiftTabInfo>::class.java).toList()
}