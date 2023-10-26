package io.agora.chatroom.viewmodel.dialog

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

/**
 * ViewModel for [SimpleDialog]
 */
class DialogViewModel(
    var title: String = "",
    var icon: Int = 0,
    var text: String = "",
    var showCancel: Boolean = false,
): ViewModel() {
    private val _showDialog = mutableStateOf(false)

    val isShowDialog: Boolean
        get() = _showDialog.value

    fun showDialog() {
        _showDialog.value = true
    }

    fun dismissDialog() {
        _showDialog.value = false
    }
}