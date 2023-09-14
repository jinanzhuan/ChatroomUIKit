package io.agora.chatroom.ui.viewmodel.messages

import androidx.lifecycle.ViewModel
import java.util.concurrent.TimeUnit

class MessageListViewModel(
    private val roomId:String,
    private val messageLimit: Int = DefaultMessageLimit,
    private val showDateSeparators: Boolean = true,
    private val showSystemMessages: Boolean = true,
    private val dateSeparatorThresholdMillis: Long = TimeUnit.HOURS.toMillis(DateSeparatorDefaultHourThreshold),
    ): ViewModel() {


    internal companion object {
        /**
         * The default threshold for showing date separators. If the message difference in hours is equal to this
         * number, then we show a separator, if it's enabled in the list.
         */
        internal const val DateSeparatorDefaultHourThreshold: Long = 4

        /**
         * The default limit for messages count in requests.
         */
        internal const val DefaultMessageLimit: Int = 30

    }
}