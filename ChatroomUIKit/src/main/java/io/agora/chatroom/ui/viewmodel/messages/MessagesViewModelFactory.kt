package io.agora.chatroom.ui.viewmodel.messages

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import io.agora.chatroom.ui.commons.MessageComposerController
import java.util.concurrent.TimeUnit

class MessagesViewModelFactory(
    private val context: Context,
    private val roomId: String,
    private val messageLimit: Int = MessageListViewModel.DefaultMessageLimit,
    private val showDateSeparators: Boolean = true,
    private val showSystemMessages: Boolean = true,
    private val dateSeparatorThresholdMillis: Long = TimeUnit.HOURS.toMillis(MessageListViewModel.DateSeparatorDefaultHourThreshold),
) : ViewModelProvider.Factory{

    /**
     * The list of factories that can build [ViewModel]s that our Messages feature components use.
     */
    private val factories: Map<Class<*>, () -> ViewModel> = mapOf(
        MessageComposerViewModel::class.java to {
            MessageComposerViewModel(
                MessageComposerController(
                    roomId = roomId
                )
            )
        },
        MessageListViewModel::class.java to {
            MessageListViewModel(
                roomId = roomId,
                messageLimit = messageLimit,
                showDateSeparators = showDateSeparators,
                showSystemMessages = showSystemMessages,
                dateSeparatorThresholdMillis = dateSeparatorThresholdMillis,
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