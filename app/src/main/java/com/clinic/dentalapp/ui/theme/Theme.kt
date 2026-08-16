package com.clinic.dentalapp.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val Teal = Color(0xFF00897B)
val TealDark = Color(0xFF00695C)
val TealLight = Color(0xFFB2DFDB)
val Background = Color(0xFFF5F7F8)

private val LightColors = lightColorScheme(
    primary = Teal,
    onPrimary = Color.White,
    primaryContainer = TealLight,
    secondary = TealDark,
    background = Background,
    surface = Color.White
)

@Composable
fun DentalClinicTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LightColors,
        content = content
    )
}
