package vn.noi.english.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.material3.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.dp
import vn.noi.english.R

val Ink = Color(0xFF172B46)
val Subtle = Color(0xFF5B6B82)
val Blue = Color(0xFF3458DB)
val BlueTint = Color(0xFFEBF0FF)
val Paper = Color(0xFFF5F7FC)
val CardWhite = Color.White
val Navy = Color(0xFF203B87)
val Line = Color(0xFFE5EAF4)
val Mint = Color(0xFFE8F5F0)
val Teal = Color(0xFF107568)
val Violet = Color(0xFF7057B7)
val Warm = Color(0xFFFFF4E3)
private val NoiFont = FontFamily(Font(R.font.be_vietnam_pro_regular), Font(R.font.be_vietnam_pro_semibold, FontWeight.SemiBold))
private fun style(size: Int, weight: FontWeight = FontWeight.Normal) = TextStyle(fontFamily = NoiFont, fontWeight = weight,
    fontSize = size.sp, lineHeight = (size * if (size >= 24) 1.22 else 1.45).sp)
private val Type = Typography(
    displaySmall = style(34, FontWeight.SemiBold),
    headlineLarge = style(28, FontWeight.SemiBold), headlineMedium = style(24, FontWeight.SemiBold),
    titleLarge = style(20, FontWeight.SemiBold), titleMedium = style(16, FontWeight.SemiBold),
    bodyLarge = style(16), bodyMedium = style(14), bodySmall = style(12),
    labelLarge = style(14, FontWeight.SemiBold), labelMedium = style(12, FontWeight.SemiBold), labelSmall = style(12),
)
@Composable fun NoiTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = lightColorScheme(primary = Blue, onPrimary = Color.White,
        background = Paper, onBackground = Ink, surface = CardWhite, onSurface = Ink,
        surfaceVariant = BlueTint, onSurfaceVariant = Subtle, outline = Color(0xFF8390A5), outlineVariant = Line,
        secondary = Teal, secondaryContainer = BlueTint, onSecondaryContainer = Blue,
        tertiary = Violet, errorContainer = Warm, onErrorContainer = Ink),
        shapes = Shapes(small = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
            medium = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
            large = androidx.compose.foundation.shape.RoundedCornerShape(24.dp)), typography = Type, content = content)
}
