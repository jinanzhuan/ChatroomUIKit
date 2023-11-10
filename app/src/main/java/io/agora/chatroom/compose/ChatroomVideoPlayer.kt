package io.agora.chatroom.compose

import android.net.Uri
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import io.agora.chatroom.theme.ChatroomUIKitTheme
import io.agora.chatroom.widget.CustomVideoView

@Composable
fun VideoPlayerCompose(
    uri: Uri,
    modifier: Modifier = Modifier
) {
    Surface(
        color = ChatroomUIKitTheme.colors.background,
        modifier = modifier
    ) {
        val height = (LocalContext.current.resources.displayMetrics.heightPixels * 1.03).toInt()
        val width = LocalContext.current.resources.displayMetrics.widthPixels
        AndroidView(
            factory = { context ->
                CustomVideoView(context).apply {
                    setVideoURI(uri)
                    val radio = height * 1.0 / width
                    val videoRadio = 2.0
                    if (radio > videoRadio) {
                        val videoWidth = (height / videoRadio).toInt()
                        setVideoSize(videoWidth, height)
                    }else {
                        val videoHeight = (width * videoRadio).toInt()
                        setVideoSize(width, videoHeight)
                    }
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

