package io.agora.chatroom.viewmodel.menu

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.ViewModel
import io.agora.chatroom.compose.drawer.DrawerType
import io.agora.chatroom.model.UIComposeDrawerItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class MenuViewModel constructor(
    private val isDarkTheme: Boolean? = false,
    private val isShowTitle:Boolean = true,
    private val isShowCancel:Boolean = true,
    private val title:String = "",
    private val drawerType: DrawerType = DrawerType.DEFAULT,
    private val menuList: List<UIComposeDrawerItem> = emptyList(),
): ViewModel() {

    private val _menuList: MutableList<UIComposeDrawerItem> = menuList.toMutableStateList()
    val list: List<UIComposeDrawerItem> = _menuList

    private val _show : MutableState<Boolean> = mutableStateOf(false)
    var isBottomDrawerVisible = _show

    private val _drawerShouldBeOpened = MutableStateFlow(false)
    val drawerShouldBeOpened = _drawerShouldBeOpened.asStateFlow()

    private val _drawerType : MutableState<DrawerType> = mutableStateOf(DrawerType.DEFAULT)
    var currentDrawerType = _drawerType

    val getTheme: Boolean?
        get() = isDarkTheme

    val getTitle: String
        get() = title

    val getIsShowTitle: Boolean
        get() = isShowTitle

    val getIsShowCancel: Boolean
        get() = isShowCancel

    fun openDrawer(){
        _show.value = true
    }

    fun closeDrawer(){
        _show.value = false
    }

    fun setCurrentDrawer(drawerType: DrawerType){
        _drawerType.value = drawerType
    }


    fun openBottomDrawer() {
        _drawerShouldBeOpened.value = true
    }

    fun resetOpenDrawerAction() {
        _drawerShouldBeOpened.value = false
    }
}