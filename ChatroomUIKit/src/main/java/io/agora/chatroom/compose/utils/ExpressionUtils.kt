package io.agora.chatroom.compose.utils

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.buildAnnotatedString
import io.agora.chatroom.data.emojiList


class ExpressionUtils {
    companion object{
        const val ee_1 :String = "U+1F600"
        const val ee_2 :String = "U+1F604"
        const val ee_3 :String = "U+1F609"
        const val ee_4 :String = "U+1F62E"
        const val ee_5 :String = "U+1F92A"
        const val ee_6 :String = "U+1F60E"
        const val ee_7 :String = "U+1F971"
        const val ee_8 :String = "U+1F974"
        const val ee_9 :String = "U+263A"
        const val ee_10 :String = "U+1F641"
        const val ee_11 :String = "U+1F62D"
        const val ee_12 :String = "U+1F610"
        const val ee_13 :String = "U+1F607"
        const val ee_14 :String = "U+1F62C"
        const val ee_15 :String = "U+1F913"
        const val ee_16 :String = "U+1F633"
        const val ee_17 :String = "U+1F973"
        const val ee_18 :String = "U+1F620"
        const val ee_19 :String = "U+1F644"
        const val ee_20 :String = "U+1F910"
        const val ee_21 :String = "U+1F97A"
        const val ee_22 :String = "U+1F928"
        const val ee_23 :String = "U+1F62B"
        const val ee_24 :String = "U+1F637"
        const val ee_25 :String = "U+1F912"
        const val ee_26 :String = "U+1F631"
        const val ee_27 :String = "U+1F618"
        const val ee_28 :String = "U+1F60D"
        const val ee_29 :String = "U+1F922"
        const val ee_30 :String = "U+1F47F"
        const val ee_31 :String = "U+1F92C"
        const val ee_32 :String = "U+1F621"
        const val ee_33 :String = "U+1F44D"
        const val ee_34 :String = "U+1F44E"
        const val ee_35 :String = "U+1F44F"
        const val ee_36 :String = "U+1F64C"
        const val ee_37 :String = "U+1F91D"
        const val ee_38 :String = "U+1F64F"
        const val ee_39 :String = "U+2764"
        const val ee_40 :String = "U+1F494"
        const val ee_41 :String = "U+1F495"
        const val ee_42 :String = "U+1F4A9"
        const val ee_43 :String = "U+1F48B"
        const val ee_44 :String = "U+2600"
        const val ee_45 :String = "U+1F31C"
        const val ee_46 :String = "U+1F308"
        const val ee_47 :String = "U+2B50"
        const val ee_48 :String = "U+1F31F"
        const val ee_49 :String = "U+1F389"
        const val ee_50 :String = "U+1F490"
        const val ee_51 :String = "U+1F382"
        const val ee_52 :String = "U+1F381"
    }

    fun containsKey(key:String):Boolean{
        var isContains = false
        emojiList.forEach {
            if (it.emojiText.contains(key, false)) {//false 不忽略大小写
                isContains = true
            }
        }
        return isContains
    }

    fun getSmiledText(text:CharSequence): AnnotatedString {
        if (!containsKey(text.toString())){
            return buildAnnotatedString {
                append(text)
            }
        }

        var result = text

        val annotatedText = buildAnnotatedString {

            emojiList.forEach {
                if (result.contains(it.emojiText)) {

                }
            }

        }
        return annotatedText
    }

    fun String.replaceWithImages(): CharSequence {
        var result = this
        emojiList.forEach {
            if (result.contains(it.emojiText)) {
                result = result.replace(it.emojiText, "<img src=\"${it.icon}\"/>")
            }
        }
        return result
    }
}