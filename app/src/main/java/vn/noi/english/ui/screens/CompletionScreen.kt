package vn.noi.english.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import vn.noi.english.learning.LearningState
import vn.noi.english.ui.components.*
import vn.noi.english.ui.theme.*

@Composable fun CompletionScreen(state: LearningState, onLessons: () -> Unit, onReplay: () -> Unit) {
    val lesson = state.lesson ?: return
    Page {
        BackHeader("Đã lưu tiến độ", onLessons)
        Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Spacer(Modifier.height(16.dp))
            Surface(color = Mint, shape = RoundedCornerShape(22.dp)) { Box(Modifier.size(76.dp), contentAlignment = Alignment.Center) { Icon(Icons.Outlined.Check, null, Modifier.size(36.dp), tint = Ink) } }
            Eyebrow("Từng bước một")
            ScreenHeading("Thêm một chút\ntự tin hôm nay.")
            Muted(lesson.title)
        }
        Panel {
            Text("4 / 4 lượt hội thoại", style = MaterialTheme.typography.titleMedium)
            Muted("3 cụm từ đã luyện · Không chấm điểm phát âm")
        }
        Spacer(Modifier.height(8.dp))
        Text("Mang theo vào cuộc sống", style = MaterialTheme.typography.titleMedium)
        lesson.vocabulary.forEach { word ->
            Column { Text(word.english, style = MaterialTheme.typography.bodyLarge); Muted(word.vietnamese) }
            HorizontalDivider(color = Line)
        }
        MainButton("Về danh sách bài", onClick = onLessons)
        SecondaryButton("Luyện lại hội thoại", onClick = onReplay)
    }
}
