package io.agora.chatroom.viewmodel.report

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import io.agora.chatroom.data.reportTagList
import io.agora.chatroom.ui.UIChatroomService

class ReportViewModelFactory(
    private val service: UIChatroomService
): ViewModelProvider.Factory {
    /**
     * The list of factories that can build [ViewModel]s that our Report feature components use.
     */
    private val factories: Map<Class<*>, () -> ViewModel> = mapOf(
        ComposeReportViewModel::class.java to {
            ComposeReportViewModel(
                reportTag = reportTagList,
                service = service
            )
        }
    )

    /**
     * Creates the required [ViewModel] for our use case, based on the [factories] we provided.
     */
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val viewModel: ViewModel = factories[modelClass]?.invoke()
            ?: throw IllegalArgumentException(
                "ReportViewModelFactory can only create instances of " +
                        "the following classes: ${factories.keys.joinToString { it.simpleName }}"
            )

        @Suppress("UNCHECKED_CAST")
        return viewModel as T
    }
}