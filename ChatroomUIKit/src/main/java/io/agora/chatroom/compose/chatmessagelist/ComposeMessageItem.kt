package io.agora.chatroom.compose.chatmessagelist

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.PlaceholderVerticalAlign
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import io.agora.chat.TextMessageBody
import io.agora.chatroom.compose.utils.ExpressionUtils
import io.agora.chatroom.model.UserInfoProtocol
import io.agora.chatroom.model.emoji.RegexEntity
import io.agora.chatroom.service.ChatMessage
import io.agora.chatroom.service.cache.UIChatroomCacheManager
import io.agora.chatroom.service.transfer
import io.agora.chatroom.theme.BodyMedium
import io.agora.chatroom.theme.SmallCorner
import io.agora.chatroom.theme.barrageDarkColor10
import io.agora.chatroom.theme.barrageLightColor20
import io.agora.chatroom.theme.neutralColor98
import io.agora.chatroom.theme.primaryColor80
import io.agora.chatroom.theme.secondaryColor80
import io.agora.chatroom.uikit.R
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ComposeMessageItem(
    itemIndex: Int,
    isDarkTheme: Boolean? = false,
    isShowDateSeparator: Boolean = true,
    isShowLabel: Boolean = true,
    isShowGift: Boolean = false,
    isShowAvatar: Boolean = false,
    dateSeparatorColor: Color = secondaryColor80,
    userNameColor: Color = primaryColor80,
    messageItem: ComposeMessageListItemState,
    itemType: ComposeItemType = ComposeItemType.NORMAL,
    onLongItemClick: (Int, ComposeMessageListItemState) -> Unit,
){
    val message = if (itemType == ComposeItemType.NORMAL) {
        (messageItem as ComposeMessageItemState).message
    } else {
        (messageItem as JoinedMessageState).message
    }

    Row (
        modifier = Modifier
            .padding(start = 16.dp, top = 4.dp, bottom = 4.dp, end = 16.dp)
            .combinedClickable(
                onLongClick = { onLongItemClick(itemIndex, messageItem) }
            ) {
                // onClick
            }
            .wrapContentWidth()
            .wrapContentHeight()
            .background(
                if (isDarkTheme == true) barrageLightColor20 else barrageDarkColor10,
                shape = SmallCorner
            ),
    ){

//        val dateSeparator = convertMillisTo24HourFormat(message.msgTime)

        val dateSeparator = convertMillisTo24HourFormat(System.currentTimeMillis())

        val content =  if(message.body is TextMessageBody){
            (message.body as TextMessageBody).message
        }else{
            ""
        }

        val userInfo = UIChatroomCacheManager.cacheManager.getUserInfo(message.from)
        var userName = userInfo.nickname?.let {
            it.ifEmpty { userInfo.userId }
        } ?: userInfo.userId
        val inlineMap = mutableMapOf<String,InlineTextContent>()
        val annotatedText = buildAnnotatedString {

            if (isShowDateSeparator){
                withStyle(style = SpanStyle(color = dateSeparatorColor)) {
                    append(dateSeparator)
                }
            }

            if (isShowLabel){
                withStyle(style = SpanStyle()) {
                    appendInlineContent("Label")
                }
            }

            if (isShowAvatar){
                withStyle(style = SpanStyle()) {
                    appendInlineContent("Avatar")
                }
            }

            withStyle(style = SpanStyle(
                color = userNameColor,
                letterSpacing = 0.01.sp,
                fontSize = 14.sp,
                fontFamily = FontFamily.Default,
            )) {
//                append(userName)
                append("apex");append("  ")
            }

            if (!content.isNullOrEmpty()){
                if (ExpressionUtils.containsKey(content)){
                    var exchange = content
                    var combination = ""
                    var insertIndex = -1
                    var oldTagLength = 0

                    val insertMap = mutableMapOf<Int,RegexEntity>()
                    val roleList = ExpressionUtils.getRole(content)

                    for (i in 0 until roleList.size){
                        var before = ""
                        var after = ""

                        if (roleList[i].startIndex > 0){
                            before = exchange.substring(0,roleList[i].startIndex - oldTagLength)
                        }
                        after = exchange.substring(roleList[i].endIndex - oldTagLength)

                        if (before.isEmpty()){
                            withStyle(style = SpanStyle()) {
                                appendInlineContent(roleList[i].emojiTag)
                            }
                            ExpressionUtils.addLienMap(roleList[i],inlineMap)
                            combination = before + after
                            exchange = combination
                            oldTagLength += roleList[i].emojiTag.length
                        } else{
                            combination = before + after
                            exchange = combination
                            insertIndex = roleList[i].startIndex - oldTagLength - 1
                            if (insertMap.containsKey(insertIndex)){
                                roleList[i].count +=1
                            }
                            insertMap[insertIndex] = roleList[i]
                            oldTagLength += roleList[i].emojiTag.length
                        }
                    }

                    combination.let { cb->
                        cb.withIndex().forEach { (i, char) ->
                            append(char)
                            insertMap.forEach {
                                if (i == it.key){
                                    for (i in 0 until it.value.count) {
                                        withStyle(style = SpanStyle()) {
                                            appendInlineContent(it.value.emojiTag)
                                        }
                                        ExpressionUtils.addLienMap(it.value,inlineMap)
                                    }
                                }
                            }
                        }
                    }
                }else{
                    append(content)
                }
            }

            if (isShowGift){
                withStyle(style = SpanStyle()) {
                    appendInlineContent("Gift")
                }
            }
        }

        if (isShowLabel){
            inlineMap["Label"] = InlineTextContent(
                placeholder = Placeholder(18.sp,18.sp, PlaceholderVerticalAlign.Center),
                children = {
                    DrawLabelImage(userInfo.transfer())
                }
            )
        }

        if (isShowAvatar){
            inlineMap["Avatar"] = InlineTextContent(
                placeholder = Placeholder(28.sp,28.sp, PlaceholderVerticalAlign.Center),
                children = {
                    DrawAvatarImage(userInfo.transfer())
                }
            )
        }

        if (isShowGift){
            inlineMap["Gift"] = InlineTextContent(
                placeholder = Placeholder(20.sp,20.sp, PlaceholderVerticalAlign.Center),
                children = { DrawGiftImage(message) }
            )
        }

        Text(
            modifier = Modifier.padding(start = 8.dp, top = 4.dp, bottom = 4.dp, end = 8.dp),
            text = annotatedText,
            inlineContent = inlineMap,
            style = BodyMedium,
            color = if (isDarkTheme == true) neutralColor98 else neutralColor98
        )

    }
}

