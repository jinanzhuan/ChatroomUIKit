package io.agora.chatroom.viewmodel

import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.unit.IntSize
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

open class ComposeBaseListViewModel<T>(
    private var contentList: List<T> = emptyList()
): ViewModel(){
    private val _items: MutableList<T> = contentList.toMutableStateList()
    val items: List<T> = _items

    fun addData(msg: T) {
        _items.add(msg)
    }

    fun addDateToIndex(index:Int = 0,msg: T){
        _items.add(index,msg)
    }

    fun addDataList(msgList:List<T>){
        _items.addAll(msgList)
    }

    fun addDataListToIndex(index:Int = 0,msgList:List<T>){
        _items.addAll(index,msgList)
    }

    fun removeData(msg: T){
        if (_items.contains(msg)  ){
            _items.remove(msg)
        }
    }

    fun clear(){
        _items.clear()
    }

    /**
     * Notifies the UI of the calculated message offset to center it on the screen.
     */
    private val _focusedMessageOffset: MutableStateFlow<Int?> = MutableStateFlow(null)
    val focusedMessageOffset: StateFlow<Int?> = _focusedMessageOffset

    /**
     * Calculates the message offset needed for the message to center inside the list on scroll.
     *
     * @param parentSize The size of the list which contains the message.
     * @param focusedMessageSize The size of the message item we wish to bring to the center and focus.
     */
    public fun calculateMessageOffset(parentSize: IntSize, focusedMessageSize: IntSize) {
        val sizeDiff = parentSize.height - focusedMessageSize.height
        if (sizeDiff > 0) {
            _focusedMessageOffset.value = -sizeDiff / 2
        } else {
            _focusedMessageOffset.value = -sizeDiff
        }
    }
}