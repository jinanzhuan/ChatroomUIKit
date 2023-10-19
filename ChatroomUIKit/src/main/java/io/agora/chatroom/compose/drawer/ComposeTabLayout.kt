package io.agora.chatroom.compose.drawer

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Icon
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import io.agora.chatroom.compose.utils.rememberStreamImagePainter
import io.agora.chatroom.data.parsingGift
import io.agora.chatroom.model.gift.AUIGiftTabInfo
import io.agora.chatroom.model.gift.selected
import io.agora.chatroom.service.GiftEntity
import io.agora.chatroom.theme.ChatroomUIKitTheme
import io.agora.chatroom.uikit.R
import io.agora.chatroom.viewmodel.gift.ComposeGiftTabViewModel
import io.agora.chatroom.viewmodel.tab.TabWithVpViewModel

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun <T> ComposeTabLayoutWithViewPager(
    modifier: Modifier = Modifier,
    viewModel:TabWithVpViewModel<T>,
    tabContent: @Composable (Int,T) -> Unit = {index,obj->
        DefaultTabContent(viewModel,index,obj)
    },
    vpContent: @Composable () -> Unit = { DefaultVpContent(viewModel) },
){
    val contentList = viewModel.list
    val pageIndex = viewModel.pageIndex
    val tabIndex = viewModel.tabIndex

    val pagerState = rememberPagerState(
        initialPage = pageIndex,
        initialPageOffsetFraction = 0f
    ) {
        contentList.size
    }

    Column(modifier = modifier
        .padding(top = 5.dp)
        .background(ChatroomUIKitTheme.colors.background)
    ) {
        TabRow(
            modifier = modifier.background(ChatroomUIKitTheme.colors.background),
            selectedTabIndex = pageIndex,
            containerColor = ChatroomUIKitTheme.colors.background,
            contentColor = ChatroomUIKitTheme.colors.background,
            divider = { },
            indicator = {}
        ) {
            contentList.forEachIndexed { index, title ->
                Tab(
                    content = {
                        tabContent(index,title)
                    },
                    selected = pageIndex == index,
                    modifier = Modifier,
                    onClick = {
                        viewModel.setTabIndex(index)
                    }
                )
            }
        }
        vpContent()
        LaunchedEffect(pagerState) {
            snapshotFlow { pagerState.currentPage }
                .collect { index ->
                    viewModel.setPageIndex(index)
                    viewModel.setTabIndex(index)
                }
        }
        LaunchedEffect(tabIndex) {
            snapshotFlow { tabIndex }
                .collect { index ->
                    viewModel.setTabIndex(index)
                    pagerState.scrollToPage(index)
                }
        }
    }
}

@Composable
fun <T> DefaultTabContent(viewModel: TabWithVpViewModel<T>,index: Int, tabInfo: T) {
    Log.e("apex","DefaultTabContent: $tabInfo")
    val pageIndex = viewModel.pageIndex
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "tab $pageIndex",
            style = ChatroomUIKitTheme.typography.bodyLarge ,
            color = if (pageIndex == index) {
                ChatroomUIKitTheme.colors.onBackground
            } else {
                ChatroomUIKitTheme.colors.tabUnSelected
            }
        )
        Icon(
            painter = painterResource(R.drawable.icon_tab_bottom_bg),
            contentDescription = "icon",
            modifier = Modifier
                .width(28.dp)
                .height(10.dp),
            tint = if (pageIndex == index) {
                ChatroomUIKitTheme.colors.primary
            } else {
                ChatroomUIKitTheme.colors.background
            }
        )
    }
}


@Composable
fun <T> DefaultVpContent(viewModel: TabWithVpViewModel<T>){
    val pageIndex = viewModel.pageIndex
    LazyColumn {
        item {
            when (pageIndex) {
                0 -> Text(text = "Content for Tab 0")
                else -> { Text(text = "Content for Tab $pageIndex")}
            }
        }
    }
}


@ExperimentalFoundationApi
@Composable
fun GiftTabLayoutWithViewPager(
    viewModel:ComposeGiftTabViewModel,
    modifier: Modifier = Modifier,
    sendGift: (GiftEntity) -> Unit = { },
) {
    ComposeTabLayoutWithViewPager(
        modifier = modifier,
        viewModel = viewModel,
        tabContent = { index,tabInfo ->
            DefaultGiftTabLayout(viewModel = viewModel, index = index, tabInfo = tabInfo)
        },
        vpContent = {
            DefaultGiftVpContent(viewModel = viewModel, sendGift = sendGift)
        }
    )

}

