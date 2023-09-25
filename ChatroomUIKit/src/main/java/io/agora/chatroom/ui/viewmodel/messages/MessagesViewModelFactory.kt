package io.agora.chatroom.ui.viewmodel.messages

import android.content.Context
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import io.agora.chatroom.ui.UIChatroomService
import io.agora.chatroom.ui.commons.ComposeChatListController
import io.agora.chatroom.ui.commons.ComposerChatBarController
import io.agora.chatroom.ui.model.UIChatBarMenuItem
import io.agora.chatroom.ui.theme.primaryColor8
import io.agora.chatroom.ui.theme.secondaryColor8
import io.agora.chatroom.uikit.R
import java.util.concurrent.TimeUnit

class MessagesViewModelFactory(
    private val context: Context?,
    private val service: UIChatroomService,
    private val isDarkTheme: Boolean = false,
    private val messageLimit: Int = MessageListViewModel.DefaultMessageLimit,
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
    private val dateSeparatorThresholdMillis: Long = TimeUnit.HOURS.toMillis(MessageListViewModel.DateSeparatorDefaultHourThreshold),
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
                    chatService = service.getChatService()
                )
            )
        },
        MessageListViewModel::class.java to {
            MessageListViewModel(
                isDarkTheme = isDarkTheme,
                messageLimit = messageLimit,
                showDateSeparators = showDateSeparators,
                showLabel = showLabel,
                showGift = showGift,
                showAvatar = showAvatar,
                dateSeparatorColor = dateSeparatorColor,
                nickNameColor = nickNameColor,
                dateSeparatorThresholdMillis = dateSeparatorThresholdMillis,
                composeChatListController = ComposeChatListController(
                    roomId = service.getRoomInfo().roomId,
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