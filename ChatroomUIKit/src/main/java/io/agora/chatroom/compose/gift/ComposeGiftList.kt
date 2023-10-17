package io.agora.chatroom.compose.gift

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import io.agora.chatroom.compose.drawer.ComposeBottomSheet
import io.agora.chatroom.compose.drawer.defaultScreenContent
import io.agora.chatroom.service.GiftEntity
import io.agora.chatroom.theme.ChatroomUIKitTheme
import io.agora.chatroom.viewmodel.gift.ComposeGiftViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
 fun ComposeGiftList(
     viewModel:ComposeGiftViewModel,
     modifier: Modifier = Modifier,
     onListItemClick: (Int, GiftEntity) -> Unit,
     drawerContent: @Composable () -> Unit = { DefaultGiftContent(viewModel, onListItemClick) },
     screenContent: @Composable () -> Unit = { defaultScreenContent() },
     onDismissRequest: () -> Unit,
     shape: Shape = ChatroomUIKitTheme.shapes.bottomSheet,
     containerColor: Color = BottomSheetDefaults.ContainerColor,
     contentColor: Color = contentColorFor(containerColor),
     tonalElevation: Dp = BottomSheetDefaults.Elevation,
     scrimColor: Color = BottomSheetDefaults.ScrimColor,
     dragHandle: @Composable (() -> Unit)? = { BottomSheetDefaults.DragHandle() },
     windowInsets: WindowInsets = BottomSheetDefaults.windowInsets,
 ){
    ComposeBottomSheet(
        modifier = modifier,
        viewModel = viewModel,
        drawerContent = drawerContent,
        screenContent = screenContent,
        onDismissRequest = onDismissRequest,
        shape = shape,
        containerColor = containerColor,
        contentColor = contentColor,
        tonalElevation = tonalElevation,
        scrimColor = scrimColor,
        dragHandle = dragHandle,
        windowInsets = windowInsets
    )
}

@Composable
fun DefaultGiftContent(viewModel: ComposeGiftViewModel, onListItemClick: (Int, GiftEntity) -> Unit){
    val items = remember { mutableStateListOf<GiftEntity>() }


}