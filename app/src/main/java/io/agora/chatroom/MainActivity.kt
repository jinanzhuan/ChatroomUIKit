package io.agora.chatroom

import android.content.Intent
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
import androidx.compose.foundation.layout.statusBarsPadding
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import io.agora.chatroom.compose.drawer.ComposeBottomSheet
import io.agora.chatroom.compose.drawer.ComposeMenuBottomSheet
import io.agora.chatroom.compose.list.LazyColumnList
import io.agora.chatroom.compose.marquee.MainScreen
import io.agora.chatroom.compose.report.ComposeMessageReport
import io.agora.chatroom.data.initialLongClickMenu
import io.agora.chatroom.data.testMarqueeList
import io.agora.chatroom.data.testMenuList1
import io.agora.chatroom.model.UIChatroomInfo
import io.agora.chatroom.model.UIComposeSheetItem
import io.agora.chatroom.theme.BodyLarge
import io.agora.chatroom.theme.ChatroomUIKitTheme
import io.agora.chatroom.theme.neutralColor20
import io.agora.chatroom.theme.neutralColor90
import io.agora.chatroom.ui.UIChatroomService
import io.agora.chatroom.ui.UISearchActivity
import io.agora.chatroom.viewmodel.RequestListViewModel
import io.agora.chatroom.viewmodel.marquee.MarqueeTextViewModel
import io.agora.chatroom.viewmodel.menu.MenuViewModel
import io.agora.chatroom.viewmodel.report.ComposeReportViewModel
import io.agora.chatroom.viewmodel.tab.TabWithVpViewModel

class MainActivity : ComponentActivity() {
    private var viewModel1:MenuViewModel = MenuViewModel(menuList = initialLongClickMenu, isExpanded = true)
    private var viewModel2:MenuViewModel = MenuViewModel(menuList = testMenuList1, isExpanded = true )
    private val viewModel4 by lazy { TestItemViewModel() }
    var viewModel3:MenuViewModel = MenuViewModel()
    private val marqueeViewModel = MarqueeTextViewModel(content = testMarqueeList)
    private var viewModel5: ComposeReportViewModel = ComposeReportViewModel(
        reportTag = mutableListOf(
            "Unwelcome commercial content or spam",
            "Pornographic or explicit content",
            "Child abuse",
            "Unwelcome commercial content or spam",
            "Pornographic or explicit content",
            "Child abuse",
            "Unwelcome commercial content or spam",
            "Pornographic or explicit content",
            "Child abuse",
        ),
        service = UIChatroomService(UIChatroomInfo("", null))
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {

            ChatroomUIKitTheme {
                // A surface container using the 'background' color from the theme
                Surface(modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colors.background)) {
                    ShowComposeMenuDrawer(viewModel = viewModel1,viewModel5 = viewModel5)
                    ShowDefaultComposeDrawer(viewModel = viewModel2)
                    testLazyColumn(viewModel = viewModel4)
                    ComposeMessageReport(
                        modifier = Modifier.height((LocalConfiguration.current.screenHeightDp/2).dp),
                        containerColor = ChatroomUIKitTheme.colors.background,
                        viewModel = viewModel5,
                        onConfirmClick = {
                            Log.e("apex","com $it")
                        },
                        onCancelClick = {
                            Log.e("apex"," onCancelClick ")
                            viewModel5.closeDrawer()
                        },
                        onDismissRequest = {
                            viewModel5.closeDrawer()
                        }
                    )

                    MainScreen(
                        modifier = Modifier
                            .padding(top = 300.dp, start = 12.dp, end = 12.dp)
                            .statusBarsPadding(),
                        marqueeViewModel
                    )

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
                                marqueeViewModel.addMarqueeText("Notification 3 Notification 3 Notification 3 Notification 3")
//                                viewModel3.openDrawer()
//                                startActivity(
//                                    UIChatroomActivity.createIntent(
//                                        context = this@MainActivity,
//                                        ownerId = "apex1",
//                                        roomId = "229551090237444",
//                                    ))
                            }
                            4 -> {
//                                val list = mutableListOf<String>()
//                                for (index in 1..50) {
//                                    list.add("Item $index")
//                                }
//                                viewModel4.add(list)
                                startActivity(Intent(this@MainActivity, UISearchActivity::class.java))
                            }
                        }
                    })

                }
            }
        }
    }
}

@Composable
fun ShowDefaultComposeDrawer(viewModel:MenuViewModel){
    ComposeMenuBottomSheet(
        viewModel = viewModel,
        onListItemClick = { index,item ->
            Log.e("apex"," default item: $index ${item.title}")
        },
        onDismissRequest = {
            viewModel.closeDrawer()
            Log.e("apex"," onClick Cancel ")
        }
    )
}
@Composable
fun ShowComposeMenuDrawer(viewModel:MenuViewModel,viewModel5:ComposeReportViewModel){
    ComposeMenuBottomSheet(
        viewModel = viewModel,
        onListItemClick = { index,item ->
            Log.e("apex"," default item: $index ${item.title}")
            if (index == 3){
                viewModel5.openDrawer()
            }
        },
        onDismissRequest = {
            viewModel.closeDrawer()
            Log.e("apex"," onClick Cancel ")
        }
    )
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ShowCustomComposeDrawer(viewModel: MenuViewModel){
    val list = mutableListOf<UIComposeSheetItem>()
    list.add(UIComposeSheetItem(id = 0, title = "Item 1"))
    list.add(UIComposeSheetItem(id = 1, title = "Item 2"))
    list.add(UIComposeSheetItem(id = 2, title = "Item 3"))
    list.add(UIComposeSheetItem(id = 3, title = "Item 4"))

    ComposeBottomSheet(
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
                                .background(if (viewModel.getTheme == true) neutralColor20 else neutralColor90)
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
                                    style = BodyLarge,
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
        onDismissRequest = {
            viewModel.closeDrawer()
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
//            viewModel2.openDrawer()

        }) {
            Text("CustomerDrawer")
        }

        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = {
            onItemClick(3)
        }) {
            Text("UIChatroomActivity")
        }
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = {
            onItemClick(4)
        }) {
            Text("Add test data")
        }

        Spacer(modifier = Modifier.height(8.dp))
    }

}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    ChatroomUIKitTheme {
//        Greeting("Android")
    }
}

@Composable
fun testLazyColumn(viewModel: TestItemViewModel) {
    ChatroomUIKitTheme {
        LazyColumnList(viewModel) { index, item ->
            Text(text = item, modifier = Modifier.padding(16.dp))
        }
    }
}

class TestItemViewModel: RequestListViewModel<String>()

class ViewPagerViewModel(list: List<String> = emptyList()): TabWithVpViewModel<String>(contentList = list)

