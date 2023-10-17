package io.agora.chatroom.viewmodel.menu

import io.agora.chatroom.model.UIComposeSheetItem

class MenuViewModel (
    isDarkTheme: Boolean? = false,
    isShowTitle:Boolean = false,
    isShowCancel:Boolean = true,
    title:String = "",
    menuList: List<UIComposeSheetItem> = emptyList(),
    isExpanded: Boolean = false,
): BottomSheetViewModel<UIComposeSheetItem>(isDarkTheme, isShowTitle, isShowCancel, title, isExpanded = isExpanded, contentList = menuList)