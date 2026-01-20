package com.smartdriveassist.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColorScheme = lightColorScheme(
    primary        = DarkGreen800,
    onPrimary      = BackgroundCard,
    primaryContainer = LightGreen100,
    onPrimaryContainer = DarkGreen900,
    secondary      = DarkGreen600,
    onSecondary    = BackgroundCard,
    background     = BackgroundPage,
    onBackground   = TextPrimary,
    surface        = BackgroundCard,
    onSurface      = TextPrimary,
    outline        = BorderLight
)

@Composable
fun SmartDriveAssistTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography  = Typography,
        content     = content
    )
}
