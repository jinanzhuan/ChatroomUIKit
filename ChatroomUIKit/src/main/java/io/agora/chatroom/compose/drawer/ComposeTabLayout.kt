package io.agora.chatroom.compose.drawer

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import coil.compose.rememberAsyncImagePainter
import io.agora.chatroom.data.parsingGift
import io.agora.chatroom.model.gift.AUIGiftTabInfo
import io.agora.chatroom.theme.ChatroomUIKitTheme
import io.agora.chatroom.uikit.R
import io.agora.chatroom.viewmodel.gift.ComposeGiftSheetViewModel
import io.agora.chatroom.viewmodel.tab.TabWithVpViewModel

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun <T> ComposeTabLayoutWithViewPager(
    modifier: Modifier = Modifier,
    viewModel:TabWithVpViewModel<T>,
    tabContent: @Composable (Int,T) -> Unit = {index,obj->
        DefaultTabContent(viewModel,index,obj)
    },
    vpContent: @Composable () -> Unit = { DefaultVpContent(viewModel)},
){
    val contentList = viewModel.contentList
    val pageIndex = viewModel.pageIndex
    val tabIndex = viewModel.tabIndex

    val pagerState = rememberPagerState(
        initialPage = pageIndex,
        initialPageOffsetFraction = 0f
    ) {
        contentList.size
    }

    Scaffold(
        modifier = Modifier
            .padding(top = 5.dp)
            .height((LocalConfiguration.current.screenHeightDp / 2).dp),
        content = {
            Column(modifier = modifier
                .padding(it)
                .background(ChatroomUIKitTheme.colors.background)
            ) {
                TabRow(
                    modifier = modifier,
                    selectedTabIndex = pageIndex,
                    containerColor = ChatroomUIKitTheme.colors.background,
                    contentColor = ChatroomUIKitTheme.colors.background,
                    indicator = {
                        TabRowDefaults.Indicator(
                            modifier = Modifier.padding().wrapContentHeight(),
                            color = Color.Transparent
                        )
                    }
                ) {
                    contentList.forEachIndexed { index, title ->
                        Tab(
                            content = {
                                tabContent(index,title)
                            },
                            selected = pageIndex == index,
                            modifier = Modifier.background(
                                color = ChatroomUIKitTheme.colors.background
                            ),
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
    )
}

@Composable
fun <T> DefaultTabContent(viewModel: TabWithVpViewModel<T>,index: Int, tabInfo: T) {
    val pageIndex = viewModel.pageIndex
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "tab $pageIndex",
            style = ChatroomUIKitTheme.typography.bodyLarge ,
            color = if (pageIndex == index) {
                ChatroomUIKitTheme.colors.background
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
    LazyColumn() {
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
    viewModel:ComposeGiftSheetViewModel,
    modifier: Modifier = Modifier,
) {
    ComposeTabLayoutWithViewPager(
        modifier = modifier,
        viewModel = viewModel,
        tabContent = { index,T ->
            DefaultGiftTabLayout(viewModel = viewModel, index = index, tabInfo = T)
        },
        vpContent = {
            DefaultGiftVpContent(viewModel = viewModel)
        }
    )

}

@Composable
fun DefaultGiftVpContent(
    viewModel:ComposeGiftSheetViewModel,
){
    val contentList = viewModel.contentList
    val pageIndex = viewModel.pageIndex
    Column(modifier = Modifier
        .fillMaxWidth()
        .wrapContentHeight()
        .background(ChatroomUIKitTheme.colors.background)
    ){
        LazyVerticalGrid(
            contentPadding = PaddingValues(10.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            columns = GridCells.Fixed(4)) {
            items(contentList[pageIndex].gifts) { emoji ->
                ConstraintLayout(
                    modifier = Modifier
                        .size(80.dp,98.dp)
                        .background(Color.Red)
                        .clip(ChatroomUIKitTheme.shapes.imageThumbnail)
                ) {
                    val painter = rememberAsyncImagePainter(
                        model = emoji.giftIcon
                    )
                    Image(
                        modifier = Modifier.size(48.dp,48.dp),
                        painter = painter,
                        alignment = Alignment.Center,
                        contentDescription = "SheetGifts"
                    )
                }
            }
        }
    }
}
@Composable
fun DefaultGiftTabLayout(viewModel: ComposeGiftSheetViewModel,index:Int,tabInfo:AUIGiftTabInfo){
    val pageIndex = viewModel.pageIndex
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = tabInfo.tabName,
            style = ChatroomUIKitTheme.typography.bodyLarge ,
            color = if (pageIndex == index) {
                ChatroomUIKitTheme.colors.background
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
    GiftTabLayoutWithViewPager(viewModel = ComposeGiftSheetViewModel(tabTitles))
}