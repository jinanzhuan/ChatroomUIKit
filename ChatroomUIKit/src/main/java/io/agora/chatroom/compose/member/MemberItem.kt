package io.agora.chatroom.compose.member

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.agora.chatroom.compose.avatar.Avatar
import io.agora.chatroom.service.ROLE
import io.agora.chatroom.service.UserEntity
import io.agora.chatroom.theme.ChatroomUIKitTheme
import io.agora.chatroom.uikit.R

@Composable
fun MemberItem(
    member: UserEntity,
    modifier: Modifier,
    labelContent: @Composable ((UserEntity) -> Unit)? = null,
    avatarContent: @Composable (UserEntity) -> Unit = {},
    nameContent: @Composable (UserEntity) -> Unit = {},
    extendContent: @Composable ((UserEntity) -> Unit)? = null,
    showDivider: Boolean = true,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        labelContent?.let {
            labelContent(member)
        }

        avatarContent(member)

        Column(
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Bottom,
            modifier = Modifier
                .fillMaxSize()) {

            Row(modifier = Modifier
                .weight(1f)
                .fillMaxWidth()) {
                Box(modifier = Modifier.weight(1f)) {
                    nameContent(member)
                }

                extendContent?.let {
                    extendContent(member)
                }
            }

            if (showDivider) {
                Divider(thickness = 1.dp, modifier = Modifier
                    .fillMaxWidth()
                    .background(ChatroomUIKitTheme.colors.outlineVariant))
            }
        }

    }
}

@Composable
fun DefaultMemberItem(
    user: UserEntity,
    labelContent: @Composable ((UserEntity) -> Unit)? = { user ->
        user.identify?.let {
            if (it.isNotBlank()) {
                Spacer(modifier = Modifier.width(12.dp))
                Avatar(
                    imageUrl = it,
                    modifier = Modifier
                        .size(26.dp),
                    shape = RoundedCornerShape(0.dp),
                    placeholderPainter = painterResource(id = R.drawable.icon_default_label)
                )
            }
        }
    },
    avatarContent: @Composable (UserEntity) -> Unit = { user ->
        Spacer(modifier = Modifier.width(12.dp))
        Avatar(
            imageUrl = user.avatar ?: "",
            shape = RoundedCornerShape(40.dp),
            modifier = Modifier
                .size(40.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
    },
    showRole: Boolean = false,
    nameContent: @Composable (UserEntity) -> Unit = {user ->
        Column(
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxHeight()
        ) {
            Text(
                text = user.nickname?.let {
                    it.ifBlank { user.userId }
                } ?: user.userId ,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(20.dp),
                style = ChatroomUIKitTheme.typography.titleMedium,
                color = ChatroomUIKitTheme.colors.onBackground
            )
            if (showRole && (user.role == ROLE.OWNER || user.role == ROLE.ADMIN)) {
                Text(
                    text = stringResource(id = user.role.let {
                        if (it == ROLE.OWNER) R.string.role_owner else R.string.role_admin }
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(18.dp),
                    style = ChatroomUIKitTheme.typography.bodyMedium,
                    color = ChatroomUIKitTheme.colors.onBackground
                )
            }
        }
    },
    onItemClick: ((UserEntity) -> Unit)? = null,
    onExtendClick: ((UserEntity) -> Unit)? = null,
    extendContent: @Composable ((UserEntity) -> Unit)? = {user ->
        if (user.role == ROLE.ADMIN || user.role == ROLE.OWNER) {
            Surface(onClick = { onExtendClick?.invoke(user) }) {
                Icon(
                    painter = painterResource(id = R.drawable.icon_bottom_bar_more),
                    modifier = Modifier.fillMaxHeight(),
                    contentDescription = null)
            }

            Spacer(modifier = Modifier.width(12.dp))
        }
    },
    showDivider: Boolean = true
) {
    MemberItem(
        user,
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp)
            .background(ChatroomUIKitTheme.colors.background)
            .clickable { onItemClick?.invoke(user) },
        labelContent,
        avatarContent,
        nameContent,
        extendContent,
        showDivider = showDivider
    )
}

@Composable
fun DefaultMuteListItem(
    user: UserEntity,
    onItemClick: ((UserEntity) -> Unit)? = null,
    onExtendClick: ((UserEntity) -> Unit)? = null,
) {
    DefaultMemberItem(
        user,
        onItemClick = onItemClick,
        onExtendClick = onExtendClick,
        labelContent = null,
    )
}

@Preview(showBackground = true)
@Composable
fun MemberItemPreview() {
    ChatroomUIKitTheme {
        DefaultMemberItem(user = UserEntity(
            userId = "123",
            nickname = "nickname",
            avatar = "",
            identify = ""
        ))
    }
}
