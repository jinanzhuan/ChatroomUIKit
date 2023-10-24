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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
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
import io.agora.chatroom.UIChatroomContext
import io.agora.chatroom.compose.utils.ExpressionUtils
import io.agora.chatroom.model.UserInfoProtocol
import io.agora.chatroom.model.emoji.UIRegexEntity
import io.agora.chatroom.service.GiftEntityProtocol
import io.agora.chatroom.theme.ChatroomUIKitTheme
import io.agora.chatroom.uikit.R
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ComposeMessageItem(
    itemIndex: Int,
    isShowDateSeparator: Boolean = true,
    isShowLabel: Boolean = true,
    isShowAvatar: Boolean = false,
    messageItem: ComposeMessageListItemState,
    itemType: ComposeItemType = ComposeItemType.NORMAL,
    onLongItemClick: (Int, ComposeMessageListItemState) -> Unit,
){
    val message = when (itemType) {
        ComposeItemType.NORMAL -> {
            (messageItem as ComposeMessageItemState).message
        }
        ComposeItemType.ITEM_JOIN -> {
            (messageItem as JoinedMessageState).message
        }
        ComposeItemType.ITEM_GIFT -> {
            (messageItem as GiftMessageState).message
        }
        else -> {
            null
        }
    }

    val gift  = if (itemType == ComposeItemType.ITEM_GIFT && messageItem is GiftMessageState){
        messageItem.gift
    } else {
        null
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
                ChatroomUIKitTheme.colors.barrageL20D10,
                shape = ChatroomUIKitTheme.shapes.small
            ),
    ){
        var userInfo:UserInfoProtocol? = null
        var userId = ""
        var userName = ""
        if (itemType == ComposeItemType.ITEM_GIFT){
            gift?.let {
                userId = it.sendUserId
            }
        }else{
            message?.let {
                userId = it.from
            }
        }

        if (userId.isNotEmpty()){
            userInfo = UIChatroomContext.getInstance().getUserInfo(userId)
            userName = userInfo.nickname?.let {
                it.ifEmpty { userInfo.userId }
            } ?: userInfo.userId
        }

        val dateSeparator = message?.msgTime?.let { convertMillisTo24HourFormat(it) }

        val content =  if(message?.body is TextMessageBody){
            (message.body as TextMessageBody).message
        }else{
            ""
        }

        val inlineMap = mutableMapOf<String,InlineTextContent>()

        val annotatedText = buildAnnotatedString {

            if (isShowDateSeparator){
                withStyle(style = SpanStyle(color = ChatroomUIKitTheme.colors.secondaryL80D70)) {
                    append(dateSeparator)
                }
            }

            if (isShowLabel && itemType != ComposeItemType.ITEM_JOIN){
                withStyle(style = SpanStyle()) {
                    appendInlineContent("Label")
                }
            }

            if (isShowAvatar){
                withStyle(style = SpanStyle()) {
                    appendInlineContent("Avatar")
                }
            }

            // 设置昵称
            withStyle(style = SpanStyle(
                color = ChatroomUIKitTheme.colors.primaryL80D80,
                letterSpacing = 0.01.sp,
                fontSize = 14.sp,
                fontFamily = FontFamily.Default,
            )) {
                append(userName);append("  ")
            }

            //设置内容
            if (itemType == ComposeItemType.NORMAL){
                if (!content.isNullOrEmpty()){
                    if (ExpressionUtils.containsKey(content)){
                        var exchange = content
                        var combination = ""
                        var insertIndex = -1
                        var oldTagLength = 0

                        val insertMap = mutableMapOf<Int,UIRegexEntity>()
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
            }else if (itemType == ComposeItemType.ITEM_JOIN){
                withStyle(style = SpanStyle(
                    color = ChatroomUIKitTheme.colors.secondaryL80D70,
                    letterSpacing = 0.01.sp,
                    fontSize = 14.sp,
                    fontFamily = FontFamily.Default,
                )) {
                    append(stringResource(id = R.string.compose_message_item_joined))
                }
            }else if (itemType == ComposeItemType.ITEM_GIFT){
                gift?.let {
                    append(stringResource(id = R.string.compose_message_gift_sent))
                    append("  ")
                    append(it.giftName)
                }
                withStyle(style = SpanStyle()) {
                    append("  ")
                    appendInlineContent("Gift")
                }
                inlineMap["Gift"] = InlineTextContent(
                    placeholder = Placeholder(20.sp,20.sp, PlaceholderVerticalAlign.Center),
                    children = { DrawGiftImage(gift) }
                )
            }
        }

        if (isShowLabel && itemType != ComposeItemType.ITEM_JOIN){
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

        Text(
            modifier = Modifier.padding(start = 8.dp, top = 4.dp, bottom = 4.dp, end = 8.dp),
            text = annotatedText,
            inlineContent = inlineMap,
            style = ChatroomUIKitTheme.typography.bodyMedium,
            color = ChatroomUIKitTheme.colors.neutralL98D98
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
fun DrawGiftImage(gift:GiftEntityProtocol?){
    var giftUrl = ""
    gift?.let {
        giftUrl =  it.giftIcon
    }
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