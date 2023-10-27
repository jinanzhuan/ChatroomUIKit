package io.agora.chatroom.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.ViewModel

sealed class RequestState {
    object Idle : RequestState()
    object Loading : RequestState()
    object LoadingMore : RequestState()
    data class Success<T>(val data: List<T> = emptyList()) : RequestState()

    data class SuccessMore<T>(val data: List<T>) : RequestState()

    object Refresh: RequestState()
    data class Error(val errorCode: Int, val message: String?) : RequestState()
}


open class ListViewModel<T>(
   private val data: List<T> = emptyList(),
): ViewModel() {

    private val _data = data.toMutableStateList()

    /**
     * Add data to the list.
     */
    fun addData(data: T): List<T>{
        _data.add(data)
        return _data
    }

    /**
     * Add data to the list.
     */
    fun addData(data: List<T>): List<T>{
        _data.addAll(data)
        return _data
    }

    /**
     * Remove data from the list.
     */
    fun removeData(data: T): List<T> {
        if (_data.contains(data)){
            _data.remove(data)
        }
        return _data
    }

    /**
     * Clear all data from the list.
     */
    fun clearData(){
        _data.clear()
    }

    /**
     * Set data to the list.
     */
    fun setData(data: List<T>): List<T>{
        _data.clear()
        _data.addAll(data)
        return _data
    }

    val getData: List<T>
        get() = _data

}

open class RequestListViewModel<T>(
   private val state: RequestState = RequestState.Idle,
): ComposeBaseListViewModel<T>() {
    init {
        if (state is RequestState.Success<*>){
            addDataList(state.data as List<T>)
        }
    }
    private val _state = mutableStateOf(state)
    val getState: RequestState
        get() = _state.value

    fun add(data: List<T>){
        addDataList(data)
        _state.value = RequestState.Success(items)
    }

    fun addMore(list: List<T>){
        addDataList(list)
        _state.value = RequestState.SuccessMore(items)
    }

    fun error(code: Int, message: String?) {
        _state.value = RequestState.Error(code, message)
    }

    fun loading(){
        _state.value = RequestState.Loading
    }

    fun loadMore() {
        _state.value = RequestState.LoadingMore
    }

    fun refresh(){
        _state.value = RequestState.Refresh
    }
}


