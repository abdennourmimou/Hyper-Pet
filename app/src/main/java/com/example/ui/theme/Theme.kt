package com.example.ui.theme

import android.os.Build
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

enum class PetGender {
    NONE, MALE, FEMALE
}

@Composable
fun MyApplicationTheme(
    gender: PetGender = PetGender.NONE,
    content: @Composable () -> Unit,
) {
    val targetBackgroundColor = when (gender) {
        PetGender.NONE -> SoftLightGray
        PetGender.MALE -> PastelBlue
        PetGender.FEMALE -> PastelPink
    }
    
    val targetPrimaryColor = CharcoalGray
    val targetSurfaceColor = CharcoalDark

    val animatedBackgroundColor by animateColorAsState(
        targetValue = targetBackgroundColor,
        animationSpec = tween(500),
        label = "Background Color Animation"
    )

    val colorScheme = lightColorScheme(
        background = animatedBackgroundColor,
        primary = targetPrimaryColor,
        secondary = targetSurfaceColor,
        surface = animatedBackgroundColor,
        onBackground = Color.Black,
        onSurface = Color.Black,
        onPrimary = Color.White
    )

    MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}
