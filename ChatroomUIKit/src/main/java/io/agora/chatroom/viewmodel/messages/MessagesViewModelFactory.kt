package io.agora.chatroom.viewmodel.messages

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import io.agora.chatroom.UIChatroomContext
import io.agora.chatroom.ui.UIChatroomService
import io.agora.chatroom.commons.ComposeChatListController
import io.agora.chatroom.commons.ComposeMessagesState
import io.agora.chatroom.commons.ComposerChatBarController
import io.agora.chatroom.model.UICapabilities
import io.agora.chatroom.model.UIChatBarMenuItem
import io.agora.chatroom.theme.primaryColor8
import io.agora.chatroom.theme.secondaryColor8
import io.agora.chatroom.uikit.R

class MessagesViewModelFactory(
    private val service: UIChatroomService,
    private val isDarkTheme: Boolean? = UIChatroomContext.shared.getCurrentTheme(),
    private val showDateSeparators: Boolean = true,
    private val showLabel: Boolean = true,
    private val showGift: Boolean = true,
    private val showAvatar: Boolean = true,
    private val dateSeparatorColor: Color = secondaryColor8,
    private val nickNameColor: Color = primaryColor8,
    private val menuItemResource: List<UIChatBarMenuItem> = listOf(
        UIChatBarMenuItem(R.drawable.icon_bottom_bar_more, 1),
        UIChatBarMenuItem(R.drawable.icon_bottom_bar_gift, 0)
    ),
) : ViewModelProvider.Factory{

    /**
     * The list of factories that can build [ViewModel]s that our Messages feature components use.
     */
    private val factories: Map<Class<*>, () -> ViewModel> = mapOf(
        MessageComposerViewModel::class.java to {
            MessageComposerViewModel(
                isDarkTheme = isDarkTheme,
                menuItemResource = menuItemResource,
                composerChatBarController = ComposerChatBarController(
                    roomId = service.getRoomInfo().roomId,
                    chatService = service.getChatService(),
                    capabilities = setOf(UICapabilities.SEND_MESSAGE)
                )
            )
        },
        MessageListViewModel::class.java to {
            MessageListViewModel(
                isDarkTheme = isDarkTheme,
                showDateSeparators = showDateSeparators,
                showLabel = showLabel,
                showGift = showGift,
                showAvatar = showAvatar,
                dateSeparatorColor = dateSeparatorColor,
                nickNameColor = nickNameColor,
                composeChatListController = ComposeChatListController(
                    roomId = service.getRoomInfo().roomId,
                    messageState = ComposeMessagesState(),
                    chatService = service.getChatService()
                )

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