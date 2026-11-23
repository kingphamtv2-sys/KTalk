package vn.noi.english.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.VolumeUp
import androidx.compose.material.icons.automirrored.outlined.HelpOutline
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import vn.noi.english.ui.theme.*
import vn.noi.english.R

@Composable fun Page(spacing: Dp = 16.dp, content: @Composable ColumnScope.() -> Unit) {
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal = 20.dp, vertical = 18.dp), verticalArrangement = Arrangement.spacedBy(spacing), content = content)
}
@Composable fun Eyebrow(text: String) { Text(text.uppercase(java.util.Locale.ROOT), style = MaterialTheme.typography.labelMedium, color = Subtle, letterSpacing = 0.6.sp) }
@Composable fun Muted(text: String, modifier: Modifier = Modifier) { Text(text, modifier, style = MaterialTheme.typography.bodyMedium, color = Subtle) }
@Composable fun MainButton(text: String, enabled: Boolean = true, onClick: () -> Unit) {
    Button(onClick, Modifier.fillMaxWidth().heightIn(min = 52.dp), enabled = enabled, shape = RoundedCornerShape(16.dp), contentPadding = PaddingValues(horizontal = 18.dp, vertical = 14.dp)) { Text(text) }
}
@Composable fun SecondaryButton(text: String, enabled: Boolean = true, onClick: () -> Unit) {
    FilledTonalButton(onClick, Modifier.fillMaxWidth().heightIn(min = 52.dp), enabled = enabled, shape = RoundedCornerShape(16.dp), contentPadding = PaddingValues(horizontal = 18.dp, vertical = 14.dp)) { Text(text) }
}
@Composable fun BackHeader(label: String = "Khoảng 5 phút", onBack: () -> Unit) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        FilledIconButton(onBack, Modifier.size(48.dp), shape = CircleShape,
            colors = IconButtonDefaults.filledIconButtonColors(containerColor = CardWhite, contentColor = Ink)) { Icon(Icons.AutoMirrored.Outlined.ArrowBack, "Quay lại") }
        InfoPill(label)
    }
}
@Composable fun AudioButton(label: String, onClick: () -> Unit) {
    TextButton(onClick, Modifier.heightIn(min = 48.dp), shape = RoundedCornerShape(12.dp), contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
        colors = ButtonDefaults.textButtonColors(containerColor = BlueTint, contentColor = Blue)) {
        Icon(Icons.AutoMirrored.Outlined.VolumeUp, null, Modifier.size(18.dp)); Spacer(Modifier.width(8.dp)); Text(label)
    }
}
@Composable fun Panel(color: Color = CardWhite, border: Boolean = true, content: @Composable ColumnScope.() -> Unit) {
    Surface(Modifier.fillMaxWidth(), color = color, shape = RoundedCornerShape(24.dp), border = if (border) BorderStroke(1.dp, Line) else null) {
        Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(10.dp), content = content)
    }
}
@Composable fun InfoPill(text: String, color: Color = Blue, background: Color = BlueTint) {
    Surface(color = background, shape = CircleShape) { Text(text, Modifier.padding(horizontal = 12.dp, vertical = 7.dp), style = MaterialTheme.typography.labelSmall, color = color) }
}
@Composable fun BrandHeader() {
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        Surface(Modifier.size(44.dp), color = Blue, shape = RoundedCornerShape(14.dp)) {
            Box(contentAlignment = Alignment.Center) { Icon(painterResource(R.drawable.ic_launcher_foreground), null, Modifier.requiredSize(72.dp), tint = Color.White) }
        }
        Column(Modifier.weight(1f)) {
            Text(stringResource(R.string.app_name), style = MaterialTheme.typography.titleLarge, color = Ink)
            Text("Tiếng Anh mỗi ngày", style = MaterialTheme.typography.bodySmall, color = Subtle)
        }
        Icon(Icons.Outlined.VerifiedUser, "Lưu trên thiết bị", Modifier.size(24.dp), tint = Teal)
    }
}
@Composable fun AdaptiveActions(primary: @Composable () -> Unit, secondary: @Composable () -> Unit) {
    BoxWithConstraints(Modifier.fillMaxWidth()) {
        if (maxWidth < 300.dp || LocalDensity.current.fontScale > 1.3f) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) { primary(); secondary() }
        } else Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Box(Modifier.weight(1f)) { primary() }
            Box(Modifier.weight(1f)) { secondary() }
        }
    }
}
fun topicAccent(id: String): Color = when (id) {
    "family", "home", "help", "routine" -> Teal
    "leisure", "phone", "weather" -> Violet
    else -> Blue
}
fun topicIcon(id: String): ImageVector = when(id) {
    "introductions" -> Icons.Outlined.PersonOutline
    "food" -> Icons.Outlined.LocalCafe
    "shopping" -> Icons.Outlined.ShoppingBag
    "directions" -> Icons.Outlined.Directions
    "family" -> Icons.Outlined.PeopleOutline
    "routine" -> Icons.Outlined.WbSunny
    "time" -> Icons.Outlined.Schedule
    "weather" -> Icons.Outlined.Cloud
    "transport" -> Icons.Outlined.DirectionsBus
    "hotel" -> Icons.Outlined.Hotel
    "leisure" -> Icons.Outlined.Headphones
    "help" -> Icons.AutoMirrored.Outlined.HelpOutline
    "home" -> Icons.Outlined.Home
    "phone" -> Icons.Outlined.Call
    else -> Icons.Outlined.ChatBubbleOutline
}
@Composable fun TopicIcon(id: String) {
    Surface(color = topicAccent(id).copy(alpha = 0.09f), shape = RoundedCornerShape(16.dp)) {
        Box(Modifier.size(48.dp), contentAlignment = Alignment.Center) { Icon(topicIcon(id), null, Modifier.size(24.dp), tint = topicAccent(id)) }
    }
}
@Composable fun ProgressTrack(value: Float, description: String) {
    LinearProgressIndicator(progress = { value.coerceIn(0f, 1f) }, modifier = Modifier.fillMaxWidth().height(6.dp), color = Blue, trackColor = Line,
        drawStopIndicator = {})
    Text(description, style = MaterialTheme.typography.bodySmall, color = Subtle)
}
@Composable fun ScreenHeading(text: String) { Text(text, style = MaterialTheme.typography.headlineLarge, color = Ink, fontWeight = FontWeight.SemiBold) }
