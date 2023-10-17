package io.agora.chatroom.compose.drawer

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import io.agora.chatroom.theme.BodyLarge
import io.agora.chatroom.theme.errorColor50
import io.agora.chatroom.theme.neutralColor10
import io.agora.chatroom.theme.neutralColor70
import io.agora.chatroom.theme.neutralColor80
import io.agora.chatroom.theme.neutralColor98
import io.agora.chatroom.theme.primaryColor50
import io.agora.chatroom.theme.primaryColor60
import io.agora.chatroom.uikit.R

@ExperimentalFoundationApi
@Composable
fun TabLayoutWithViewPager(
    isDarkTheme: Boolean? = false,
    modifier: Modifier = Modifier,
    tabTitles: List<AUIGiftTabInfo>,
) {

    val itemIndex = remember { mutableIntStateOf(0) }
    val pageIndex by itemIndex

    val tabItemIndex = remember { mutableIntStateOf(0) }
    val selectedTabItemIndex by tabItemIndex

    val pagerState = rememberPagerState(
        initialPage = pageIndex,
        initialPageOffsetFraction = 0f
    ) {
        tabTitles.size
    }

    Scaffold(
        modifier = Modifier
            .padding(top = 5.dp)
            .height((LocalConfiguration.current.screenHeightDp / 2).dp),
        content = {

            Column(modifier = Modifier
                .padding(it)
                .background(if (isDarkTheme == true) neutralColor10 else neutralColor98)) {
                TabRow(
                    modifier = modifier,
                    selectedTabIndex = pageIndex,
                    containerColor = if (isDarkTheme == true) neutralColor10 else neutralColor98,
                    contentColor = if (isDarkTheme == true) neutralColor10 else neutralColor98,
                    indicator = {
                        TabRowDefaults.Indicator(
                            modifier = Modifier.padding(),
                            height = 50.dp,
                            color = Color.Transparent
                        )
                    }
                ) {
                    tabTitles.forEachIndexed { index, title ->
                        Tab(
                            content = {
                                Column(
                                    verticalArrangement = Arrangement.Center,
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = title.tabName,
                                        style = BodyLarge,
                                        color = if (isDarkTheme == true) {
                                            if (pageIndex == index) neutralColor98 else neutralColor98
                                        } else {
                                            if (pageIndex == index) neutralColor10 else neutralColor70
                                        }
                                    )
                                    Icon(
                                        painter = painterResource(R.drawable.icon_tab_bottom_bg),
                                        contentDescription = "icon",
                                        modifier = Modifier
                                            .width(28.dp)
                                            .height(10.dp),
                                        tint = if (pageIndex == index) {
                                            if (isDarkTheme == true) primaryColor60 else primaryColor50
                                        } else {
                                            if (isDarkTheme == true) neutralColor10 else neutralColor98
                                        }
                                    )
                                }
                            },
                            selected = pageIndex == index,
                            modifier = Modifier.background(
                                color = if (isDarkTheme == true) neutralColor10 else neutralColor98
                            ),
                            onClick = {
                                tabItemIndex.intValue = index
                            }
                        )
                    }
                }

                DefaultVpContent(
                    isDarkTheme = isDarkTheme,
                    tabTitles,
                    selectedTabItemIndex
                )

                LaunchedEffect(pagerState) {
                    snapshotFlow { pagerState.currentPage }
                        .collect { index ->
                            itemIndex.intValue = index
                            tabItemIndex.intValue = index
                        }
                }

                LaunchedEffect(selectedTabItemIndex) {
                    snapshotFlow { selectedTabItemIndex }
                        .collect { index ->
                            itemIndex.intValue = index
                            pagerState.scrollToPage(index)
                        }
                }

            }
        }
    )
}

@Composable
fun DefaultVpContent(
    isDarkTheme: Boolean?,
    data:List<AUIGiftTabInfo>,
    pageIndex:Int,
){
    Column(modifier = Modifier
        .fillMaxWidth()
        .wrapContentHeight()
        .background(if (isDarkTheme == true) neutralColor10 else neutralColor98)
    ){
        LazyVerticalGrid(
            contentPadding = PaddingValues(10.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            columns = GridCells.Fixed(4)) {
            items(data[pageIndex].gifts) { emoji ->
                ConstraintLayout(modifier = Modifier.size(80.dp,98.dp).background(Color.Red)) {
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


//    LazyColumn() {
//        item {
//            when (pageIndex) {
//                0 -> Text(modifier = Modifier.background(color = errorColor50), text = "Content for Tab 1")
//                1 -> Text(modifier = Modifier.background(color = primaryColor50),text = "Content for Tab 2")
//                2 -> Text(modifier = Modifier.background(color = neutralColor80),text = "Content for Tab 3")
//            }
//        }
//    }
}


@ExperimentalFoundationApi
@Preview(showBackground = true)
@Composable
fun PreviewTabLayoutWithViewPager() {
    val tabTitles = parsingGift(context = LocalContext.current)
    TabLayoutWithViewPager(tabTitles = tabTitles)
}