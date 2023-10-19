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
    val sendGift:Shape,
    val giftItemBg:Shape,
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
            sendGift = RoundedCornerShape(bottomStart = 8.dp, bottomEnd = 8.dp),
            giftItemBg = RoundedCornerShape(22.dp),
        )
    }

}