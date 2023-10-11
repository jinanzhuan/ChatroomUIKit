package io.agora.chatroom.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LocalDimens = compositionLocalOf<UIDimens> {
    error("No dimens provided! Make sure to wrap all usages of Stream components in a ChatTheme.")
}


private val mDarkColorScheme = darkColors(
    primary = primaryColor6,
    primaryVariant = primaryColor2,
    secondary = secondaryColor6,
    onError = errorColor6,
    background = neutralColor1,
    onBackground = neutralColor98,
)

private val mLightColorScheme = lightColors(
    primary = primaryColor5,
    primaryVariant = primaryColor95,
    secondary = secondaryColor4,
    onError = errorColor5,
    background = neutralColor98,
    onBackground = neutralColor1,
)

@Composable
public fun ChatroomUIKitTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
//    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
//        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
//            val context = LocalContext.current
//            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
//        }
        darkTheme -> mDarkColorScheme
        else -> mLightColorScheme
    }
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = darkTheme
        }
    }

    MaterialTheme(
        colors = colorScheme,
        typography = Typography,
        content = content
    )
}

public object ChatroomUIKitTheme {

    /**
     * Retrieves the current [UIDimens] at the call site's position in the hierarchy.
     */
    public val dimens: UIDimens
        @Composable
        @ReadOnlyComposable
        get() = LocalDimens.current
}