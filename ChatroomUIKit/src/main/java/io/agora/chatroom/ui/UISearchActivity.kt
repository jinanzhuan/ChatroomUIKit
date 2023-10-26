package io.agora.chatroom.ui

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import io.agora.chatroom.ChatroomUIKitClient
import io.agora.chatroom.compose.drawer.ComposeMenuBottomSheet
import io.agora.chatroom.compose.input.SearchInputFiled
import io.agora.chatroom.compose.member.MembersPage
import io.agora.chatroom.compose.member.MutedListPage
import io.agora.chatroom.theme.ChatroomUIKitTheme
import io.agora.chatroom.uikit.R
import io.agora.chatroom.viewmodel.member.MemberListViewModel
import io.agora.chatroom.viewmodel.member.MemberViewModelFactory
import io.agora.chatroom.viewmodel.member.MutedListViewModel
import io.agora.chatroom.viewmodel.menu.MenuViewModelFactory
import io.agora.chatroom.viewmodel.menu.RoomMemberMenuViewModel

class UISearchActivity : ComponentActivity() {
    private var roomId: String? = ""
    private var title: String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        roomId = intent.getStringExtra(KEY_ROOM_ID)
        title = intent.getStringExtra(KEY_TITLE)
        if (roomId.isNullOrEmpty() || title.isNullOrEmpty()) {
            finish()
            return
        }

        setContent {
            ChatroomUIKitTheme {
                SearchScaffold(this, roomId!!, title!!)
            }
        }
    }

    companion object {
        private const val KEY_ROOM_ID = "roomId"
        private const val KEY_TITLE = "title"

        fun createIntent(
            context: Context,
            roomId: String,
            title: String
        ): Intent {
            return Intent(context, UISearchActivity::class.java).apply {
                putExtra(KEY_ROOM_ID, roomId)
                putExtra(KEY_TITLE, title)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScaffold(context: Activity, roomId: String, title: String) {
    val service = UIChatroomService(ChatroomUIKitClient.getInstance().getContext().getCurrentRoomInfo())
    var isEmpty by rememberSaveable { mutableStateOf(false) }
    val viewModel = viewModel(MemberListViewModel::class.java, factory = MemberViewModelFactory(context = LocalContext.current, roomId = roomId, service = service))
    val mutedViewModel = viewModel(MutedListViewModel::class.java, factory = MemberViewModelFactory(context = LocalContext.current, roomId = roomId, service = service))
    val memberMenuViewModel = viewModel(RoomMemberMenuViewModel::class.java, factory = MenuViewModelFactory())
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = ChatroomUIKitTheme.colors.background,
                    titleContentColor = ChatroomUIKitTheme.colors.onBackground,
                ),
                title = {
                    SearchInputFiled(
                        value = "",
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(end = 16.dp)
                            .height(36.dp),
                        onValueChange = { newText ->
                            if (title == context.getString(R.string.member_management_participant)) {
                                viewModel.searchUsers(newText, onSuccess = { result ->
                                    isEmpty = result.isEmpty() && newText.isNotEmpty()
                                })
                            } else if (title == context.getString(R.string.member_management_mute)) {
                                mutedViewModel.searchUsers(newText, true, onSuccess = { result ->
                                    isEmpty = result.isEmpty() && newText.isNotEmpty()
                                })
                            }
                        },
                        onClearClick = {
                            isEmpty = true
                        }
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { context.finish() }) {
                        Icon(
                            painter = painterResource(id = R.drawable.arrow_left),
                            contentDescription = "Localized description"
                        )
                    }
                },
                scrollBehavior = scrollBehavior,
            )
        }
    ) { innerPadding ->

        if (isEmpty) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Icon(painter = painterResource(id = R.drawable.data_empty), contentDescription = "Empty data")
            }
        } else {
            ComposeMenuBottomSheet(
                viewModel = memberMenuViewModel,
                onListItemClick = { index,item ->
                    Log.e("apex"," default item: $index ${item.title}")
                    when(index){
                        0 -> {
                            if (item.title == context.getString(R.string.menu_item_mute)){
                                mutedViewModel.muteUser(memberMenuViewModel.user.userId,
                                    onSuccess = {
                                        memberMenuViewModel.closeDrawer()
                                        setOKResult(context)
                                    },
                                    onError = {code, error ->
                                        memberMenuViewModel.closeDrawer()
                                    }
                                )
                            }else if (item.title == context.getString(R.string.menu_item_unmute)){
                                mutedViewModel.unmuteUser(memberMenuViewModel.user.userId,
                                    onSuccess = {
                                        memberMenuViewModel.closeDrawer()
                                        setOKResult(context)
                                    },
                                    onError = {code, error ->
                                        memberMenuViewModel.closeDrawer()
                                    }
                                )
                            }
                        }
                        1 -> {
                            if (item.title == context.getString(R.string.menu_item_remove)){
                                mutedViewModel.removeUser(memberMenuViewModel.user.userId,
                                    onSuccess = {
                                        memberMenuViewModel.closeDrawer()
                                        setOKResult(context)
                                    },
                                    onError = {code, error ->
                                        memberMenuViewModel.closeDrawer()
                                    }
                                )
                            }
                        }
                    }
                },
                onDismissRequest = {
                    memberMenuViewModel.closeDrawer()
                }
            )

            if (title == stringResource(id = R.string.member_management_participant)) {
                MembersPage(
                    modifier = Modifier.padding(innerPadding),
                    viewModel = viewModel,
                    tab = title,
                    onExtendClick = { tab, user ->
                        memberMenuViewModel.user = user
                        memberMenuViewModel.setMenuList(context, tab)
                        memberMenuViewModel.openDrawer()
                    },
                )
            } else if (title == stringResource(id = R.string.member_management_mute)) {
                MutedListPage(
                    modifier = Modifier.padding(innerPadding),
                    viewModel = mutedViewModel,
                    tab = title,
                    onExtendClick = { tab, user ->
                        memberMenuViewModel.user = user
                        memberMenuViewModel.setMenuList(context, tab)
                        memberMenuViewModel.openDrawer()
                    },
                )
            }
        }
    }
}

fun setOKResult(context: Activity) {
    context.finish()
    context.setResult(Activity.RESULT_OK)
}
