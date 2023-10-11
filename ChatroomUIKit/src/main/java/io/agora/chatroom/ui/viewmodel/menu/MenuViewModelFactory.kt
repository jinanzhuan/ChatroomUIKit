package io.agora.chatroom.ui.viewmodel.menu

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import io.agora.chatroom.UIChatroomContext
import io.agora.chatroom.ui.model.UIComposeDrawerItem

class MenuViewModelFactory(
    private val isDarkTheme: Boolean? = UIChatroomContext().shared()?.getCurrentTheme(),
    private val title:String = "",
    private val menuList: List<UIComposeDrawerItem> = emptyList(),
    private val isShowTitle:Boolean = true,
    private val isShowCancel:Boolean = true,
    ): ViewModelProvider.Factory {

    private val factories: Map<Class<*>, () -> ViewModel> = mapOf(
        MenuViewModel::class.java to {
            MenuViewModel(
                isDarkTheme = isDarkTheme,
                title = title,
                menuList = menuList,
                isShowTitle = isShowTitle,
                isShowCancel = isShowCancel
            )
        },
    )

    /**
     * Creates the required [ViewModel] for our use case, based on the [factories] we provided.
     */
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val viewModel: ViewModel = factories[modelClass]?.invoke()
            ?: throw IllegalArgumentException(
                "MessageListViewModelFactory can only create instances of " +
                        "the following classes: ${factories.keys.joinToString { it.simpleName }}"
            )

        @Suppress("UNCHECKED_CAST")
        return viewModel as T
    }
}


