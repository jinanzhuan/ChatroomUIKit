package io.agora.chatroom.compose.drawer

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.agora.chatroom.model.UIComposeSheetItem
import io.agora.chatroom.theme.BodyLarge
import io.agora.chatroom.theme.ChatroomUIKitTheme
import io.agora.chatroom.theme.errorColor50
import io.agora.chatroom.theme.errorColor60
import io.agora.chatroom.theme.neutralColor10
import io.agora.chatroom.theme.neutralColor20
import io.agora.chatroom.theme.neutralColor90
import io.agora.chatroom.theme.neutralColor98
import io.agora.chatroom.theme.primaryColor50
import io.agora.chatroom.theme.primaryColor60
import io.agora.chatroom.viewmodel.menu.MenuViewModel
import io.agora.chatroom.viewmodel.menu.BottomSheetViewModel
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> ComposeBottomSheet(
    modifier: Modifier = Modifier,
    viewModel: BottomSheetViewModel<T>,
    drawerContent: @Composable () -> Unit = {},
    screenContent: @Composable () -> Unit = {},
    onCancelListener:() -> Unit = {},
    onDismissRequest: () -> Unit,
    shape: Shape = ChatroomUIKitTheme.shapes.bottomSheet,
    containerColor: Color = BottomSheetDefaults.ContainerColor,
    contentColor: Color = contentColorFor(containerColor),
    tonalElevation: Dp = BottomSheetDefaults.Elevation,
    scrimColor: Color = BottomSheetDefaults.ScrimColor,
    dragHandle: @Composable (() -> Unit)? = { BottomSheetDefaults.DragHandle() },
    windowInsets: WindowInsets = BottomSheetDefaults.windowInsets,
) {
    if (viewModel.isEnable()) {
        val isBottomSheetVisible = viewModel.isBottomSheetVisible.value
        val scope = rememberCoroutineScope()
        val bottomSheetState = rememberModalBottomSheetState(
            skipPartiallyExpanded = viewModel.isExpanded
        )
        val isShowTitle = viewModel.getIsShowTitle
        val isShowCancel = viewModel.getIsShowCancel

        ModalBottomSheet(
            onDismissRequest = onDismissRequest,
            sheetState = bottomSheetState,
            shape = shape,
            containerColor = containerColor,
            contentColor = contentColor,
            tonalElevation = tonalElevation,
            scrimColor = scrimColor,
            dragHandle = dragHandle,
            windowInsets = windowInsets,
            modifier = modifier) {

            Column(
                modifier = modifier
            ) {

                if (isShowTitle) {
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 0.dp),
                        textAlign = TextAlign.Center,
                        text = viewModel.getTitle,
                        color = ChatroomUIKitTheme.colors.onBackground,
                        style = MaterialTheme.typography.titleMedium
                    )
                }

                drawerContent()

                if (isShowCancel) {
                    Divider(
                        modifier = Modifier
                            .height(8.dp)
                            .background(ChatroomUIKitTheme.colors.onBackground)
                    )
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 13.dp, bottom = 13.dp)
                            .clickable {
                                onCancelListener()
                                viewModel.closeDrawer()
                            },
                        textAlign = TextAlign.Center,
                        text = stringResource(id = viewModel.getCancelText),
                        color = ChatroomUIKitTheme.colors.primary,
                        style = ChatroomUIKitTheme.typography.bodyLarge
                    )
                }

                Box {
                    screenContent()
                }
            }
        }

        LaunchedEffect(isBottomSheetVisible){
            if (isBottomSheetVisible){
                viewModel.setVisible(true)
            }else{
                scope.launch {
                    bottomSheetState.hide()
                }.invokeOnCompletion {
                    viewModel.closeDrawer()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ComposeMenuBottomSheet(
    viewModel: MenuViewModel,
    modifier: Modifier = Modifier,
    onListItemClick: (Int, UIComposeSheetItem) -> Unit,
    drawerContent: @Composable () -> Unit = { DefaultDrawerContent(viewModel, onListItemClick)},
    screenContent: @Composable () -> Unit = { defaultScreenContent() },
    onCancelListener:() -> Unit = {},
    onDismissRequest: () -> Unit,
    shape: Shape = ChatroomUIKitTheme.shapes.bottomSheet,
    containerColor: Color = BottomSheetDefaults.ContainerColor,
    contentColor: Color = contentColorFor(containerColor),
    tonalElevation: Dp = BottomSheetDefaults.Elevation,
    scrimColor: Color = BottomSheetDefaults.ScrimColor,
    dragHandle: @Composable (() -> Unit)? = { BottomSheetDefaults.DragHandle() },
    windowInsets: WindowInsets = BottomSheetDefaults.windowInsets,
) {
    ComposeBottomSheet(
        modifier = modifier,
        viewModel = viewModel,
        drawerContent = drawerContent,
        screenContent = screenContent,
        onCancelListener = onCancelListener,
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
fun DefaultDrawerContent(viewModel: MenuViewModel, onListItemClick: (Int, UIComposeSheetItem) -> Unit){
    val items = remember { mutableStateListOf<UIComposeSheetItem>() }
    items.addAll(viewModel.list)
    LazyColumn(
        modifier = Modifier
            .padding(start = 16.dp, end = 16.dp, bottom = 8.dp)
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        itemsIndexed(viewModel.list){ index, item ->
            if (index > 0){
                Divider(
                    modifier = Modifier
                        .height(0.5.dp)
                        .background(ChatroomUIKitTheme.colors.outlineVariant)
                        .fillMaxWidth()
                        .padding(top = 10.dp, bottom = 10.dp)
                )
            }
            ListItem(
                headlineContent = {
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 10.dp, bottom = 10.dp),
                        textAlign = TextAlign.Center,
                        style = ChatroomUIKitTheme.typography.bodyLarge,
                        color = if (item.isError) ChatroomUIKitTheme.colors.error else ChatroomUIKitTheme.colors.primary,
                        text = item.title
                    )
                },
                modifier = Modifier
                    .background(ChatroomUIKitTheme.colors.onBackground)
                    .clickable {
                        onListItemClick(index, item)
                        viewModel.closeDrawer()
                    }
            )
        }
    }
}

@Composable
fun defaultScreenContent(){
    Divider(modifier = Modifier.height(34.dp).background(Color.Red))
}

@Preview(showBackground = true)
@Composable
fun JetpackComposeBottomSheet() {
    ComposeBottomSheet(
        viewModel = MenuViewModel(),
        drawerContent = {
            Text("Drawer Content")
        },
        screenContent = {
            Text("Screen Content")
        },
        onDismissRequest = {

        }
    )
}