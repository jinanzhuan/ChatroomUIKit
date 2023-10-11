package io.agora.chatroom.ui.binder

import android.util.Log
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.ComposeView
import androidx.lifecycle.viewmodel.compose.viewModel
import io.agora.chatroom.ui.UIChatroomService
import io.agora.chatroom.ui.compose.ComposeBottomDrawer
import io.agora.chatroom.ui.data.initialLongClickMenu
import io.agora.chatroom.ui.model.UIComposeDrawerItem
import io.agora.chatroom.ui.theme.ChatroomUIKitTheme
import io.agora.chatroom.ui.viewmodel.menu.MenuViewModel
import io.agora.chatroom.ui.viewmodel.menu.MenuViewModelFactory

class UIChatRoomBinder(
    private val chatroom: ComposeView,
    private val service: UIChatroomService,
):UIBinder{
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
        ComposeBottomDrawer(
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