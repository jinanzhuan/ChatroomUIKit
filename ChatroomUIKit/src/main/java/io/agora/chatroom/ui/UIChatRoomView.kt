package io.agora.chatroom.ui

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.compose.ui.graphics.Color
import io.agora.chatroom.binder.UIChatListBinder
import io.agora.chatroom.binder.UIChatRoomBinder
import io.agora.chatroom.theme.primaryColor80
import io.agora.chatroom.theme.secondaryColor80
import io.agora.chatroom.viewmodel.messages.MessagesViewModelFactory
import io.agora.chatroom.uikit.databinding.ActivityUiChatroomBinding

class UIChatRoomView : FrameLayout{
    private val mRoomViewBinding = ActivityUiChatroomBinding.inflate(LayoutInflater.from(context))
    private val mBinders = mutableListOf<io.agora.chatroom.binder.UIBinder>()

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

        viewBinderConnected(service)
    }

    private fun viewBinderConnected(service: UIChatroomService){

        val factory = buildViewModelFactory(
            service = service
        )

        val chatBottomBar = io.agora.chatroom.binder.UIChatBottomBarBinder(
            baseLayout = mRoomViewBinding.baseLayout,
            chatBottomBar = mRoomViewBinding.composeChatBottomBar,
            service = service,
            factory = factory
        )
        chatBottomBar.let {
            it.bind()
            mBinders.add(it)
        }

        val chatList = UIChatListBinder(
            chatList = mRoomViewBinding.composeChatList,
            service = service,
            factory = factory,
        )
        chatList.let {
            it.bind()
            mBinders.add(it)
        }

        val chatroom = UIChatRoomBinder(
            chatroom = mRoomViewBinding.composeChatroom,
            service = service
        )
        chatroom.let {
            it.bind()
            mBinders.add(it)
        }

    }

    private fun buildViewModelFactory(
        service: UIChatroomService,
        showDateSeparators: Boolean = true,
        showLabel: Boolean = true,
        showGift: Boolean = true,
        showAvatar: Boolean = true,
        dateSeparatorColor: Color = secondaryColor80,
        nickNameColor: Color = primaryColor80,
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

        mBinders.forEach {
            it.unBind()
        }
    }



}