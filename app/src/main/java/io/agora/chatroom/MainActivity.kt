package io.agora.chatroom

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ListItem
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.agora.chatroom.ui.ComposeBottomDrawer
import io.agora.chatroom.ui.model.UIComposeDrawerItem
import io.agora.chatroom.ui.darkTheme
import io.agora.chatroom.ui.theme.ChatroomUIKitTheme
import io.agora.chatroom.ui.theme.neutralColor2
import io.agora.chatroom.ui.theme.neutralColor9

@ExperimentalMaterialApi
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ChatroomUIKitTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
//                    Greeting("Android")
                    ShowComposeDrawer()
                }
            }
        }
    }
}

@ExperimentalMaterialApi
@Composable
fun ShowComposeDrawer(){
    val list = mutableListOf<UIComposeDrawerItem>()
    list.add(UIComposeDrawerItem("Item 1"))
    list.add(UIComposeDrawerItem("Item 2"))

    val items = remember { mutableStateListOf<UIComposeDrawerItem>() }
    items.addAll(list)

    ComposeBottomDrawer(
        title = "Nickname: Message",
        drawerContent = {
            // 设置Drawer content
            LazyColumn(
                modifier = Modifier
                    .padding(start = 16.dp, end = 16.dp)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                itemsIndexed(items){ index, item ->
                    Column{
                        Divider(
                            modifier = Modifier
                                .height(1.dp)
                                .background(if (darkTheme) neutralColor2 else neutralColor9)
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
                            modifier = Modifier.clickable {
                                Log.e("apex"," item-1: $index ${item.title}")
                            }
                        )
                    }
                }
            }
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

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    ChatroomUIKitTheme {
        Greeting("Android")
    }
}