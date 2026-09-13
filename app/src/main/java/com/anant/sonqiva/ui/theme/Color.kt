package com.anant.sonqiva.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// Dark theme base surfaces
val BackgroundDark = Color(0xFF08080A)
val SurfaceDark = Color(0xFF13131B)
val SurfaceDim = Color(0xFF13131B)
val SurfaceBright = Color(0xFF393841)
val SurfaceContainerLowest = Color(0xFF0D0D15)
val SurfaceContainerLow = Color(0xFF1B1B23)
val SurfaceContainer = Color(0xFF1F1F27)
val SurfaceContainerHigh = Color(0xFF292932)
val SurfaceContainerHighest = Color(0xFF34343D)

// Text & Content Colors
val OnSurface = Color(0xFFE4E1ED)
val OnSurfaceVariant = Color(0xFF908FA0)
val OnBackground = Color(0xFFE4E1ED)
val OutlineColor = Color(0xFF464554)
val OutlineVariant = Color(0xFF2B2B36)

// Accent & Brand Colors
val PrimaryAccent = Color(0xFFC0C1FF)
val PrimaryContainer = Color(0xFF3B3D99)
val OnPrimary = Color(0xFF1000A9)
val OnPrimaryContainer = Color(0xFFE1E0FF)

val SecondaryAccent = Color(0xFFDDB7FF)
val SecondaryContainer = Color(0xFF5A1E8A)
val OnSecondary = Color(0xFF490080)
val OnSecondaryContainer = Color(0xFFF0DBFF)

val TertiaryAccent = Color(0xFFFFB783)
val TertiaryContainer = Color(0xFF703700)
val OnTertiary = Color(0xFF4F2500)
val OnTertiaryContainer = Color(0xFFFFDCC5)

val ErrorColor = Color(0xFFFFB4AB)
val ErrorContainer = Color(0xFF93000A)
val OnError = Color(0xFF690005)

// Glassmorphism Surfaces
val GlassBackground = Color(0x1AFFFFFF)
val GlassBackgroundLight = Color(0x2EFFFFFF)
val GlassBorder = Color(0x1FFFFFFF)
val GlassBorderActive = Color(0x40C0C1FF)

// Atmospheric Background Gradients
val AtmosphericGradientTop = Brush.verticalGradient(
    colors = listOf(
        Color(0x333F3D8A),
        Color(0x1A251B4D),
        Color(0x0008080A)
    )
)

val AtmosphericGradientHero = Brush.radialGradient(
    colors = listOf(
        Color(0x4D6366F1),
        Color(0x268083FF),
        Color(0x0008080A)
    ),
    radius = 900f
)

val PrimaryGradient = Brush.horizontalGradient(
    colors = listOf(
        Color(0xFF8083FF),
        Color(0xFFA855F7)
    )
)