package io.agora.chatroom.ui

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

var darkTheme: Boolean = false

@ExperimentalMaterialApi
@Composable
fun ComposeBottomDrawer(
    title:String,
    isShowTitle:Boolean = true,
    listData: List<UIComposeDrawerItem> = emptyList(),
    onListItemClick: (Int, UIComposeDrawerItem) -> Unit = { index:Int, item: UIComposeDrawerItem ->},
    drawerContent: @Composable () -> Unit = { DefaultDrawerContent(listData,onListItemClick) },
    screenContent: @Composable () -> Unit,
    onCancelListener:() -> Unit = {}
) {
    darkTheme = isSystemInDarkTheme()

    val drawerState = rememberBottomDrawerState(BottomDrawerValue.Expanded)

    BottomDrawer(
        modifier = Modifier
            .fillMaxWidth()
//            .height(
//                (LocalConfiguration.current.screenHeightDp/2).dp
//            )
        ,
        drawerShape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
        drawerBackgroundColor = if (darkTheme) neutralColor1 else neutralColor98,
        drawerState = drawerState,
        gesturesEnabled = drawerState.isOpen,
        drawerContent = {
            Column(){
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
            .padding(horizontal = 16.dp)
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
                        text = item.title
                    )
                },
                modifier = Modifier.clickable { onListItemClick(index, item) }
            )
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