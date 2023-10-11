package io.agora.chatroom.ui.compose

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.agora.chatroom.ui.theme.AlphabetBodyLarge
import io.agora.chatroom.ui.theme.errorColor5
import io.agora.chatroom.ui.theme.neutralColor1
import io.agora.chatroom.ui.theme.neutralColor7
import io.agora.chatroom.ui.theme.neutralColor8
import io.agora.chatroom.ui.theme.neutralColor98
import io.agora.chatroom.ui.theme.primaryColor5
import io.agora.chatroom.ui.theme.primaryColor6
import io.agora.chatroom.uikit.R

var isUserDefaultContent:Boolean = false

@ExperimentalFoundationApi
@Composable
fun TabLayoutWithViewPager(
    isDarkTheme:Boolean? = false,
    tabTitles: List<String>,
    vpContent: @Composable (pageIndex:Int) -> Unit = { isUserDefaultContent = true},
) {

    val itemIndex = remember { mutableStateOf(0) }
    val selectedItemIndex by itemIndex

    val tabItemIndex = remember { mutableStateOf(0) }
    val selectedTabItemIndex by tabItemIndex

    val pagerState = rememberPagerState(initialPage = selectedItemIndex)

    Scaffold(
        modifier = Modifier
            .padding(top = 5.dp)
            .height((LocalConfiguration.current.screenHeightDp/2).dp),
        content = {
            it.calculateTopPadding()
            it.calculateBottomPadding()
            Column (modifier = Modifier.background(if (isDarkTheme == true) neutralColor1 else neutralColor98)){
                TabRow(
                    modifier = Modifier.height(50.dp),
                    selectedTabIndex = selectedItemIndex,
                    backgroundColor = if (isDarkTheme == true) neutralColor1 else neutralColor98,
                    contentColor = if (isDarkTheme == true) neutralColor1 else neutralColor98,
                ) {
                    tabTitles.forEachIndexed { index, title ->
                        Tab(
                            content= {
                                Column(
                                    verticalArrangement = Arrangement.Center,
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ){
                                    Text(
                                        text = title,
                                        style = AlphabetBodyLarge,
                                        color = if (isDarkTheme == true){
                                            if (selectedItemIndex == index) neutralColor98 else neutralColor98
                                        }else{
                                            if (selectedItemIndex == index) neutralColor1 else neutralColor7
                                        }
                                    )
                                    Icon(
                                        painter = painterResource(R.drawable.icon_tab_bottom_bg),
                                        contentDescription = "icon",
                                        modifier = Modifier
                                            .width(28.dp).height(10.dp),
                                        tint = if (selectedItemIndex == index ){
                                            if (isDarkTheme == true) primaryColor6 else primaryColor5
                                        } else{
                                            if (isDarkTheme == true) neutralColor1 else neutralColor98
                                        }
                                    )
                                }
                            },
                            selected = selectedItemIndex == index,
                            modifier = Modifier.background(
                                color = if (isDarkTheme == true) neutralColor1 else neutralColor98
                            ),
                            onClick = {
                                tabItemIndex.value = index
                            }
                        )
                    }
                }

                HorizontalPager(
                    pageCount = tabTitles.size,
                    modifier = Modifier.height(200.dp),
                    state = pagerState
                ) {
                    if (isUserDefaultContent){
                        DefaultVpContent(selectedItemIndex)
                    }else{
                        vpContent(selectedItemIndex)
                    }
                }

                LaunchedEffect(pagerState) {
                    snapshotFlow { pagerState.currentPage }
                        .collect { index ->
                            itemIndex.value = index
                            tabItemIndex.value = index
                        }
                }

                LaunchedEffect(selectedTabItemIndex) {
                    snapshotFlow { selectedTabItemIndex }
                        .collect { index ->
                            itemIndex.value = index
                            pagerState.scrollToPage(index)
                        }
                }

            }
        }
    )
}

@Composable
fun DefaultVpContent(pageIndex:Int){
    LazyColumn() {
        item {
            when (pageIndex) {
                0 -> Text(modifier = Modifier.background(color = errorColor5), text = "Content for Tab 1")
                1 -> Text(modifier = Modifier.background(color = primaryColor5),text = "Content for Tab 2")
                2 -> Text(modifier = Modifier.background(color = neutralColor8),text = "Content for Tab 3")
            }
        }
    }
}


@ExperimentalFoundationApi
@Preview(showBackground = true)
@Composable
fun PreviewTabLayoutWithViewPager() {
    val tabTitles = listOf("Tab 1", "Tab 2", "Tab 3")
    TabLayoutWithViewPager(tabTitles = tabTitles)
}