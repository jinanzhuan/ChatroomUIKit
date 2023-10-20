package io.agora.chatroom.compose.list

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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

    Box(modifier = modifier) {
        LazyColumn(
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
                Box(modifier = Modifier) {
                    itemContent(index,item)
                }
            }
        }
    }
}