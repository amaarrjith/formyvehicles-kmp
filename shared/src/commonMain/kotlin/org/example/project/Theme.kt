package org.example.project

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import org.jetbrains.compose.resources.Font
import formyvehiclesai.shared.generated.resources.Res
import formyvehiclesai.shared.generated.resources.poppins_regular
import formyvehiclesai.shared.generated.resources.poppins_medium
import formyvehiclesai.shared.generated.resources.poppins_semibold
import formyvehiclesai.shared.generated.resources.poppins_bold
import formyvehiclesai.shared.generated.resources.inter_regular
import formyvehiclesai.shared.generated.resources.inter_medium
import formyvehiclesai.shared.generated.resources.inter_semibold
import formyvehiclesai.shared.generated.resources.inter_bold
import formyvehiclesai.shared.generated.resources.inter_tight_regular
import formyvehiclesai.shared.generated.resources.inter_tight_medium
import formyvehiclesai.shared.generated.resources.inter_tight_semibold
import formyvehiclesai.shared.generated.resources.inter_tight_bold
import formyvehiclesai.shared.generated.resources.plus_jakarta_sans_regular
import formyvehiclesai.shared.generated.resources.plus_jakarta_sans_medium
import formyvehiclesai.shared.generated.resources.plus_jakarta_sans_semibold
import formyvehiclesai.shared.generated.resources.plus_jakarta_sans_bold

val LightAppColorScheme = lightColorScheme(
    primary = Color(0xFF6366F1), // Indigo
    onPrimary = Color.White,
    primaryContainer = Color(0xFFEEF2FF),
    onPrimaryContainer = Color(0xFF4F46E5),
    secondary = Color(0xFF64748B),
    onSecondary = Color.White,
    background = Color.White,
    onBackground = Color(0xFF1E293B),
    surface = Color.White,
    onSurface = Color(0xFF1E293B),
    surfaceVariant = Color(0xFFF8FAFC),
    onSurfaceVariant = Color(0xFF64748B),
    surfaceTint = Color.Transparent, // Disables default Material 3 pink/purple surface tinting
    outline = Color(0xFFE2E8F0),
    outlineVariant = Color(0xFFCBD5E1)
)

@Composable
fun getPlusJakartaSansFontFamily(): FontFamily {
    return FontFamily(
        Font(Res.font.plus_jakarta_sans_regular, weight = FontWeight.Normal, style = FontStyle.Normal),
        Font(Res.font.plus_jakarta_sans_medium, weight = FontWeight.Medium, style = FontStyle.Normal),
        Font(Res.font.plus_jakarta_sans_semibold, weight = FontWeight.SemiBold, style = FontStyle.Normal),
        Font(Res.font.plus_jakarta_sans_bold, weight = FontWeight.Bold, style = FontStyle.Normal)
    )
}

@Composable
fun getJakartaSansFontFamily(): FontFamily {
    return getPlusJakartaSansFontFamily()
}

@Composable
fun getPoppinsFontFamily(): FontFamily {
    return getPlusJakartaSansFontFamily()
}

@Composable
fun getInterFontFamily(): FontFamily {
    return getPlusJakartaSansFontFamily()
}

@Composable
fun getInterTightFontFamily(): FontFamily {
    return getPlusJakartaSansFontFamily()
}

@Composable
fun AppTheme(
    content: @Composable () -> Unit
) {
    val jakartaFontFamily = getPlusJakartaSansFontFamily()
    val defaultTypography = Typography()
    val typography = Typography(
        displayLarge = defaultTypography.displayLarge.copy(fontFamily = jakartaFontFamily),
        displayMedium = defaultTypography.displayMedium.copy(fontFamily = jakartaFontFamily),
        displaySmall = defaultTypography.displaySmall.copy(fontFamily = jakartaFontFamily),
        headlineLarge = defaultTypography.headlineLarge.copy(fontFamily = jakartaFontFamily),
        headlineMedium = defaultTypography.headlineMedium.copy(fontFamily = jakartaFontFamily),
        headlineSmall = defaultTypography.headlineSmall.copy(fontFamily = jakartaFontFamily),
        titleLarge = defaultTypography.titleLarge.copy(fontFamily = jakartaFontFamily),
        titleMedium = defaultTypography.titleMedium.copy(fontFamily = jakartaFontFamily),
        titleSmall = defaultTypography.titleSmall.copy(fontFamily = jakartaFontFamily),
        bodyLarge = defaultTypography.bodyLarge.copy(fontFamily = jakartaFontFamily),
        bodyMedium = defaultTypography.bodyMedium.copy(fontFamily = jakartaFontFamily),
        bodySmall = defaultTypography.bodySmall.copy(fontFamily = jakartaFontFamily),
        labelLarge = defaultTypography.labelLarge.copy(fontFamily = jakartaFontFamily),
        labelMedium = defaultTypography.labelMedium.copy(fontFamily = jakartaFontFamily),
        labelSmall = defaultTypography.labelSmall.copy(fontFamily = jakartaFontFamily)
    )

    MaterialTheme(
        colorScheme = LightAppColorScheme,
        typography = typography,
        content = content
    )
}
