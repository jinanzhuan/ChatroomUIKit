package io.agora.chatroom

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Button
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ListItem
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.agora.chatroom.ui.UIChatroomActivity
import io.agora.chatroom.compose.drawer.ComposeBottomDrawer
import io.agora.chatroom.compose.drawer.ComposeMenuBottomDrawer
import io.agora.chatroom.data.initialLongClickMenu
import io.agora.chatroom.data.testMenuList1
import io.agora.chatroom.model.UIComposeDrawerItem
import io.agora.chatroom.theme.AlphabetBodyLarge
import io.agora.chatroom.theme.ChatroomUIKitTheme
import io.agora.chatroom.theme.neutralColor2
import io.agora.chatroom.theme.neutralColor9
import io.agora.chatroom.viewmodel.menu.MenuViewModel

class MainActivity : ComponentActivity() {
    private var viewModel1:MenuViewModel = MenuViewModel(menuList = initialLongClickMenu)
    private var viewModel2:MenuViewModel = MenuViewModel(menuList = testMenuList1 )
    var viewModel3:MenuViewModel = MenuViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {

            ChatroomUIKitTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {

                    ShowComposeMenuDrawer(viewModel = viewModel1)
                    ShowDefaultComposeDrawer(viewModel = viewModel2)

                    Greeting(viewModel1,viewModel2,onItemClick = {
                        Log.e("apex","onItemClick $it")
                        when (it) {
                            1 -> {
                                viewModel1.openDrawer()
                            }
                            2 -> {
                                viewModel2.openDrawer()
                            }
                            3 -> {
                                viewModel3.openDrawer()
                                this.startActivity(
                                    UIChatroomActivity.createIntent(
                                        context = this,
                                        roomId = "193314355740675",
                                    ))
                            }
                        }
                    })



                }
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ShowDefaultComposeDrawer(viewModel:MenuViewModel){
    ComposeMenuBottomDrawer(
        viewModel = viewModel,
        onListItemClick = { index,item ->
            Log.e("apex"," default item: $index ${item.title}")
        },
        onCancelListener = {
            Log.e("apex"," onClick Cancel ")
        }
    )
}
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ShowComposeMenuDrawer(viewModel:MenuViewModel){
    ComposeMenuBottomDrawer(
        viewModel = viewModel,
        onListItemClick = { index,item ->
            Log.e("apex"," default item: $index ${item.title}")
        },
        onCancelListener = {
            Log.e("apex"," onClick Cancel ")
        }
    )
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ShowCustomComposeDrawer(viewModel: MenuViewModel){
    val list = mutableListOf<UIComposeDrawerItem>()
    list.add(UIComposeDrawerItem("Item 1"))
    list.add(UIComposeDrawerItem("Item 2"))
    list.add(UIComposeDrawerItem("Item 3"))
    list.add(UIComposeDrawerItem("Item 4"))

    ComposeBottomDrawer(
        viewModel = viewModel,
        modifier = Modifier.fillMaxWidth(),
        drawerContent = {
            LazyColumn(
                modifier = Modifier
                    .padding(start = 16.dp, end = 16.dp)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                itemsIndexed(list){ index, item ->
                    Column{
                        Divider(
                            modifier = Modifier
                                .height(0.5.dp)
                                .background(if (viewModel.getTheme == true) neutralColor2 else neutralColor9)
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
                                    text = item.title
                                )
                            },
                            modifier = Modifier.clickable {
                                Log.e("apex"," custom item: $index ${item.title}")
                            }
                        )
                    }
                }
            }
//            TabLayoutWithViewPager(tabTitles = listOf("Participants", "Muted"))
//            TabLayoutViewPager(
//                onVpSelected = { i->
//                    Log.e("apex","TabLayoutViewPager change: $i")
//                }
//            )
        },
        onCancelListener = {
            Log.e("apex"," onClick Cancel ")
        }
    )

}
@Composable
fun Greeting(viewModel1: MenuViewModel,viewModel2: MenuViewModel,onItemClick:(index:Int) -> Unit = {}) {
    Column(Modifier.fillMaxWidth()) {
        Button(onClick = {
            onItemClick(1)

//            ShowComposeMenuDrawer(viewModel = viewModel1)
            viewModel1.openDrawer()

        }) {
            Text("DefaultDrawer")
        }
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = {
            onItemClick(2)
            viewModel2.openDrawer()

        }) {
            Text("CustomerDrawer")
        }

        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = {
            onItemClick(3)
        }) {
            Text("UIChatroomActivity")
        }
    }

}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    ChatroomUIKitTheme {
//        Greeting("Android")
    }
}