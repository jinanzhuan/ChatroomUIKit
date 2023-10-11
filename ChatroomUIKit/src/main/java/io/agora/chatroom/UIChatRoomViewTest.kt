package io.agora.chatroom

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.lifecycle.viewmodel.compose.viewModel
import io.agora.CallBack
import io.agora.chat.ChatClient
import io.agora.chatroom.service.ChatMessage
import io.agora.chatroom.service.ChatroomChangeListener
import io.agora.chatroom.ui.UIChatroomService
import io.agora.chatroom.compose.chatbottombar.ComposeChatBottomBar
import io.agora.chatroom.compose.chatmessagelist.ComposeChatMessageList
import io.agora.chatroom.theme.ChatroomUIKitTheme
import io.agora.chatroom.theme.primaryColor8
import io.agora.chatroom.theme.secondaryColor8
import io.agora.chatroom.viewmodel.messages.MessageComposerViewModel
import io.agora.chatroom.viewmodel.messages.MessageListViewModel
import io.agora.chatroom.viewmodel.messages.MessagesViewModelFactory
import io.agora.chatroom.uikit.databinding.ActivityUiChatroomTestBinding

class UIChatRoomViewTest : FrameLayout, ChatroomChangeListener {
    private val mRoomViewBinding = ActivityUiChatroomTestBinding.inflate(LayoutInflater.from(context))
    private val inputField: MutableState<Boolean> = mutableStateOf(false)
    private lateinit var listViewModel:MessageListViewModel
    private lateinit var bottomBarViewModel:MessageComposerViewModel
    private lateinit var service:UIChatroomService

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        addView(mRoomViewBinding.root)
    }

    fun bindService(service: UIChatroomService){
        this.service = service
        val roomId = service.getRoomInfo().roomId

        service.getChatService().bindListener(this)

        if (!ChatroomUIKitClient.shared.isLoginBefore()){
            ChatClient.getInstance().login("apex1","1",object : CallBack {
                override fun onSuccess() {
                    Log.e("apex","login onSuccess")
                    joinChatroom(roomId)
                }

                override fun onError(code: Int, error: String?) {
                    Log.e("apex","login onError $code  $error")
                }

            })
        }else{
            joinChatroom(roomId)
        }

        mRoomViewBinding.composeChatroom.setContent {

            val factory = buildViewModelFactory(
                service = service
            )

            listViewModel = viewModel(MessageListViewModel::class.java, factory = factory)
            bottomBarViewModel = viewModel(MessageComposerViewModel::class.java, factory = factory)

            val isShowInput by inputField


            ChatroomUIKitTheme{
                ConstraintLayout(modifier = Modifier
                    .fillMaxSize()
                    .clickable {
                        inputField.value = false
                    }
                ) {
                    val (msgList, bottomBar) = createRefs()

                    ComposeChatMessageList(
                        viewModel = listViewModel,
                        modifier = Modifier
                            .constrainAs(msgList) {
                                bottom.linkTo(bottomBar.top)
                            }
                            .size(296.dp, 164.dp),
                        onLongItemClick = { index,message->
                            Log.e("apex","onLongItemClick $index $message")
                        }
                    )

                    ComposeChatBottomBar(
                        modifier = Modifier
                            .constrainAs(bottomBar) {
                                bottom.linkTo(parent.bottom)
                            }
                            .fillMaxWidth()
                            .wrapContentHeight(),
                        viewModel = bottomBarViewModel,
                        showInput = isShowInput,
                        onSendMessage = { input->
                            Log.e("apex","onSendMessage")
                            service.getChatService().sendTextMessage(
                                message = input,
                                roomId = roomId,//service.getRoomInfo().roomId
                                onSuccess = {
                                    listViewModel.addTextMessage(it)
                                },
                                onError = {code, error ->

                                })
                        },
                        onMenuClick = {
                            Log.e("apex","onMenuClick:  $it")
                        },
                        onInputClick = {
                            Log.e("apex","onInputClick: ")
                            inputField.value = true
                        }
                    )
                }
            }
        }
    }

    private fun buildViewModelFactory(
        service: UIChatroomService,
        showDateSeparators: Boolean = true,
        showLabel: Boolean = true,
        showGift: Boolean = true,
        showAvatar: Boolean = true,
        dateSeparatorColor: Color = secondaryColor8,
        nickNameColor: Color = primaryColor8,
    ): MessagesViewModelFactory {
        return MessagesViewModelFactory(
            service = service,
            showDateSeparators = showDateSeparators,
            showLabel = showLabel,
            showGift = showGift,
            showAvatar = showAvatar,
            dateSeparatorColor = dateSeparatorColor,
            nickNameColor = nickNameColor
        )
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()

        service.getChatService().unbindListener(this)
    }

    override fun onMessageReceived(message: ChatMessage) {
        super.onMessageReceived(message)
        listViewModel.addTextMessage(message)
    }


    fun joinChatroom(roomId:String){
        service.getChatService().joinChatroom(roomId,"apex1"
            , onSuccess = {
                Log.e("apex","joinChatroom  193314355740675 onSuccess")
            }
            , onError = {errorCode,result->
                Log.e("apex","joinChatroom  193314355740675 onError $errorCode $result")
            }
        )
    }

}