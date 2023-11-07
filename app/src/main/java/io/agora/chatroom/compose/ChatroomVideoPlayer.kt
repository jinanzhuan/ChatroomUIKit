package io.agora.chatroom.compose

import android.net.Uri
import android.util.DisplayMetrics
import android.view.ViewGroup
import android.widget.VideoView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.viewinterop.AndroidView
import io.agora.chatroom.R
import io.agora.chatroom.theme.ChatroomUIKitTheme

@Composable
fun VideoPlayerCompose(uri: Uri) {
    Surface(
        color = ChatroomUIKitTheme.colors.background,
        modifier = Modifier.fillMaxHeight()
    ) {
        AndroidView(
            factory = { context ->
                VideoView(context).apply {
//                    background = getContext().getDrawable(R.drawable.splash_bg)
                    layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
                    setVideoURI(uri)
                    setOnPreparedListener { mediaPlayer ->
                        mediaPlayer.isLooping = true
                    }
                    start()
                }
            },
            modifier = Modifier.fillMaxSize()
        )
    }
}