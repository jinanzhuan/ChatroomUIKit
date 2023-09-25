package io.agora.chatroom.ui.compose

import android.text.format.DateUtils
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
import androidx.compose.material.Text
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
import io.agora.chatroom.model.UserInfoProtocol
import io.agora.chatroom.service.ChatMessage
import io.agora.chatroom.service.cache.UIChatroomCacheManager
import io.agora.chatroom.ui.theme.AlphabetBodyMedium
import io.agora.chatroom.ui.theme.SmallCorner
import io.agora.chatroom.ui.theme.barrageDarkColor1
import io.agora.chatroom.ui.theme.barrageLightColor2
import io.agora.chatroom.ui.theme.neutralColor98
import io.agora.chatroom.ui.theme.primaryColor8
import io.agora.chatroom.ui.theme.secondaryColor8
import io.agora.chatroom.uikit.R

@OptIn(ExperimentalFoundationApi::class)
@Composable
public fun ComposeMessageItem(
    isDarkTheme: Boolean = false,
    isShowDateSeparator: Boolean = true,
    isShowLabel: Boolean = true,
    isShowGift: Boolean = false,
    isShowAvatar: Boolean = false,
    dateSeparatorColor: Color = secondaryColor8,
    userNameColor: Color = primaryColor8,
    messageItem: ComposeMessageListItemState,
    itemType:ComposeItemType = ComposeItemType.NORMAL,
    onLongItemClick: (ChatMessage) -> Unit,
){
    val message = if (itemType == ComposeItemType.NORMAL) {
        (messageItem as ComposeMessageItemState).message
    } else {
        (messageItem as JoinedMessageState).message
    }

    Row (
        modifier = Modifier
            .padding(start = 16.dp, top = 4.dp, bottom = 4.dp, end = 16.dp)
            .combinedClickable {
                onLongItemClick(message)
            }
            .wrapContentWidth()
            .wrapContentHeight()
            .background(
                if (isDarkTheme) barrageLightColor2 else barrageDarkColor1,
                shape = SmallCorner
            ),

    ){

        val dateSeparator = defaultMessageDateSeparatorContent(message)
        val content =  if(message.body is TextMessageBody){
            (message.body as TextMessageBody).message
        }else{
            ""
        }

        var userName = ""
        val userInfo = UIChatroomCacheManager.cacheManager.getUserInfo(message.from)
        userInfo?.let {
            userName = it.nickname.ifEmpty { it.userId }
        }

        val annotatedText = buildAnnotatedString {

            if (isShowDateSeparator){
                withStyle(style = SpanStyle(color = dateSeparatorColor)) {
//                    append(dateSeparator)
                    append("23:00")
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
                append("apex")
            }

            append(content)

//            if (isShowGift){
//                withStyle(style = SpanStyle()) {
//                    appendInlineContent("Gift")
//                }
//            }

        }

        val inlineMap = mutableMapOf<String,InlineTextContent>()

        if (isShowLabel){
            inlineMap["Label"] = InlineTextContent(
                placeholder = Placeholder(18.sp,18.sp, PlaceholderVerticalAlign.Center),
                children = {
                    DrawLabelImage(UserInfoProtocol())
//                    userInfo?.let { it1 -> DrawLabelImage(it1) }
                }
            )
        }

        if (isShowAvatar){
            inlineMap["Avatar"] = InlineTextContent(
                placeholder = Placeholder(18.sp,18.sp, PlaceholderVerticalAlign.Center),
                children = {
                    DrawAvatarImage(UserInfoProtocol())
//                    userInfo?.let { it1 -> DrawAvatarImage(it1) }
                }
            )
        }

//        if (isShowGift){
//            inlineMap["Gift"] = InlineTextContent(
//                placeholder = Placeholder(18.sp,18.sp, PlaceholderVerticalAlign.Center),
//                children = { DrawGiftImage(message) }
//            )
//        }

        Text(
            modifier = Modifier.padding(start = 8.dp, top = 4.dp, bottom = 4.dp, end = 8.dp),
            text = annotatedText,
            inlineContent = inlineMap,
            style = AlphabetBodyMedium,
            color = if (isDarkTheme) neutralColor98 else neutralColor98
        )

    }
}


/**
 * Represents a date separator item that shows whenever messages are too far apart in time.
 *
 * @param messageItem The data used to show the separator text.
 */
fun defaultMessageDateSeparatorContent(
    message: ChatMessage,
):String {
    return DateUtils.getRelativeTimeSpanString(
        message.msgTime,
        System.currentTimeMillis(),
        DateUtils.DAY_IN_MILLIS,
        DateUtils.FORMAT_ABBREV_RELATIVE
    ).toString()
}


@Composable
fun DrawLabelImage(userInfo:UserInfoProtocol) {
//    val labelUrl = userInfo.identify
//    val labelUrl = ""
//    val painter = rememberAsyncImagePainter(
//        model = labelUrl
//    )
//    Image(
//        modifier = Modifier.size(18.dp,18.dp),
//        painter = if (labelUrl.isNullOrEmpty()) painterResource(id = R.drawable.icon_default_label) else painter,
//        contentDescription = "Label"
//    )

    Image(
        modifier = Modifier.size(18.dp,18.dp).padding(start = 4.dp),
        painter = painterResource(id = R.drawable.icon_default_label),
        contentDescription = "Label"
    )
}
@Composable
fun DrawAvatarImage(userInfo:UserInfoProtocol){
//    val avatarUrl = userInfo.avatarUrl
    val avatarUrl = ""
    val painter = rememberAsyncImagePainter(
        model = avatarUrl
    )
    Image(
        modifier = Modifier.size(18.dp,18.dp).padding(start = 4.dp, end = 4.dp),
        painter = if (avatarUrl.isNullOrEmpty())painterResource(id = R.drawable.icon_default_avatar) else painter,
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
        modifier = Modifier.size(18.dp,18.dp),
        painter = if (giftUrl.isEmpty())painterResource(id = R.drawable.icon_bottom_bar_gift) else painter,
        contentDescription = "Gift"
    )
}