@Composable
fun DrawLabelImage(userInfo:UserInfoProtocol?) {
    var labelUrl = ""
    userInfo?.let {
        labelUrl = it.identify.toString()
    }
    val painter = rememberAsyncImagePainter(
        model = labelUrl
    )
    Image(
        modifier = Modifier
            .size(18.dp, 18.dp)
            .padding(start = 4.dp),
        painter = if (labelUrl.isEmpty()) painterResource(id = R.drawable.icon_default_label) else painter,
        contentDescription = "Label"
    )
}
@Composable
fun DrawAvatarImage(userInfo:UserInfoProtocol?){
    var avatarUrl:String? = ""
    userInfo?.let {
        avatarUrl = it.avatarUrl
    }
    val painter = rememberAsyncImagePainter(
        model = avatarUrl
    )
    Image(
        modifier = Modifier
            .size(28.dp, 28.dp)
            .padding(start = 4.dp, end = 4.dp),
        painter = if (avatarUrl?.isEmpty() == true)painterResource(id = R.drawable.icon_default_avatar) else painter,
        contentDescription = "Avatar"
    )
}
@Composable
fun DrawGiftImage(msg:ChatMessage){
    val giftUrl = if (msg.ext().containsKey("gift")) msg.getStringAttribute("gift") else ""
    val painter = rememberAsyncImagePainter(
        model = giftUrl
    )
    Image(
        modifier = Modifier.size(20.dp,20.dp),
        painter = if (giftUrl.isEmpty())painterResource(id = R.drawable.icon_bottom_bar_gift) else painter,
        contentDescription = "Gift"
    )
}

fun convertMillisTo24HourFormat(millis: Long): String {
    val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
    return sdf.format(Date(millis))
}