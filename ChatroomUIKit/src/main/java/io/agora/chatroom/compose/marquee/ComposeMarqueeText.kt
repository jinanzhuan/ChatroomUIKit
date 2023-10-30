package io.agora.chatroom.compose.marquee

import android.util.Log
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.TargetBasedAnimation
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

@Composable
fun MarqueeText(
    text:String,
    count:Int = 1,
    content:List<String> = listOf<String>(),
    gradientEdgeColor: Color = Color.White,
    modifier: Modifier = Modifier,
    textModifier:Modifier = Modifier,
    textAlign: TextAlign,
    overflow: TextOverflow = TextOverflow.Clip,
    softWrap:Boolean = true,
    onTextLayout:(TextLayoutResult) -> Unit = {},
    style:TextStyle = LocalTextStyle.current
){
    if (content.isEmpty()){ return }
    var marText by remember { mutableStateOf("")}

    LaunchedEffect(content.size){
        marText = content[0]
    }

    val createText = @Composable {
        localModifier: Modifier ->
        Text(
            modifier = localModifier,
            text = marText,
            textAlign = textAlign,
            style = style,
            onTextLayout = onTextLayout,
            maxLines = 1,
            softWrap = softWrap,
            overflow = overflow
        )
    }

    var offset by remember { mutableIntStateOf(0) }
    val textLayoutInfoState = remember { mutableStateOf<TextLayoutInfo?>(null)}
    LaunchedEffect(textLayoutInfoState.value){
        val textLayoutInfo = textLayoutInfoState.value ?: return@LaunchedEffect
        if (textLayoutInfo.textWidth <= textLayoutInfo.containerWidth) return@LaunchedEffect
        if (textLayoutInfo.containerWidth == 0) return@LaunchedEffect

        //计算播放一遍的总时间
        val duration = 7500 * textLayoutInfo.textWidth / textLayoutInfo.containerWidth
        val delay = 1000L

        do{
            val animation = TargetBasedAnimation(
                animationSpec = infiniteRepeatable(
                    animation = tween(
                        durationMillis = duration,
                        delayMillis = 1000,
                        easing = LinearEasing,
                    ),
                    repeatMode = RepeatMode.Restart
                ),
                typeConverter = Int.VectorConverter,
                initialValue = 0,
                targetValue =  - textLayoutInfo.textWidth
            )
            //根据动画帧时间，获取偏移量
            //起始帧时间
            val startTime = withFrameNanos { it }
            do {
                val playTime = withFrameNanos { it } - startTime
                offset = animation.getValueFromNanos(playTime)
                if (offset == 0){

                }
            } while (!animation.isFinishedFromNanos(playTime))

//            delay(delay)

        }while (true)
    }

    SubcomposeLayout(
        modifier = modifier.clipToBounds()
    ){ constraints ->

        // 测量文本总宽度
        val infiniteWidthConstraints = constraints.copy(maxWidth = Int.MAX_VALUE)
        var mainText = subcompose(MarqueeLayers.MainText) {
            createText(textModifier)
        }.first().measure(infiniteWidthConstraints)

        val totalWidth = mainText.width
        Log.e("apex","totalWidth $totalWidth")

        var gradient: Placeable? = null
//        var secondPlaceableWithOffset: Pair<Placeable, Int>? = null

        if (mainText.width <= constraints.maxWidth) {// 文本宽度小于容器最大宽度， 则无需跑马灯动画
            mainText = subcompose(MarqueeLayers.SecondaryText) {
                createText(textModifier.fillMaxWidth())
            }.first().measure(constraints)
            textLayoutInfoState.value = null
        } else {
            // 循环文本增加间隔
            val spacing = constraints.maxWidth * 2 / 3
            textLayoutInfoState.value = TextLayoutInfo(
                textWidth = mainText.width+spacing,
                containerWidth = constraints.maxWidth
            )
//            // 第二遍文本偏移量
//            val secondTextOffset = mainText.width + offset + spacing
//            val secondTextSpace = constraints.maxWidth - secondTextOffset
//            if (secondTextSpace > 0) {
//                secondPlaceableWithOffset = subcompose(MarqueeLayers.SecondaryText) {
//                    createText(textModifier)
//                }.first().measure(infiniteWidthConstraints) to secondTextOffset
//            }
            // 测量左右两边渐变控件
            gradient = subcompose(MarqueeLayers.EdgesGradient) {
                Row {
                    GradientEdge(gradientEdgeColor, Color.Transparent)
                    Spacer(Modifier.weight(1f))
                    GradientEdge(Color.Transparent, gradientEdgeColor)
                }
            }.first().measure(constraints.copy(maxHeight = mainText.height))
        }

        // 将文本、渐变控件 进行位置布局
        layout(
            width = constraints.maxWidth,
            height = mainText.height
        ) {
            mainText.place(offset, 0)
//            secondPlaceableWithOffset?.let {
//                it.first.place(it.second, 0)
//            }
            gradient?.place(0, 0)
        }
    }
}

/**
 * 渐变侧边
 */
@Composable
private fun GradientEdge(
    startColor: Color, endColor: Color,
) {
    Box(
        modifier = Modifier
            .width(10.dp)
            .fillMaxHeight()
            .background(
                brush = Brush.horizontalGradient(
                    0f to startColor, 1f to endColor,
                )
            )
    )
}

private enum class MarqueeLayers{ MainText,SecondaryText,EdgesGradient}

private data class TextLayoutInfo(val textWidth:Int,val containerWidth:Int)



