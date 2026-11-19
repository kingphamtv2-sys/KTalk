package vn.noi.english.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.VolumeUp
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import vn.noi.english.ui.theme.*

@Composable fun Page(content: @Composable ColumnScope.() -> Unit) {
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal = 22.dp, vertical = 16.dp), verticalArrangement = Arrangement.spacedBy(12.dp), content = content)
}
@Composable fun Eyebrow(text: String) { Text(text.uppercase(java.util.Locale.ROOT), style = MaterialTheme.typography.labelMedium, color = Subtle, letterSpacing = 1.sp) }
@Composable fun Muted(text: String, modifier: Modifier = Modifier) { Text(text, modifier, style = MaterialTheme.typography.bodyMedium, color = Subtle) }
@Composable fun MainButton(text: String, enabled: Boolean = true, onClick: () -> Unit) {
    Button(onClick, Modifier.fillMaxWidth().heightIn(min = 52.dp), enabled = enabled, shape = RoundedCornerShape(12.dp), contentPadding = PaddingValues(16.dp)) { Text(text) }
}
@Composable fun SecondaryButton(text: String, enabled: Boolean = true, onClick: () -> Unit) {
    FilledTonalButton(onClick, Modifier.fillMaxWidth().heightIn(min = 48.dp), enabled = enabled, shape = RoundedCornerShape(12.dp)) { Text(text) }
}
@Composable fun BackHeader(label: String = "Khoảng 5 phút", onBack: () -> Unit) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        OutlinedIconButton(onBack, Modifier.size(48.dp), shape = RoundedCornerShape(12.dp)) { Icon(Icons.AutoMirrored.Outlined.ArrowBack, "Quay lại") }
        Surface(color = Color(0xFFEAF0F6), shape = RoundedCornerShape(8.dp)) { Text(label, Modifier.padding(8.dp), style = MaterialTheme.typography.bodySmall, color = Subtle) }
    }
}
@Composable fun AudioButton(label: String, onClick: () -> Unit) {
    TextButton(onClick, contentPadding = PaddingValues(horizontal = 0.dp)) {
        Icon(Icons.AutoMirrored.Outlined.VolumeUp, null, Modifier.size(18.dp)); Spacer(Modifier.width(8.dp)); Text(label)
    }
}
@Composable fun Panel(color: Color = Color.White, border: Boolean = true, content: @Composable ColumnScope.() -> Unit) {
    Surface(Modifier.fillMaxWidth(), color = color, shape = RoundedCornerShape(16.dp), border = if (border) BorderStroke(1.dp, Line) else null) {
        Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(8.dp), content = content)
    }
}
fun topicIcon(id: String): ImageVector = when(id) {
    "introductions" -> Icons.Outlined.PersonOutline
    "food" -> Icons.Outlined.LocalCafe
    "shopping" -> Icons.Outlined.ShoppingBag
    "directions" -> Icons.Outlined.Directions
    else -> Icons.Outlined.ChatBubbleOutline
}
@Composable fun TopicIcon(id: String) {
    Surface(color = Color(0xFFEAF0F6), shape = RoundedCornerShape(13.dp)) {
        Box(Modifier.size(44.dp), contentAlignment = Alignment.Center) { Icon(topicIcon(id), null, Modifier.size(22.dp), tint = Color(0xFF35536E)) }
    }
}
@Composable fun ProgressTrack(value: Float, description: String) {
    LinearProgressIndicator(progress = { value.coerceIn(0f, 1f) }, modifier = Modifier.fillMaxWidth().height(4.dp), color = Blue, trackColor = Line,
        drawStopIndicator = {})
    Text(description, style = MaterialTheme.typography.bodySmall, color = Subtle)
}
@Composable fun ScreenHeading(text: String) { Text(text, style = MaterialTheme.typography.headlineLarge, color = Ink, fontWeight = FontWeight.SemiBold) }
