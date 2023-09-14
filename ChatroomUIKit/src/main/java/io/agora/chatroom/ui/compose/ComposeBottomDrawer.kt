package io.agora.chatroom.ui.compose

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
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
import io.agora.chatroom.uikit.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

var darkTheme: Boolean = false
@OptIn(ExperimentalMaterialApi::class)
lateinit var drawerState:BottomDrawerState
lateinit var scope: CoroutineScope
lateinit var drawerType: DrawerType


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
    title:String="",
    @SuppressLint("ModifierParameter") modifier: Modifier = Modifier,
    isShowTitle:Boolean = true,
    isShowCancel:Boolean = true,
    drawerType: DrawerType = DrawerType.DEFAULT,
    listData: List<UIComposeDrawerItem> = emptyList(),
    onListItemClick: (Int, UIComposeDrawerItem) -> Unit = { index:Int, item: UIComposeDrawerItem ->},
    drawerContent: @Composable () -> Unit = { DefaultDrawerContent(listData,onListItemClick) },
    screenContent: @Composable () -> Unit = {},
    onCancelListener:() -> Unit = {}
) {
    darkTheme = isSystemInDarkTheme()
    scope = rememberCoroutineScope()

    drawerState = rememberBottomDrawerState(BottomDrawerValue.Closed)

    BottomDrawer(
        modifier = modifier,
        drawerShape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
        drawerBackgroundColor = if (darkTheme) neutralColor1 else neutralColor98,
        drawerState = drawerState,
        gesturesEnabled = drawerState.isOpen,
        drawerContent = {
            Column(
                modifier = modifier
            ){
                Image(
                    modifier = Modifier
                        .padding(top = 6.dp)
                        .align(Alignment.CenterHorizontally),
                    painter = painterResource(
                        id = if (darkTheme)
                            R.drawable.icon_rectangle_dark else R.drawable.icon_rectangle_light
                    ),
                    contentDescription = "icon"
                )

                if (isShowTitle){
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding( top = 13.dp, bottom = 13.dp),
                        textAlign = TextAlign.Center,
                        text = title,
                        color = if (darkTheme) neutralColor6 else neutralColor5,
                        style = AlphabetBodyMedium
                    )
                }

                drawerContent()

                if (isShowCancel){
                    Divider(
                        modifier = Modifier
                            .height(8.dp)
                            .background(if (darkTheme) neutralColor0 else neutralColor98)
                    )
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding( top = 13.dp, bottom = 13.dp)
                            .clickable {
                                onCancelListener()
                            },
                        textAlign = TextAlign.Center,
                        text = "Cancel",
                        color = if (darkTheme) primaryColor6 else primaryColor5,
                        style = AlphabetBodyLarge
                    )
                }
            }
        },
        content = {
            Box(Modifier.padding(bottom = with(LocalDensity.current) { drawerState.offset.value.dp })) {
                screenContent()
            }
        }
    )
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun DefaultDrawerContent(listData: List<UIComposeDrawerItem>, onListItemClick: (Int, UIComposeDrawerItem) -> Unit){
    val items = remember { mutableStateListOf<UIComposeDrawerItem>() }
    items.addAll(listData)
    LazyColumn(
        modifier = Modifier
            .padding( start = 16.dp, end = 16.dp, bottom = 8.dp)
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        itemsIndexed(listData){ index, item ->
            Divider(
                modifier = Modifier
                    .height(0.5.dp)
                    .background(if (darkTheme) neutralColor2 else neutralColor9)
                    .fillMaxWidth()
                    .padding(top = 10.dp, bottom = 10.dp)
            )
            ListItem(
                text = {
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 10.dp, bottom = 10.dp),
                        textAlign = TextAlign.Center,
                        style = AlphabetBodyLarge,
                        color = if (item.title == "Report"){
                            if (darkTheme) errorColor6 else errorColor5
                        }else{
                            if (darkTheme) primaryColor6 else primaryColor5
                        },

                        text = item.title
                    )
                },
                modifier = Modifier
                    .background(if (darkTheme) neutralColor1 else neutralColor98)
                    .clickable { onListItemClick(index, item) }
            )
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
fun showComposeBottomDrawer() {
    scope.launch {
        if (drawerState.isClosed){
            drawerState.open()
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
fun hindComposeBottomDrawer(){
    scope.launch {
        if (!drawerState.isClosed){
            drawerState.close()
        }
    }
}


@OptIn(ExperimentalMaterialApi::class)
@Preview(showBackground = true)
@Composable
fun JetPackComposeBottomDrawer() {
    val items = mutableListOf<UIComposeDrawerItem>()
    items.add(UIComposeDrawerItem("Item 1"))
    items.add(UIComposeDrawerItem("Item 2"))

    ComposeBottomDrawer(
        "title",
        drawerContent = {
            // 设置Drawer content
            Text("Drawer Content")
        },
        screenContent = {
            // 设置Screen content
            Text("Screen Content")
        },
        listData = items,
        onListItemClick = { index,item ->
            Log.e("apex"," item: $index ${item.title}")
        },
        onCancelListener = {

        }

    )
}