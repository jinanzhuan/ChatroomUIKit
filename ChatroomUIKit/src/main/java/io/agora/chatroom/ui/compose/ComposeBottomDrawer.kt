package io.agora.chatroom.ui.compose

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.agora.chatroom.ui.model.UIComposeDrawerItem
import io.agora.chatroom.ui.theme.AlphabetBodyLarge
import io.agora.chatroom.ui.theme.AlphabetBodyMedium
import io.agora.chatroom.ui.theme.errorColor5
import io.agora.chatroom.ui.theme.errorColor6
import io.agora.chatroom.ui.theme.neutralColor0
import io.agora.chatroom.ui.theme.neutralColor1
import io.agora.chatroom.ui.theme.neutralColor2
import io.agora.chatroom.ui.theme.neutralColor5
import io.agora.chatroom.ui.theme.neutralColor6
import io.agora.chatroom.ui.theme.neutralColor9
import io.agora.chatroom.ui.theme.neutralColor98
import io.agora.chatroom.ui.theme.primaryColor5
import io.agora.chatroom.ui.theme.primaryColor6
import io.agora.chatroom.ui.viewmodel.menu.MenuViewModel
import io.agora.chatroom.uikit.R
import kotlinx.coroutines.launch


enum class DrawerType {
    MENU_LIST,//长按菜单
    MUTED_LIST,//禁言列表
    PARTICIPANTS_LIST,//成员列表
    MUTED_MENU,//禁言菜单
    CUSTOM,//自定义类型
    DEFAULT,//默认类型
}

@ExperimentalMaterialApi
@Composable
fun ComposeBottomDrawer(
    viewModel: MenuViewModel,
    modifier: Modifier = Modifier,
    onListItemClick: (Int, UIComposeDrawerItem) -> Unit = { index:Int, item: UIComposeDrawerItem ->},
    drawerContent: @Composable () -> Unit = { DefaultDrawerContent(viewModel,viewModel.list,onListItemClick) },
    screenContent: @Composable () -> Unit = {},
    onCancelListener:() -> Unit = {}
) {
    val scope = rememberCoroutineScope()
    val isDarkTheme = viewModel.getTheme
    val title = viewModel.getTitle
    val isShowTitle = viewModel.getIsShowTitle
    val isShowCancel = viewModel.getIsShowCancel
    val drawerType = viewModel.getDrawerType
    val listData =  viewModel.list

    val isBottomDrawerVisible = viewModel.isBottomDrawerVisible.value
    val bottomDrawerState =  rememberBottomDrawerState(initialValue = BottomDrawerValue.Closed)

    BottomDrawer(
        modifier = modifier,
        drawerShape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
        drawerBackgroundColor = if (isDarkTheme == true) neutralColor1 else neutralColor98,
        drawerState = bottomDrawerState,
        gesturesEnabled = true,
        drawerContent = {
            Column(
                modifier = modifier
            ){
                Image(
                    modifier = Modifier
                        .padding(top = 6.dp)
                        .align(Alignment.CenterHorizontally),
                    painter = painterResource(
                        id = if (isDarkTheme == true)
                            R.drawable.icon_rectangle_dark else R.drawable.icon_rectangle_light
                    ),
                    contentDescription = "icon"
                )

                if (isShowTitle){
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 13.dp, bottom = 13.dp),
                        textAlign = TextAlign.Center,
                        text = title,
                        color = if (isDarkTheme == true) neutralColor6 else neutralColor5,
                        style = AlphabetBodyMedium
                    )
                }

                drawerContent()

                if (isShowCancel){
                    Divider(
                        modifier = Modifier
                            .height(8.dp)
                            .background(if (isDarkTheme == true) neutralColor0 else neutralColor98)
                    )
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 13.dp, bottom = 13.dp)
                            .clickable {
                                onCancelListener()
                                viewModel.closeDrawer()
                            },
                        textAlign = TextAlign.Center,
                        text = "Cancel",
                        color = if (isDarkTheme == true) primaryColor6 else primaryColor5,
                        style = AlphabetBodyLarge
                    )
                }
            }
        },
        content = {
            Box(Modifier.padding(bottom = with(LocalDensity.current) { bottomDrawerState.offset.value.dp })) {
                screenContent()
            }
        }
    )

    LaunchedEffect(bottomDrawerState.isOpen) {
        if (!bottomDrawerState.isOpen) {
            // 抽屉隐藏时执行的代码
            viewModel.closeDrawer()
        }
    }

    LaunchedEffect(isBottomDrawerVisible){
        Log.e("apex","isBottomDrawerVisible:  $isBottomDrawerVisible")
        if (isBottomDrawerVisible){
            scope.launch {
                bottomDrawerState.open()
            }
        }else{
            scope.launch {
                bottomDrawerState.close()
            }
        }
    }


}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun DefaultDrawerContent(viewModel: MenuViewModel,listData: List<UIComposeDrawerItem>, onListItemClick: (Int, UIComposeDrawerItem) -> Unit){
    val items = remember { mutableStateListOf<UIComposeDrawerItem>() }
    items.addAll(listData)
    LazyColumn(
        modifier = Modifier
            .padding(start = 16.dp, end = 16.dp, bottom = 8.dp)
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        itemsIndexed(listData){ index, item ->
            if (index > 0){
                Divider(
                    modifier = Modifier
                        .height(0.5.dp)
                        .background(if (viewModel.getTheme == true) neutralColor2 else neutralColor9)
                        .fillMaxWidth()
                        .padding(top = 10.dp, bottom = 10.dp)
                )
            }
            ListItem(
                text = {
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 10.dp, bottom = 10.dp),
                        textAlign = TextAlign.Center,
                        style = AlphabetBodyLarge,
                        color = if (item.title == "Report"){
                            if (viewModel.getTheme == true) errorColor6 else errorColor5
                        }else{
                            if (viewModel.getTheme == true) primaryColor6 else primaryColor5
                        },

                        text = item.title
                    )
                },
                modifier = Modifier
                    .background(if (viewModel.getTheme == true) neutralColor1 else neutralColor98)
                    .clickable {
                        onListItemClick(index, item)
                        viewModel.closeDrawer()
                    }
            )
        }
    }
}

//@OptIn(ExperimentalMaterialApi::class)
//fun showComposeBottomDrawer() {
//    scope.launch {
//        if (drawerState.isClosed){
//            drawerState.open()
//        }
//    }
//}
//
//@OptIn(ExperimentalMaterialApi::class)
//fun hindComposeBottomDrawer(){
//    scope.launch {
//        if (!drawerState.isClosed){
//            drawerState.close()
//        }
//    }
//}


@OptIn(ExperimentalMaterialApi::class)
@Preview(showBackground = true)
@Composable
fun JetPackComposeBottomDrawer() {
    val items = mutableListOf<UIComposeDrawerItem>()
    items.add(UIComposeDrawerItem("Item 1"))
    items.add(UIComposeDrawerItem("Item 2"))

    ComposeBottomDrawer(
        viewModel = MenuViewModel(),
        drawerContent = {
            // 设置Drawer content
            Text("Drawer Content")
        },
        screenContent = {
            // 设置Screen content
            Text("Screen Content")
        },
        onListItemClick = { index,item ->
            Log.e("apex"," item: $index ${item.title}")
        },
        onCancelListener = {

        }

    )
}