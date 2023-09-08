package io.agora.chatroom.ui.theme


import androidx.compose.material.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

val Typography by lazy {
    Typography(
        h1 = AlphabetHeadlineLarge,
        subtitle1 = AlphabetTitleLarge,
        subtitle2 = AlphabetTitleSmall,
        body1 = AlphabetBodyLarge,
        body2 = AlphabetBodySmall,
    )
}

val AlphabetHeadlineLarge = TextStyle(
    fontFamily = FontFamily.Default,
    fontWeight = FontWeight.SemiBold,
    lineHeight = 28.sp,
    fontSize = 20.sp,
    color = Color_171A1C,
    letterSpacing = 0.08.sp,
)

val AlphabetHeadlineMedium = TextStyle(
    fontFamily = FontFamily.Default,
    fontWeight = FontWeight.SemiBold,
    lineHeight = 24.sp,
    fontSize = 18.sp,
    color = Color_171A1C,
    letterSpacing = 0.06.sp,
)

val AlphabetHeadlineSmall = TextStyle(
    fontFamily = FontFamily.Default,
    fontWeight = FontWeight.SemiBold,
    lineHeight = 22.sp,
    fontSize = 16.sp,
    color = Color_171A1C,
    letterSpacing = 0.03.sp,
)

val AlphabetTitleLarge = TextStyle(
    fontFamily = FontFamily.Default,
    fontWeight = FontWeight.Normal,
    lineHeight = 24.sp,
    fontSize = 18.sp,
    color = Color_171A1C,
    letterSpacing = 0.06.sp,
)
val AlphabetTitleMedium = TextStyle(
    fontFamily = FontFamily.Default,
    fontWeight = FontWeight.Normal,
    lineHeight = 22.sp,
    fontSize = 16.sp,
    color = Color_171A1C,
    letterSpacing = 0.03.sp,
)
val AlphabetTitleSmall = TextStyle(
    fontFamily = FontFamily.Default,
    fontWeight = FontWeight.Normal,
    lineHeight = 20.sp,
    fontSize = 14.sp,
    color = Color_171A1C,
    letterSpacing = 0.01.sp,
)
val AlphabetLabelLarge = TextStyle(
    fontFamily = FontFamily.Default,
    fontWeight = FontWeight.Normal,
    lineHeight = 22.sp,
    fontSize = 16.sp,
    color = Color_171A1C,
    letterSpacing = 0.03.sp,
)
val AlphabetLabelMedium = TextStyle(
    fontFamily = FontFamily.Default,
    fontWeight = FontWeight.Normal,
    lineHeight = 18.sp,
    fontSize = 14.sp,
    color = Color_171A1C,
    letterSpacing = 0.01.sp,
)
val AlphabetLabelSmall = TextStyle(
    fontFamily = FontFamily.Default,
    fontWeight = FontWeight.Normal,
    lineHeight = 16.sp,
    fontSize = 12.sp,
    color = Color_171A1C,
    letterSpacing = 0.01.sp,
)
val AlphabetLabelExtraSmall = TextStyle(
    fontFamily = FontFamily.Default,
    fontWeight = FontWeight.Normal,
    lineHeight = 14.sp,
    fontSize = 11.sp,
    color = Color_171A1C,
)
val AlphabetBodyLarge  = TextStyle(
    fontFamily = FontFamily.Default,
    fontWeight = FontWeight.Normal,
    lineHeight = 22.sp,
    fontSize = 16.sp,
    color = Color_171A1C,
    letterSpacing = 0.03.sp,
)
val AlphabetBodyMedium = TextStyle(
    fontFamily = FontFamily.Default,
    fontWeight = FontWeight.Normal,
    lineHeight = 18.sp,
    fontSize = 14.sp,
    color = Color_171A1C,
    letterSpacing = 0.01.sp,
)
val AlphabetBodySmall = TextStyle(
    fontFamily = FontFamily.Default,
    fontWeight = FontWeight.SemiBold,
    lineHeight = 16.sp,
    fontSize = 12.sp,
    color = Color_171A1C,
)
val AlphabetBodyExtraSmall = TextStyle(
    fontFamily = FontFamily.Default,
    fontWeight = FontWeight.SemiBold,
    lineHeight = 14.sp,
    fontSize = 11.sp,
    color = Color_171A1C,
)