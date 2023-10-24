package io.agora.chatroom.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.unit.IntSize
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

open class ComposeBaseListViewModel<T>(
    private val contentList: List<T> = emptyList(),
): ViewModel(){
    private val _items: MutableList<T> = contentList.toMutableStateList()
    val items: List<T> = _items

    private val _isAutoClear = mutableStateOf(false)
    val isAutoClear: State<Boolean> get() = _isAutoClear

    private val _autoClearTime = mutableLongStateOf(3000)
    val autoClearTime: State<Long> get() = _autoClearTime

    open fun addData(data: T) {
        _items.add(data)
    }

    open fun addDateToIndex(index:Int = 0,data: T){
        _items.add(index,data)
    }

    open fun addDataList(msgList:List<T>){
        _items.addAll(msgList)
    }

    open fun addDataListToIndex(index:Int = 0,msgList:List<T>){
        _items.addAll(index,msgList)
    }

    open fun removeData(data: T){
        if (_items.contains(data)  ){
            _items.remove(data)
        }
    }

    open fun clear(){
        _items.clear()
    }

    fun setAutoClearTime(time:Long){
        _autoClearTime.longValue = time
    }

    fun openAutoClear(){
        _isAutoClear.value = true
    }

    fun closeAutoClear(){
        _isAutoClear.value = false
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