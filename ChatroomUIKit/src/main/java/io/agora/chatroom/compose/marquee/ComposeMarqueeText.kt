package io.agora.chatroom.compose.marquee

import android.graphics.Paint
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import io.agora.chatroom.model.marquee.UINotification
import kotlinx.coroutines.delay

@Composable
fun MainScreen(
    notification: UINotification,
) {
    if (notification.content.size <= 0) return

    val text = remember { mutableStateOf( if (notification.content.size > 0) notification.content[0] else "") }

    val offsetX = remember { mutableFloatStateOf(0f) }

    var initValue by rememberSaveable { mutableFloatStateOf(1f) }

    val animateValue = remember { Animatable(initValue) }

    Box(
        modifier = Modifier.padding(top = 100.dp)
    ) {
        // 创建一个画布
        Canvas(modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
        ) {
            offsetX.floatValue = size.width * animateValue.value
            drawContext.canvas.nativeCanvas.drawText(
                text.value,
                offsetX.floatValue,
                drawContext.size.height / 2 ,
                Paint().apply {
                    textAlign = Paint.Align.LEFT
                    textSize = 40f
                    color = Color.Black.toArgb()
                }
            )
        }
    }

    LaunchedEffect(initValue) {
        animateValue.animateTo(
            targetValue = 0f, // 目标位置
            animationSpec = tween(durationMillis = 3000) // 动画持续时间
        ) {
            if (this.value == 0.0f) {
                initValue = this.value
            }
        }

        if (!animateValue.isRunning) {
            if (notification.content.size > 0) {
                notification.content.removeAt(0)
                delay(3000)
                text.value = if (notification.content.size > 0) notification.content[0] else ""
                initValue = 1f
                animateValue.snapTo(initValue)
            } else {
                text.value = ""
                return@LaunchedEffect
            }
        }
    }
}
