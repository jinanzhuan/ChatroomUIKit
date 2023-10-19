package io.agora.chatroom.compose.gift

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
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
import io.agora.chatroom.viewmodel.gift.ComposeGiftListViewModel

@Composable
fun ComposeGiftList(
    viewModel:ComposeGiftListViewModel,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(vertical = 16.dp),
    onItemClick: (Int, ComposeGiftListItemState) -> Unit = { index, message-> },
    itemContent: @Composable (Int, ComposeGiftListItemState) -> Unit = { index, item ->
        DefaultGiftItemContent(
            itemIndex = index,
            giftListItem = item,
            viewModel = viewModel,
            onItemClick = onItemClick,
        )
    },
    emptyContent: @Composable () -> Unit = { },
) {
    when{
        (viewModel.items.isNotEmpty())->{
            ComposeBaseList(
                viewModel = viewModel,
                modifier = modifier,
                itemContent = itemContent,
                contentPadding = contentPadding,
            )
        }
        else -> {
            emptyContent()
        }
    }
}

@Composable
fun <T> ComposeBaseList(
    viewModel: ComposeBaseListViewModel<T>,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(vertical = 16.dp),
    itemContent: @Composable (Int, T) -> Unit,
){
    val items = viewModel.items
    var parentSize by remember { mutableStateOf(IntSize(0, 0)) }
    val density = LocalDensity.current
    Box(modifier = modifier) {
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
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
                },
            horizontalAlignment = Alignment.Start,
            reverseLayout = true,
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

@Composable
fun DefaultGiftItemContent(
    itemIndex:Int,
    giftListItem:ComposeGiftListItemState,
    viewModel:ComposeGiftListViewModel,
    onItemClick: (Int,ComposeGiftListItemState) -> Unit,
){
    when (giftListItem) {
        //is ComposeDynamicGiftListItemState -> ComposeDynamicGiftItem(giftListItem)
        else -> ComposeGiftItem(viewModel,itemIndex,giftListItem,onItemClick)
    }
}

@Composable
fun ComposeGiftItem(
    viewModel: ComposeGiftListViewModel,
    itemIndex:Int,
    item:ComposeGiftListItemState,
    onItemClick: (Int,ComposeGiftListItemState) -> Unit,
){
    val giftMessage = (item as ComposeGiftItemState).message

}