package io.agora.chatroom.viewmodel.marquee

import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.ViewModel

class MarqueeTextViewModel(
    val content:MutableList<String> = mutableListOf(),
    val duration:Long = 3000,          //结束停留时间
    val durationMillis:Int = 4500,    //动画持续时间
): ViewModel() {
    private val _marqueeTextList: MutableList<String> = content.toMutableStateList()
    val marqueeTextList: List<String> = _marqueeTextList

    fun addMarqueeText(content:String){
        _marqueeTextList.add(content)
    }

    fun removeMarqueeText(index:Int = 0){
        _marqueeTextList.removeAt(index)
    }
}