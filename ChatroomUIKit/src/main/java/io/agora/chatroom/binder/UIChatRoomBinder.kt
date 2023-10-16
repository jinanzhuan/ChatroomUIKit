package io.agora.chatroom.binder

import android.util.Log
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.ComposeView
import androidx.lifecycle.viewmodel.compose.viewModel
import io.agora.chatroom.ui.UIChatroomService
import io.agora.chatroom.compose.drawer.ComposeMenuBottomDrawer
import io.agora.chatroom.data.initialLongClickMenu
import io.agora.chatroom.model.UIComposeDrawerItem
import io.agora.chatroom.theme.ChatroomUIKitTheme
import io.agora.chatroom.viewmodel.menu.MenuViewModel
import io.agora.chatroom.viewmodel.menu.MenuViewModelFactory

class UIChatRoomBinder(
    private val chatroom: ComposeView,
    private val service: UIChatroomService,
): io.agora.chatroom.binder.UIBinder {
   lateinit var menuViewModel:MenuViewModel
    init {

        val menuFactory = buildMenuViewModelFactory(
            menuList = initialLongClickMenu,
            isShowTitle = false,
        )

        chatroom.setContent {

            menuViewModel = viewModel(modelClass = MenuViewModel::class.java, factory = menuFactory)
            ChatroomUIKitTheme{
                ShowMenuComposeDrawer()
            }
        }

    }


    override fun bind() {

    }

    override fun unBind() {

    }

    private fun buildMenuViewModelFactory(
        title:String = "",
        menuList: List<UIComposeDrawerItem> = emptyList(),
        isShowTitle:Boolean = true,
        isShowCancel:Boolean = true,
    ):MenuViewModelFactory{
        return MenuViewModelFactory(
            title = title,
            menuList = menuList,
            isShowTitle = isShowTitle,
            isShowCancel = isShowCancel
        )
    }



    @OptIn(ExperimentalMaterialApi::class)
    @Composable
    fun ShowMenuComposeDrawer(){
        ComposeMenuBottomDrawer(
            viewModel = menuViewModel,
            onListItemClick = { index,item ->
                Log.e("apex"," default item: $index ${item.title}")
            },
            onCancelListener = {
                Log.e("apex"," onClick Cancel ")
            }
        )
    }

}