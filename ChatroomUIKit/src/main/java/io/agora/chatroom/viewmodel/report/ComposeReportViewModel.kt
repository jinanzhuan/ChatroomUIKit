package io.agora.chatroom.viewmodel.report

import androidx.compose.runtime.mutableStateOf
import io.agora.chatroom.viewmodel.menu.BottomSheetViewModel

class ComposeReportViewModel(
    private val reportTag:List<String>,
): BottomSheetViewModel<String>(contentList = reportTag) {

    private val _msgId = mutableStateOf("")
    val reportMsgId = _msgId

    fun setReportMsgId(msgId:String){
        _msgId.value = msgId
    }

}