package io.agora.chatroom.compose.gift

import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import io.agora.chatroom.compose.list.ComposeBaseList
import io.agora.chatroom.compose.utils.rememberStreamImagePainter
import io.agora.chatroom.service.cache.UIChatroomCacheManager
import io.agora.chatroom.theme.ChatroomUIKitTheme
import io.agora.chatroom.uikit.R
import io.agora.chatroom.viewmodel.gift.ComposeGiftListViewModel

@Composable
fun ComposeGiftList(
    viewModel:ComposeGiftListViewModel,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(vertical = 16.dp),
    onItemClick: (Int, ComposeGiftListItemState) -> Unit = { index, message-> },
    itemContent: @Composable (Int, ComposeGiftListItemState) -> Unit = { index, item ->
        DefaultGiftItemContent(
            itemIndex = index,
            giftListItem = item,
            viewModel = viewModel,
            onItemClick = onItemClick,
        )
    },
    emptyContent: @Composable () -> Unit = { },
) {
    when{
        (viewModel.items.isNotEmpty())->{
            ComposeBaseList(
                viewModel = viewModel,
                modifier = modifier,
                itemContent = itemContent,
                contentPadding = contentPadding,
            )
        }
        else -> {
            emptyContent()
        }
    }
}

@Composable
fun DefaultGiftItemContent(
    itemIndex:Int,
    giftListItem:ComposeGiftListItemState,
    viewModel:ComposeGiftListViewModel,
    onItemClick: (Int,ComposeGiftListItemState) -> Unit,
){
    when (giftListItem) {
        //is ComposeDynamicGiftListItemState -> ComposeDynamicGiftItem(giftListItem)
        else -> ComposeGiftItem(viewModel,itemIndex,giftListItem,onItemClick)
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ComposeGiftItem(
    viewModel: ComposeGiftListViewModel,
    itemIndex:Int,
    item:ComposeGiftListItemState,
    onItemClick: (Int,ComposeGiftListItemState) -> Unit,
){
    val gift = (item as ComposeGiftItemState).gift

    Row (
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .padding(start = 16.dp, top = 3.dp, bottom = 3.dp, end = 16.dp)
            .combinedClickable(
                onLongClick = { }
            ) {
                onItemClick(itemIndex, item)
            }
            .wrapContentWidth()
            .height(44.dp)
            .background(
                color = ChatroomUIKitTheme.colors.giftBg,
                shape = ChatroomUIKitTheme.shapes.giftItemBg
            ),
    ){

        val userInfo = UIChatroomCacheManager.cacheManager.getUserInfo(gift.sendUserId)

        val userName = userInfo.nickname?.let {
            it.ifEmpty { userInfo.userId }
        } ?: userInfo.userId

        val avatarUrl = userInfo.avatar?.let {
            it.ifEmpty { "" }
        } ?: ""

        val userPainter = rememberStreamImagePainter(avatarUrl, placeholderPainter = painterResource(id = R.drawable.icon_default_avatar))
        val giftPainter = rememberStreamImagePainter(gift.giftIcon, placeholderPainter = painterResource(id = R.drawable.icon_default_sweet_heart))


        if (avatarUrl.isBlank()){
            Image(
                modifier = Modifier.size(36.dp, 36.dp),
                painter = painterResource(id = R.drawable.icon_default_avatar),
                alignment = Alignment.Center,
                contentDescription = "userAvatar"
            )
        }else{
            Image(
                modifier = Modifier.size(36.dp, 36.dp),
                painter = userPainter,
                alignment = Alignment.Center,
                contentDescription = "userAvatar"
            )
        }

        Column(
            modifier = Modifier
                .padding(start = 6.dp, end = 6.dp)
                .wrapContentWidth()
                .wrapContentHeight()
        ) {

            Text(
                text = userName,
                modifier = Modifier.wrapContentWidth().wrapContentHeight(),
                style = ChatroomUIKitTheme.typography.bodySmall
            )
            Text(
                text = gift.giftName,
                modifier = Modifier.wrapContentWidth().wrapContentHeight(),
                style = ChatroomUIKitTheme.typography.bodySmall
            )

        }

        Image(
            modifier = Modifier.size(40.dp, 40.dp),
            painter = giftPainter,
            alignment = Alignment.Center,
            contentDescription = "gifts"
        )

        Text(
            modifier = Modifier
                .wrapContentWidth()
                .wrapContentHeight()
                .padding(start = 6.dp, end = 6.dp),
            text = "X1",
            style = ChatroomUIKitTheme.typography.titleSmall
        )

    }
}