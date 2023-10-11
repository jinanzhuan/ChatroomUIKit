package io.agora.chatroom.ui.compose.drawer

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.BottomDrawer
import androidx.compose.material.BottomDrawerValue
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Text
import androidx.compose.material.rememberBottomDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import io.agora.chatroom.ui.compose.DefaultDrawerContent
import io.agora.chatroom.ui.compose.DrawerType
import io.agora.chatroom.ui.model.UIComposeDrawerItem
import io.agora.chatroom.ui.theme.AlphabetBodyLarge
import io.agora.chatroom.ui.theme.AlphabetBodyMedium
import io.agora.chatroom.ui.theme.neutralColor0
import io.agora.chatroom.ui.theme.neutralColor1
import io.agora.chatroom.ui.theme.neutralColor5
import io.agora.chatroom.ui.theme.neutralColor6
import io.agora.chatroom.ui.theme.neutralColor98
import io.agora.chatroom.ui.theme.primaryColor5
import io.agora.chatroom.ui.theme.primaryColor6
import io.agora.chatroom.ui.viewmodel.menu.MenuViewModel
import io.agora.chatroom.uikit.R
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ComposeMenuBottomDrawer(
    viewModel: MenuViewModel,
    modifier: Modifier = Modifier,
    onListItemClick: (Int, UIComposeDrawerItem) -> Unit = { index:Int, item: UIComposeDrawerItem ->},
    drawerContent: @Composable () -> Unit = { DefaultDrawerContent(viewModel,onListItemClick) },
    screenContent: @Composable () -> Unit = {},
    onCancelListener:() -> Unit = {}
){
    val scope = rememberCoroutineScope()
    val isDarkTheme = viewModel.getTheme
    val title = viewModel.getTitle
    val isShowTitle = viewModel.getIsShowTitle
    val isShowCancel = viewModel.getIsShowCancel

    val isBottomDrawerVisible = viewModel.isBottomDrawerVisible.value
    val bottomDrawerState =  rememberBottomDrawerState(initialValue = BottomDrawerValue.Closed)

    if (viewModel.currentDrawerType.value == DrawerType.MENU_LIST){
        BottomDrawer(
            modifier = modifier,
            drawerShape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
            drawerBackgroundColor = if (isDarkTheme == true) neutralColor1 else neutralColor98,
            drawerState = bottomDrawerState,
            gesturesEnabled = bottomDrawerState.isOpen,
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
                        contentDescription = "icon"
                    )

                    if (isShowTitle){
                        Text(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 13.dp, bottom = 13.dp),
                            textAlign = TextAlign.Center,
                            text = title,
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
                            text = "Cancel",
                            color = if (isDarkTheme == true) primaryColor6 else primaryColor5,
                            style = AlphabetBodyLarge
                        )
                    }
                }
            },
            content = {
                Box(Modifier.padding(bottom = with(LocalDensity.current) { bottomDrawerState.offset.value.dp })) {
                    screenContent()
                }
            }
        )
        LaunchedEffect(bottomDrawerState.isOpen) {
            if (!bottomDrawerState.isOpen) {
                // 抽屉隐藏时执行的代码
                viewModel.closeDrawer()
            }
        }

        LaunchedEffect(isBottomDrawerVisible){
            Log.e("apex","isBottomDrawerVisible:  $isBottomDrawerVisible")
            if (isBottomDrawerVisible){
                scope.launch {
                    bottomDrawerState.open()
                }
            }else{
                scope.launch {
                    bottomDrawerState.close()
                }
            }
        }
    }

}