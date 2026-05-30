package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val DynamicKidSpaceScheme = darkColorScheme(
    primary = StarGold,
    secondary = CoralBlue,
    background = OceanBg,
    surface = CardBg,
    onPrimary = OceanBg,
    onSecondary = OceanBg,
    onBackground = TextWhite,
    onSurface = TextWhite
)

@Composable
fun MyApplicationTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DynamicKidSpaceScheme,
        typography = Typography,
        content = content
    )
}

