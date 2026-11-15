package com.rivan.csc.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val PrimaryGreen = Color(0xFF087F45)
val PrimaryDarkGreen = Color(0xFF063B25)
val PrimaryLightGreen = Color(0xFFE6F4ED)
val BgLight = Color(0xFFF2F5F3)
val CardLight = Color(0xFFFFFFFF)
val BgDark = Color(0xFF0C120F)
val CardDark = Color(0xFF161F1B)
val DangerRed = Color(0xFFD94A4A)
val WarningOrange = Color(0xFFD88913)
val InfoBlue = Color(0xFF2563A6)

private val LightColorScheme = lightColorScheme(
    primary = PrimaryGreen,
    onPrimary = Color.White,
    primaryContainer = PrimaryLightGreen,
    onPrimaryContainer = PrimaryDarkGreen,
    secondary = PrimaryDarkGreen,
    background = BgLight,
    surface = CardLight,
    onBackground = Color(0xFF14211B),
    onSurface = Color(0xFF14211B),
    error = DangerRed
)

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF18A765),
    onPrimary = Color.Black,
    primaryContainer = Color(0xFF1B3026),
    onPrimaryContainer = Color(0xFFD9EEE3),
    secondary = Color(0xFF8AB8A2),
    background = BgDark,
    surface = CardDark,
    onBackground = Color(0xFFEAF2EE),
    onSurface = Color(0xFFEAF2EE),
    error = Color(0xFFFFB4B4)
)

@Composable
fun RivanCscTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography(),
        content = content
    )
}
