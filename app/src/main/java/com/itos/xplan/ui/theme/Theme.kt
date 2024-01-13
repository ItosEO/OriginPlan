package com.itos.xplan.ui.theme

import android.app.Activity
import android.os.Build
import android.view.WindowManager
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import com.google.accompanist.systemuicontroller.rememberSystemUiController

private val DarkColorScheme = darkColorScheme(
//        primary = Purple80,
//        secondary = PurpleGrey80,
//        tertiary = Pink80
    primary = DarkPrimary,
    primaryContainer = DarkPrimaryContainer,
    secondary = DarkSecondary,
    tertiary = DarkTertiary,
    error = DarkError
)

private val LightColorScheme = lightColorScheme(
//        primary = Purple40,
//        secondary = PurpleGrey40,
//        tertiary = Pink40
    primary = LightPrimary,
    primaryContainer = LightPrimaryContainer,
    secondary = LightSecondary,
    tertiary = LightTertiary

    /* Other default colors to override
background = Color(0xFFFFFBFE),
surface = Color(0xFFFFFBFE),
onPrimary = Color.White,
onSecondary = Color.White,
onTertiary = Color.White,
onBackground = Color(0xFF1C1B1F),
onSurface = Color(0xFF1C1B1F),
*/
)

@Suppress("NAME_SHADOWING")
@Composable
fun OriginPlanTheme(
    // 是否处于暗色模式
    darkTheme: Boolean = isSystemInDarkTheme(),

    // Dynamic color is available on Android 12+
    // 是否支持动态颜色
    dynamicColor: Boolean = true,
    // 内容
    content: @Composable () -> Unit
) {
    // 根据是否支持动态颜色，以及当前系统版本，确定颜色方案
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            // 如果是暗色模式，则使用动态暗色方案，否则使用动态亮色方案
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    // 获取当前视图
    val view = LocalView.current
    // 如果不是编辑模式
    if (!view.isInEditMode) {
        val systemUiController = rememberSystemUiController()
        val context = LocalContext.current
        val view = LocalView.current
        // 添加一个副作用，设置状态栏颜色和外观
        SideEffect {
            val window = (view.context as Activity).window

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                window.attributes.layoutInDisplayCutoutMode =
                    WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
            }

            WindowCompat.setDecorFitsSystemWindows(window, false)

            window.statusBarColor = Color.Transparent.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars =
                !darkTheme
        }
    }

    // 设置颜色方案、类型方案和内容
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}