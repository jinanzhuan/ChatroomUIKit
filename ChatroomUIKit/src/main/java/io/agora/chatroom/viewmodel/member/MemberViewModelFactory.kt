package io.agora.chatroom.viewmodel.member

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import io.agora.chatroom.ui.UIChatroomService

class MemberViewModelFactory(
    private val context: Context,
    private val roomId: String,
    private val service: UIChatroomService,
): ViewModelProvider.Factory {

    private val factories: Map<Class<*>, () -> ViewModel> = mapOf(
        MemberListViewModel::class.java to {
            MemberListViewModel(
                roomId, service
            )
        },
        MutedListViewModel::class.java to {
            MutedListViewModel(
                roomId, service
            )
        }
    )

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val viewModel: ViewModel = factories[modelClass]?.invoke()
            ?: throw IllegalArgumentException(
                "MemberViewModelFactory can only create instances of " +
                        "the following classes: ${factories.keys.joinToString { it.simpleName }}"
            )

        @Suppress("UNCHECKED_CAST")
        return viewModel as T
    }
}