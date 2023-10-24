package io.agora.chatroom.compose.list

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import io.agora.chatroom.viewmodel.ComposeBaseListViewModel
import kotlinx.coroutines.delay


@Composable
fun <T> ComposeBaseList(
    viewModel: ComposeBaseListViewModel<T>,
    modifier: Modifier = Modifier,
    reverseLayout:Boolean = true,
    horizontal:Alignment.Horizontal = Alignment.Start,
    contentPadding: PaddingValues = PaddingValues(vertical = 16.dp),
    itemContent: @Composable (Int, T) -> Unit,
){
    val items = viewModel.items
    var parentSize by remember { mutableStateOf(IntSize(0, 0)) }
    val density = LocalDensity.current
    var timer by remember { mutableLongStateOf(0L) }

    val listState = rememberLazyListState()
//    val addItemAnimation by animateDpAsState(targetValue = 0.dp, animationSpec = spring(), label = "addItem")
//    val removeItemAnimation by animateDpAsState(targetValue = 0.dp, animationSpec = spring(), label = "removeItem")

    if (viewModel.isAutoClear.value){
        LaunchedEffect(items) {
            timer = System.currentTimeMillis() // 记录当前时间
            delay(viewModel.autoClearTime.value) // 等待3秒
            if (System.currentTimeMillis() - timer >= 3000 && items.isNotEmpty()) { // 判断是否有新数据加入
                viewModel.clear() // 删除所有数据
            }
        }
    }

    Box(modifier = modifier) {
        LazyColumn(
            state = listState,
            modifier = modifier
                .onGloballyPositioned {
                    val bottomPadding = contentPadding.calculateBottomPadding()
                    val topPadding = contentPadding.calculateTopPadding()

                    val paddingPixels = with(density) {
                        bottomPadding.roundToPx() + topPadding.roundToPx()
                    }

                    parentSize = IntSize(
                        width = it.size.width,
                        height = it.size.height - paddingPixels
                    )
                }
            ,
            horizontalAlignment = horizontal,
            reverseLayout = reverseLayout,
            contentPadding = contentPadding
        ){
            itemsIndexed(items){index, item ->
//                AnimatedContent(
//                    targetState = item,
//                    transitionSpec = {
//                        (fadeIn(animationSpec = tween(220, delayMillis = 90)) +
//                                scaleIn(initialScale = 0.92f, animationSpec = tween(220, delayMillis = 90)))
//                        .togetherWith(fadeOut(animationSpec = tween(90)))
//                    },
//                    label = "GiftItemAnimated"
//                ){state ->
//                    Text(text = "$state", modifier = Modifier.padding(vertical = 8.dp))
//                }
                Box(modifier = Modifier) {
                    itemContent(index,item)
                }
            }
        }
    }
}