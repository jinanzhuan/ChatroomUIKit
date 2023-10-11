package io.agora.chatroom.ui.compose.utils

import android.content.res.Resources

object DisplayUtils {
    val metrics = Resources.getSystem().displayMetrics
    val density = metrics.density

    fun pxToDp(px: Int): Float {
        val density = Resources.getSystem().displayMetrics.density
        return px / density
    }
}