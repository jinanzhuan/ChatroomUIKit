package io.agora.chatroom.viewmodel.menu

import io.agora.chatroom.model.UIComposeDrawerItem

class MenuViewModel (
    isDarkTheme: Boolean? = false,
    isShowTitle:Boolean = true,
    isShowCancel:Boolean = true,
    title:String = "",
    menuList: List<UIComposeDrawerItem> = emptyList(),
): BottomDrawerViewModel<UIComposeDrawerItem>(isDarkTheme, isShowTitle, isShowCancel, title, contentList = menuList)