package io.agora.chatroom.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp


val HeadlineLarge = TextStyle(
    fontFamily = FontFamily.Default,
    fontWeight = FontWeight.SemiBold,
    lineHeight = 28.sp,
    fontSize = 20.sp,
    color = Color_171A1C,
    letterSpacing = 0.08.sp,
)

val HeadlineMedium = TextStyle(
    fontFamily = FontFamily.Default,
    fontWeight = FontWeight.SemiBold,
    lineHeight = 24.sp,
    fontSize = 18.sp,
    color = Color_171A1C,
    letterSpacing = 0.06.sp,
)

val HeadlineSmall = TextStyle(
    fontFamily = FontFamily.Default,
    fontWeight = FontWeight.SemiBold,
    lineHeight = 22.sp,
    fontSize = 16.sp,
    color = Color_171A1C,
    letterSpacing = 0.03.sp,
)

val TitleLarge = TextStyle(
    fontFamily = FontFamily.Default,
    fontWeight = FontWeight.Normal,
    lineHeight = 24.sp,
    fontSize = 18.sp,
    color = Color_171A1C,
    letterSpacing = 0.06.sp,
)
val TitleMedium = TextStyle(
    fontFamily = FontFamily.Default,
    fontWeight = FontWeight.Normal,
    lineHeight = 22.sp,
    fontSize = 16.sp,
    color = Color_171A1C,
    letterSpacing = 0.03.sp,
)
val TitleSmall = TextStyle(
    fontFamily = FontFamily.Default,
    fontWeight = FontWeight.Normal,
    lineHeight = 20.sp,
    fontSize = 14.sp,
    color = Color_171A1C,
    letterSpacing = 0.01.sp,
)
val LabelLarge = TextStyle(
    fontFamily = FontFamily.Default,
    fontWeight = FontWeight.Normal,
    lineHeight = 22.sp,
    fontSize = 16.sp,
    color = Color_171A1C,
    letterSpacing = 0.03.sp,
)
val LabelMedium = TextStyle(
    fontFamily = FontFamily.Default,
    fontWeight = FontWeight.Normal,
    lineHeight = 18.sp,
    fontSize = 14.sp,
    color = Color_171A1C,
    letterSpacing = 0.01.sp,
)
val LabelSmall = TextStyle(
    fontFamily = FontFamily.Default,
    fontWeight = FontWeight.Normal,
    lineHeight = 16.sp,
    fontSize = 12.sp,
    color = Color_171A1C,
    letterSpacing = 0.01.sp,
)
val LabelExtraSmall = TextStyle(
    fontFamily = FontFamily.Default,
    fontWeight = FontWeight.Normal,
    lineHeight = 14.sp,
    fontSize = 11.sp,
    color = Color_171A1C,
)
val BodyLarge  = TextStyle(
    fontFamily = FontFamily.Default,
    fontWeight = FontWeight.Normal,
    lineHeight = 22.sp,
    fontSize = 16.sp,
    color = Color_171A1C,
    letterSpacing = 0.03.sp,
)
val BodyMedium = TextStyle(
    fontFamily = FontFamily.Default,
    fontWeight = FontWeight.Normal,
    lineHeight = 18.sp,
    fontSize = 14.sp,
    color = Color_171A1C,
    letterSpacing = 0.01.sp,
)
val BodySmall = TextStyle(
    fontFamily = FontFamily.Default,
    fontWeight = FontWeight.SemiBold,
    lineHeight = 16.sp,
    fontSize = 12.sp,
    color = Color_171A1C,
)
val BodyExtraSmall = TextStyle(
    fontFamily = FontFamily.Default,
    fontWeight = FontWeight.SemiBold,
    lineHeight = 14.sp,
    fontSize = 11.sp,
    color = Color_171A1C,
)
@Immutable
data class UITypography(
    val headlineLarge: TextStyle ,
    val headlineMedium: TextStyle ,
    val headlineSmall: TextStyle ,
    val titleLarge: TextStyle ,
    val titleMedium: TextStyle ,
    val titleSmall: TextStyle ,
    val bodyLarge: TextStyle ,
    val bodyMedium: TextStyle ,
    val bodySmall: TextStyle ,
    val labelLarge: TextStyle ,
    val labelMedium: TextStyle ,
    val labelSmall: TextStyle ,
) {
    companion object {
        @Composable
        fun defaultTypography(): UITypography = UITypography(
            headlineLarge = HeadlineLarge,
            headlineMedium = HeadlineMedium,
            headlineSmall = HeadlineSmall,
            titleLarge = TitleLarge,
            titleMedium = TitleMedium,
            titleSmall = TitleSmall,
            bodyLarge = BodyLarge,
            bodyMedium = BodyMedium,
            bodySmall = BodySmall,
            labelLarge = LabelLarge,
            labelMedium = LabelMedium,
            labelSmall = LabelSmall)
    }
}