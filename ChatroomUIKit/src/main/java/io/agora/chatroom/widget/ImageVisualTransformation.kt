package io.agora.chatroom.widget

import android.content.Context
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import io.agora.chatroom.compose.utils.ExpressionUtils.getEmojiRegex
import io.agora.chatroom.data.emojiMap
import java.util.regex.Matcher
import java.util.regex.Pattern

class ImageVisualTransformation(private val context: Context) : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val builder = AnnotatedString.Builder()

        val pattern: Pattern = Pattern.compile(getEmojiRegex())
        val matcher: Matcher = pattern.matcher(text.text)

        // 筛选匹配到的字符
        while (matcher.find()) {
            val matchedString = matcher.group()
            if (matchedString.isNotEmpty() && matchedString.isNotBlank()){
                val icon = emojiMap[matchedString]
                val startIndex = matcher.start()
                val endIndex = matcher.end()
            }
        }
        return TransformedText(builder.toAnnotatedString(), OffsetMapping.Identity)
    }
}