package io.agora.chatroom.ui

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import io.agora.chatroom.ui.binder.UIBinder
import io.agora.chatroom.ui.binder.UIComposeChatBottomBarBinder
import io.agora.chatroom.uikit.databinding.ActivityUiChatroomBinding

class UIChatRoomView : FrameLayout{
    private val mRoomViewBinding = ActivityUiChatroomBinding.inflate(LayoutInflater.from(context))
    private val mBinders = mutableListOf<UIBinder>()

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        addView(mRoomViewBinding.root)
    }

    fun bindService(){

        viewBinderConnected()
    }

    private fun viewBinderConnected(){
        val chatBottomBar = UIComposeChatBottomBarBinder(
            mRoomViewBinding.composeChatBottomBar
        )
        chatBottomBar.let {
            mBinders.add(it)
        }
    }

}