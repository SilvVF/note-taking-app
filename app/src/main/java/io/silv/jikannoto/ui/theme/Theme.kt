package io.silv.jikannoto.ui.theme

import android.os.Build
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView

@Stable
@Immutable
interface CustomTheme {

    val primary: Color
    val drawer: Color
    val notoListBackGround: Color
    val userSettingsBackGround: Color
    val notoViewBackground: Color
    val headerBar: Color
    val text: Color
    val subtext: Color
    val surface: Color
    val background: Color
    val error: Color
    val bottomSheet: Color
}
@Stable
@Immutable
data class Light(
    override val primary: Color = Color(0xFF1A85D5),
    override val drawer: Color = Color(0xFFDFEEFF),
    override val headerBar: Color = Color(0xFFDFEEFF),
    override val text: Color = Slate00,
    override val subtext: Color = Slate30,
    override val surface: Color = Color(0xFF9BCBFF),
    override val background: Color = Color(0xFFF8FCFF),
    override val error: Color = Color(0xFFDB0812),
    override val bottomSheet: Color = Color(0xFFF8FBFF),
    override val notoListBackGround: Color = Color.White,
    override val userSettingsBackGround: Color = Color.White,
    override val notoViewBackground: Color = Color.White
) : CustomTheme

@Stable
@Immutable
data class Dark(
    override val primary: Color = Color(0xff2185d0),
    override val drawer: Color = Color(0xFF001D35),
    override val headerBar: Color = Slate40,
    override val text: Color = Slate00,
    override val subtext: Color = Slate100,
    override val surface: Color = Slate30,
    override val background: Color = Slate900,
    override val error: Color = Color(0xFF99080F),
    override val bottomSheet: Color = Slate30,
    override val notoListBackGround: Color = Color.White,
    override val userSettingsBackGround: Color = Color.White,
    override val notoViewBackground: Color = Color.White
) : CustomTheme

val LocalCustomTheme = compositionLocalOf<CustomTheme> { Light() }

private val LightColors = lightColorScheme(
    primary = md_theme_light_primary,
    onPrimary = md_theme_light_onPrimary,
    primaryContainer = md_theme_light_primaryContainer,
    onPrimaryContainer = md_theme_light_onPrimaryContainer,
    secondary = md_theme_light_secondary,
    onSecondary = md_theme_light_onSecondary,
    secondaryContainer = md_theme_light_secondaryContainer,
    onSecondaryContainer = md_theme_light_onSecondaryContainer,
    tertiary = md_theme_light_tertiary,
    onTertiary = md_theme_light_onTertiary,
    tertiaryContainer = md_theme_light_tertiaryContainer,
    onTertiaryContainer = md_theme_light_onTertiaryContainer,
    error = md_theme_light_error,
    errorContainer = md_theme_light_errorContainer,
    onError = md_theme_light_onError,
    onErrorContainer = md_theme_light_onErrorContainer,
    background = md_theme_light_background,
    onBackground = md_theme_light_onBackground,
    surface = md_theme_light_surface,
    onSurface = md_theme_light_onSurface,
    surfaceVariant = md_theme_light_surfaceVariant,
    onSurfaceVariant = md_theme_light_onSurfaceVariant,
    outline = md_theme_light_outline,
    inverseOnSurface = md_theme_light_inverseOnSurface,
    inverseSurface = md_theme_light_inverseSurface,
    inversePrimary = md_theme_light_inversePrimary,
    surfaceTint = md_theme_light_surfaceTint,
    outlineVariant = md_theme_light_outlineVariant,
    scrim = md_theme_light_scrim,
)

private val DarkColors = darkColorScheme(
    primary = md_theme_dark_primary,
    onPrimary = md_theme_dark_onPrimary,
    primaryContainer = md_theme_dark_primaryContainer,
    onPrimaryContainer = md_theme_dark_onPrimaryContainer,
    secondary = md_theme_dark_secondary,
    onSecondary = md_theme_dark_onSecondary,
    secondaryContainer = md_theme_dark_secondaryContainer,
    onSecondaryContainer = md_theme_dark_onSecondaryContainer,
    tertiary = md_theme_dark_tertiary,
    onTertiary = md_theme_dark_onTertiary,
    tertiaryContainer = md_theme_dark_tertiaryContainer,
    onTertiaryContainer = md_theme_dark_onTertiaryContainer,
    error = md_theme_dark_error,
    errorContainer = md_theme_dark_errorContainer,
    onError = md_theme_dark_onError,
    onErrorContainer = md_theme_dark_onErrorContainer,
    background = md_theme_dark_background,
    onBackground = md_theme_dark_onBackground,
    surface = md_theme_dark_surface,
    onSurface = md_theme_dark_onSurface,
    surfaceVariant = md_theme_dark_surfaceVariant,
    onSurfaceVariant = md_theme_dark_onSurfaceVariant,
    outline = md_theme_dark_outline,
    inverseOnSurface = md_theme_dark_inverseOnSurface,
    inverseSurface = md_theme_dark_inverseSurface,
    inversePrimary = md_theme_dark_inversePrimary,
    surfaceTint = md_theme_dark_surfaceTint,
    outlineVariant = md_theme_dark_outlineVariant,
    scrim = md_theme_dark_scrim,
)

@Composable
fun JikanNotoTheme(
    darkTheme: Boolean,
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColors
        else -> LightColors
    }
    val view = LocalView.current
    CompositionLocalProvider(
        LocalSpacing provides Spacing(), LocalCustomTheme providesDefault Light()
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}
