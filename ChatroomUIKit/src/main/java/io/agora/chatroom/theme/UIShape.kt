package io.agora.chatroom.theme

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ShapeDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp


val ExtraSmallCorner = RoundedCornerShape(4.dp)
val SmallCorner = RoundedCornerShape(8.dp)
val MediumCorner = RoundedCornerShape(12.dp)
val LargeCorner = RoundedCornerShape(16.dp)

val TopEnd_SendBubbleCorner_ExtraSmall = RoundedCornerShape(topEnd = 4.dp)
val BottomEnd_SendBubbleCorner_ExtraSmall = RoundedCornerShape(bottomEnd = 4.dp)

val TopEnd_SendBubbleCorner_Small = RoundedCornerShape(topEnd = 4.dp,topStart = 8.dp, bottomEnd = 8.dp, bottomStart = 8.dp)
val BottomEnd_SendBubbleCorner_Small = RoundedCornerShape(bottomEnd = 4.dp,topStart = 8.dp, topEnd = 8.dp, bottomStart = 8.dp)

val TopEnd_SendBubbleCorner_Medium = RoundedCornerShape(topEnd = 4.dp,topStart = 12.dp, bottomEnd = 12.dp, bottomStart = 12.dp)
val BottomEnd_SendBubbleCorner_Medium = RoundedCornerShape(bottomEnd = 4.dp,topStart = 12.dp, topEnd = 12.dp, bottomStart = 12.dp)

val TopEnd_SendBubbleCorner_Large = RoundedCornerShape(topEnd = 4.dp,topStart = 16.dp, bottomEnd = 16.dp, bottomStart = 16.dp)
val BottomEnd_SendBubbleCorner_Large = RoundedCornerShape(bottomEnd = 4.dp,topStart = 16.dp, topEnd = 16.dp, bottomStart = 16.dp)

val TopStart_RecBubbleCorner_ExtraSmall = RoundedCornerShape(topStart = 4.dp)
val BottomStart_RecBubbleCorner_ExtraSmall = RoundedCornerShape(bottomStart = 4.dp)

val TopStart_RecBubbleCorner_Small = RoundedCornerShape(topStart = 4.dp,topEnd = 8.dp,bottomEnd = 8.dp, bottomStart = 8.dp)
val BottomStart_RecBubbleCorner_Small = RoundedCornerShape(bottomStart = 4.dp,bottomEnd = 8.dp,topStart = 8.dp, topEnd = 8.dp)

val TopStart_RecBubbleCorner_Medium = RoundedCornerShape(topStart = 4.dp,topEnd = 12.dp,bottomEnd = 12.dp, bottomStart = 12.dp)
val BottomStart_RecBubbleCorner_Medium = RoundedCornerShape(bottomStart = 4.dp,bottomEnd = 12.dp,topStart = 12.dp, topEnd = 12.dp)

val TopStart_RecBubbleCorner_Large = RoundedCornerShape(topStart = 4.dp,topEnd = 16.dp,bottomEnd = 16.dp, bottomStart = 16.dp)
val BottomStart_RecBubbleCorner_Large = RoundedCornerShape(bottomStart = 4.dp,bottomEnd = 16.dp,topStart = 16.dp, topEnd = 16.dp)

@Immutable
data class UIShapes(
    val avatar: Shape,
    val sendMessageBubble: Shape,
    val receiveMessageBubble: Shape,
    val inputField: Shape,
    val attachment: Shape,
    val imageThumbnail: Shape,
    val bottomSheet: Shape,
    val header: Shape,
    val extraSmall: Shape = ExtraSmallCorner,
    val small: Shape = SmallCorner,
    val medium: Shape = MediumCorner,
    val large: Shape = LargeCorner,
    val extraLarge: Shape = ShapeDefaults.ExtraLarge,
) {
    companion object {
        @Composable
        fun defaultShapes(): UIShapes = UIShapes(
            avatar = CircleShape,
            sendMessageBubble = RoundedCornerShape(16.dp),
            receiveMessageBubble = RoundedCornerShape(16.dp),
            inputField = RoundedCornerShape(24.dp),
            attachment = RoundedCornerShape(16.dp),
            imageThumbnail = RoundedCornerShape(8.dp),
            bottomSheet = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
            header = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
            extraSmall = ExtraSmallCorner,
            small  = SmallCorner,
            medium  = MediumCorner,
            large  = LargeCorner,
            extraLarge  = ShapeDefaults.ExtraLarge,
        )
    }

}