package io.agora.chatroom.compose.drawer

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.agora.chatroom.model.UIComposeDrawerItem
import io.agora.chatroom.theme.AlphabetBodyLarge
import io.agora.chatroom.theme.AlphabetBodyMedium
import io.agora.chatroom.theme.errorColor5
import io.agora.chatroom.theme.errorColor6
import io.agora.chatroom.theme.neutralColor0
import io.agora.chatroom.theme.neutralColor1
import io.agora.chatroom.theme.neutralColor2
import io.agora.chatroom.theme.neutralColor5
import io.agora.chatroom.theme.neutralColor6
import io.agora.chatroom.theme.neutralColor9
import io.agora.chatroom.theme.neutralColor98
import io.agora.chatroom.theme.primaryColor5
import io.agora.chatroom.theme.primaryColor6
import io.agora.chatroom.viewmodel.menu.MenuViewModel
import io.agora.chatroom.uikit.R
import io.agora.chatroom.viewmodel.menu.BottomDrawerViewModel
import kotlinx.coroutines.launch


@ExperimentalMaterialApi
@Composable
fun <T> ComposeBottomDrawer(
    viewModel: BottomDrawerViewModel<T>,
    modifier: Modifier = Modifier,
    drawerContent: @Composable () -> Unit = {},
    screenContent: @Composable () -> Unit = {},
    onCancelListener:() -> Unit = {},
    drawerShape: Shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
    drawerStateValue: BottomDrawerValue = BottomDrawerValue.Closed,
    gesturesEnabled: Boolean = true,
    drawerElevation: Dp = DrawerDefaults.Elevation,
    drawerBackgroundColor: Color = if (viewModel.getTheme == true) neutralColor1 else neutralColor98,
    drawerContentColor: Color = contentColorFor(drawerBackgroundColor),
    scrimColor: Color = DrawerDefaults.scrimColor,
    contentDescription: String? = null,
) {
    if (viewModel.isEnable()) {
        val isBottomDrawerVisible = viewModel.isBottomDrawerVisible.value
        val drawerState = rememberBottomDrawerState(drawerStateValue)
        val scope = rememberCoroutineScope()
        val isDarkTheme = viewModel.getTheme
        val isShowTitle = viewModel.getIsShowTitle
        val isShowCancel = viewModel.getIsShowCancel
        BottomDrawer(
            modifier = modifier,
            drawerShape = drawerShape,
            drawerBackgroundColor = drawerBackgroundColor,
            drawerContentColor = drawerContentColor,
            scrimColor = scrimColor,
            drawerState = drawerState,
            gesturesEnabled = gesturesEnabled,
            drawerElevation = drawerElevation,
            drawerContent = {
                Column(
                    modifier = modifier
                ){
                    Image(
                        modifier = Modifier
                            .padding(top = 6.dp)
                            .align(Alignment.CenterHorizontally),
                        painter = painterResource(
                            id = if (isDarkTheme == true)
                                R.drawable.icon_rectangle_dark else R.drawable.icon_rectangle_light
                        ),
                        contentDescription = contentDescription
                    )

                    if (isShowTitle){
                        Text(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 13.dp, bottom = 13.dp),
                            textAlign = TextAlign.Center,
                            text = viewModel.getTitle,
                            color = if (isDarkTheme == true) neutralColor6 else neutralColor5,
                            style = AlphabetBodyMedium
                        )
                    }

                    drawerContent()

                    if (isShowCancel){
                        Divider(
                            modifier = Modifier
                                .height(8.dp)
                                .background(if (isDarkTheme == true) neutralColor0 else neutralColor98)
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
                            color = if (isDarkTheme == true) primaryColor6 else primaryColor5,
                            style = AlphabetBodyLarge
                        )
                    }
                }
            },
            content = {
                Box(Modifier.padding(bottom = with(LocalDensity.current) { drawerState.offset.value.dp })) {
                    screenContent()
                }
            }
        )

        LaunchedEffect(drawerState.isOpen) {
            if (!drawerState.isOpen) {
                viewModel.closeDrawer()
                if (viewModel.isVisible()) {
                    viewModel.setVisible(false)
                    viewModel.setEnable(false)
                }
            } else {
                viewModel.setVisible(true)
            }
        }

        LaunchedEffect(isBottomDrawerVisible){
            if (isBottomDrawerVisible){
                scope.launch {
                    drawerState.open()
                }
            }else{
                scope.launch {
                    drawerState.close()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ComposeMenuBottomDrawer(
    viewModel: MenuViewModel,
    modifier: Modifier = Modifier,
    onListItemClick: (Int, UIComposeDrawerItem) -> Unit,
    drawerContent: @Composable () -> Unit = { DefaultDrawerContent(viewModel, onListItemClick)},
    screenContent: @Composable () -> Unit = {},
    onCancelListener:() -> Unit = {},
    drawerShape: Shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
    drawerStateValue: BottomDrawerValue = BottomDrawerValue.Closed,
    gesturesEnabled: Boolean = true,
    drawerElevation: Dp = DrawerDefaults.Elevation,
    drawerBackgroundColor: Color = if (viewModel.getTheme == true) neutralColor1 else neutralColor98,
    drawerContentColor: Color = contentColorFor(drawerBackgroundColor),
    scrimColor: Color = DrawerDefaults.scrimColor,
) {
    ComposeBottomDrawer(viewModel = viewModel,
        modifier = modifier,
        drawerShape = drawerShape,
        drawerBackgroundColor = drawerBackgroundColor,
        drawerContentColor = drawerContentColor,
        scrimColor = scrimColor,
        drawerStateValue = drawerStateValue,
        gesturesEnabled = gesturesEnabled,
        drawerElevation = drawerElevation,
        drawerContent = drawerContent,
        screenContent = screenContent,
        onCancelListener = onCancelListener
    )
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun DefaultDrawerContent(viewModel: MenuViewModel, onListItemClick: (Int, UIComposeDrawerItem) -> Unit){
    val items = remember { mutableStateListOf<UIComposeDrawerItem>() }
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
                        .background(if (viewModel.getTheme == true) neutralColor2 else neutralColor9)
                        .fillMaxWidth()
                        .padding(top = 10.dp, bottom = 10.dp)
                )
            }
            ListItem(
                text = {
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 10.dp, bottom = 10.dp),
                        textAlign = TextAlign.Center,
                        style = AlphabetBodyLarge,
                        color = if (item.title == "Report"){
                            if (viewModel.getTheme == true) errorColor6 else errorColor5
                        }else{
                            if (viewModel.getTheme == true) primaryColor6 else primaryColor5
                        },

                        text = item.title
                    )
                },
                modifier = Modifier
                    .background(if (viewModel.getTheme == true) neutralColor1 else neutralColor98)
                    .clickable {
                        onListItemClick(index, item)
                        viewModel.closeDrawer()
                    }
            )
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Preview(showBackground = true)
@Composable
fun JetPackComposeBottomDrawer() {
    ComposeBottomDrawer(
        viewModel = MenuViewModel(),
        drawerContent = {
            Text("Drawer Content")
        },
        screenContent = {
            Text("Screen Content")
        },
        onCancelListener = {

        }
    )
}