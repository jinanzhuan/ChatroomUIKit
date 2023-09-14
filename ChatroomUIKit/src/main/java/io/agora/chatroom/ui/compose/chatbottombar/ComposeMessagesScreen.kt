package io.agora.chatroom.ui.compose.chatbottombar

import android.content.Context
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import io.agora.chatroom.ui.viewmodel.messages.MessageComposerViewModel
import io.agora.chatroom.ui.viewmodel.messages.MessageListViewModel
import io.agora.chatroom.ui.viewmodel.messages.MessagesViewModelFactory
import androidx.lifecycle.viewmodel.compose.viewModel


@Composable
public fun MessagesScreen(
    roomId: String,
    isDarkTheme:Boolean = false,
    messageLimit: Int = MessageListViewModel.DefaultMessageLimit,
    showDateSeparators: Boolean = true,
    showSystemMessages: Boolean = true,
){
    val factory = buildViewModelFactory(
        context = LocalContext.current,
        roomId = roomId,
        messageLimit = messageLimit,
        showDateSeparators = showDateSeparators,
        showSystemMessages = showSystemMessages
    )

    val listViewModel = viewModel(MessageListViewModel::class.java, factory = factory)
    val composerViewModel = viewModel(MessageComposerViewModel::class.java, factory = factory)

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {},
            bottomBar = {
                ComposeChatBottomBar(
                    isDarkTheme = isDarkTheme,
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .align(Alignment.Center),
                    viewModel = composerViewModel,
                    onSendMessage = { message->
                        composerViewModel.sendMessage(message)
                    },
                )
            }
        ){
            it.calculateTopPadding()
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(text = "列表")
            }
        }
    }

}

private fun buildViewModelFactory(
    context: Context,
    roomId: String,
    messageLimit: Int,
    showDateSeparators: Boolean,
    showSystemMessages: Boolean,
): MessagesViewModelFactory {
    return MessagesViewModelFactory(
        context = context,
        roomId = roomId,
        messageLimit = messageLimit,
        showDateSeparators = showDateSeparators,
        showSystemMessages = showSystemMessages
    )
}
