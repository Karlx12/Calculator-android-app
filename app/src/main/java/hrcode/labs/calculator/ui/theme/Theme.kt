package hrcode.labs.calculator.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext


private val DarkColorScheme = darkColorScheme(
    primary = ShakespeareDark01,
    secondary = NileBlue,
    tertiary = ShakespeareDark02,
    background = NileBlueDark01,
    surface = NileBlue,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = ShakespeareLight01,
    onSurface = ShakespeareLight01
)

private val LightColorScheme = lightColorScheme(
    primary = ShakespeareLight01,
    secondary = NileBlueLight02,
    tertiary = Shakespeare,
    background = Color.White,
    surface = ShakespeareLight01,
    onPrimary = NileBlue,
    onSecondary = NileBlue,
    onTertiary = NileBlue,
    onBackground = NileBlue,
    onSurface = NileBlue
)

@Composable
fun CalculatorTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}