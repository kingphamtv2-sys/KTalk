package vn.noi.english.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.material3.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import vn.noi.english.R

val Ink = Color(0xFF172D3E)
val Subtle = Color(0xFF61717E)
val Blue = Color(0xFF254FDB)
val Paper = Color(0xFFF7F9FB)
val Navy = Color(0xFF203C6D)
val Line = Color(0xFFE3E9EE)
val Mint = Color(0xFFE8F3EF)
private val NoiFont = FontFamily(Font(R.font.be_vietnam_pro_regular), Font(R.font.be_vietnam_pro_semibold, FontWeight.SemiBold))
private fun style(size: Int, weight: FontWeight = FontWeight.Normal) = TextStyle(fontFamily = NoiFont, fontWeight = weight, fontSize = size.sp, lineHeight = (size * 1.5).sp)
private val Type = Typography(
    headlineLarge = style(28, FontWeight.SemiBold), headlineMedium = style(24, FontWeight.SemiBold),
    titleLarge = style(20, FontWeight.SemiBold), titleMedium = style(16, FontWeight.SemiBold),
    bodyLarge = style(16), bodyMedium = style(14), bodySmall = style(12),
    labelLarge = style(14, FontWeight.SemiBold), labelMedium = style(12, FontWeight.SemiBold), labelSmall = style(12),
)
@Composable fun NoiTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = lightColorScheme(primary = Blue, onPrimary = Color.White,
        background = Paper, onBackground = Ink, surface = Paper, onSurface = Ink,
        surfaceVariant = Color(0xFFEAF0F6), onSurfaceVariant = Subtle, outlineVariant = Line,
        secondaryContainer = Color(0xFFE8EDFC), onSecondaryContainer = Blue), typography = Type, content = content)
}
