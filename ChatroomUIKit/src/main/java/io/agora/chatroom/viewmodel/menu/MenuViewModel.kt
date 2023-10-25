package io.agora.chatroom.viewmodel.menu

import android.content.Context
import io.agora.chatroom.model.UIComposeSheetItem
import io.agora.chatroom.service.UserEntity
import io.agora.chatroom.uikit.R

open class MenuViewModel (
    isDarkTheme: Boolean? = false,
    isShowTitle:Boolean = false,
    isShowCancel:Boolean = true,
    title:String = "",
    menuList: List<UIComposeSheetItem> = emptyList(),
    isExpanded: Boolean = false,
): BottomSheetViewModel<UIComposeSheetItem>(isDarkTheme, isShowTitle, isShowCancel, title, isExpanded = isExpanded, contentList = menuList)

class RoomMemberMenuViewModel (
    isDarkTheme: Boolean? = false,
    isShowTitle:Boolean = false,
    isShowCancel:Boolean = true,
    title:String = "",
    menuList: List<UIComposeSheetItem> = emptyList(),
    isExpanded: Boolean = false,
    var user: UserEntity = UserEntity("")
): MenuViewModel(isDarkTheme, isShowTitle, isShowCancel, title, isExpanded = isExpanded, menuList = menuList) {

    /**
     * Set the menu list according to the tab.
     */
    fun setMenuList(context: Context, tab: String) {
        val memberMenuList = mutableListOf<UIComposeSheetItem>()
        when(tab){
            context.getString(R.string.member_management_participant) -> {
                memberMenuList.add(UIComposeSheetItem(0, context.getString(R.string.menu_item_mute)))
                memberMenuList.add(UIComposeSheetItem(1, context.getString(R.string.menu_item_remove)))
            }
            context.getString(R.string.member_management_mute) -> {
                memberMenuList.add(UIComposeSheetItem(0, context.getString(R.string.menu_item_unmute)))
                memberMenuList.add(UIComposeSheetItem(1, context.getString(R.string.menu_item_remove)))
            }
        }
        clear()
        add(memberMenuList)
    }
}