@Composable
fun DefaultGiftVpContent(
    viewModel:ComposeGiftTabViewModel,
    sendGift: (GiftEntity) -> Unit,
){
    val contentList = viewModel.contentList
    val pageIndex = viewModel.pageIndex

    val selectedItemIndex = remember { mutableIntStateOf(-1) }
    Column(modifier = Modifier
        .fillMaxWidth()
        .wrapContentHeight()
        .background(ChatroomUIKitTheme.colors.background)
    ){
        LazyVerticalGrid(
            contentPadding = PaddingValues(10.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            columns = GridCells.Fixed(4)) {
            itemsIndexed(contentList[pageIndex].gifts){ index, emoji ->
                ConstraintLayout(
                    modifier = Modifier
                        .size(80.dp, 98.dp)
                        .background(
                            (if (index == selectedItemIndex.intValue)
                                ChatroomUIKitTheme.colors.giftSelected
                            else ChatroomUIKitTheme.colors.background),
                            ChatroomUIKitTheme.shapes.imageThumbnail
                        )
                        .border(
                            BorderStroke(
                                width = 1.dp,
                                color = if (index == selectedItemIndex.intValue)
                                    ChatroomUIKitTheme.colors.primary
                                else ChatroomUIKitTheme.colors.background
                            ), ChatroomUIKitTheme.shapes.imageThumbnail
                        )
                        .clickable {
                            if (selectedItemIndex.intValue == index){
                                emoji.selected = false
                                selectedItemIndex.intValue = -1
                            }else{
                                emoji.selected = true
                                selectedItemIndex.intValue = index
                            }
                        }
                ) {
                    val (giftIcon,giftName,tagLayout,sendBtn) = createRefs()

                    val painter = rememberStreamImagePainter(
                        data = emoji.giftIcon,
                        placeholderPainter = painterResource(id = R.drawable.icon_default_sweet_heart)
                    )
                    Image(
                        modifier = Modifier
                            .size(48.dp, 48.dp)
                            .constrainAs(giftIcon) {
                                top.linkTo(parent.top)
                                start.linkTo(parent.start)
                                end.linkTo(parent.end)
                            }
                            .padding(top = 6.dp),
                        painter = painter,
                        alignment = Alignment.Center,
                        contentDescription = "giftIcon"
                    )

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentHeight()
                            .constrainAs(tagLayout){
                                top.linkTo(giftIcon.bottom)
                                bottom.linkTo(sendBtn.top)
                                start.linkTo(parent.start)
                                end.linkTo(parent.end)
                        },
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        if (index != selectedItemIndex.intValue){
                            Text(
                                textAlign = TextAlign.Center,
                                maxLines = 1,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .wrapContentHeight(),
                                text = emoji.giftName,
                                style = ChatroomUIKitTheme.typography.titleSmall
                            )
                        }
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .wrapContentHeight(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Image(
                                modifier = Modifier
                                    .size(12.dp, 12.dp),
                                painter = painterResource(id = R.drawable.icon_dollagora),
                                alignment = Alignment.Center,
                                contentDescription = "tagIcon"
                            )
                            Text(
                                maxLines = 1,
                                textAlign = TextAlign.Center,
                                modifier = Modifier
                                    .padding(start = 2.dp)
                                    .wrapContentWidth()
                                    .wrapContentHeight(),
                                text = emoji.giftName,
                                style = ChatroomUIKitTheme.typography.labelExtraSmall
                            )
                        }

                    }
                    if (index == selectedItemIndex.intValue){
                        Text(
                            maxLines = 1,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .background(
                                    ChatroomUIKitTheme.colors.primary,
                                    ChatroomUIKitTheme.shapes.sendGift
                                )
                                .fillMaxWidth()
                                .height(28.dp)
                                .constrainAs(sendBtn) {
                                    bottom.linkTo(parent.bottom)
                                }
                                .clickable {
                                    sendGift(emoji)
                                },
                            style = ChatroomUIKitTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = ChatroomUIKitTheme.colors.giftSend
                            ),
                            text = "Send"
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DefaultGiftTabLayout(viewModel: ComposeGiftTabViewModel, index:Int, tabInfo:AUIGiftTabInfo){
    val pageIndex = viewModel.pageIndex
    Log.e("apex","DefaultGiftVpContent ${viewModel.list}")
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = tabInfo.tabName,
            style = ChatroomUIKitTheme.typography.bodyLarge ,
            color = if (pageIndex == index) {
                ChatroomUIKitTheme.colors.onBackground
            } else {
                ChatroomUIKitTheme.colors.tabUnSelected
            }
        )
        Icon(
            painter = painterResource(R.drawable.icon_tab_bottom_bg),
            contentDescription = "icon",
            modifier = Modifier
                .width(28.dp)
                .height(10.dp),
            tint = if (pageIndex == index) {
                ChatroomUIKitTheme.colors.primary
            } else {
                ChatroomUIKitTheme.colors.background
            }
        )
    }

}


@ExperimentalFoundationApi
@Preview(showBackground = true)
@Composable
fun PreviewTabLayoutWithViewPager() {
    val tabTitles = parsingGift(context = LocalContext.current)
    GiftTabLayoutWithViewPager(viewModel = ComposeGiftTabViewModel(tabTitles))
}