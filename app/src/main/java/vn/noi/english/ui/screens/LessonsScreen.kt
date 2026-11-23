package vn.noi.english.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowRight
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import vn.noi.english.learning.LearningState
import vn.noi.english.ui.components.*
import vn.noi.english.ui.theme.*

@Composable fun LessonsScreen(state: LearningState, onBack: () -> Unit, onLesson: (String) -> Unit) {
    val topic = state.topic
    Page {
        BackHeader(onBack = onBack)
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            TopicIcon(topic.id)
            Eyebrow("Chủ đề hằng ngày")
        }
        ScreenHeading(topic.title)
        Muted("Những tình huống nhỏ, dùng được mỗi ngày.")
        val done = topic.lessons.count { it.id in state.progress.completedLessonIds }
        Panel(BlueTint, false) { ProgressTrack(done / topic.lessons.size.coerceAtLeast(1).toFloat(), "$done / ${topic.lessons.size} bài hoàn thành") }
        topic.lessons.forEachIndexed { index, lesson ->
            val completed = lesson.id in state.progress.completedLessonIds
            Surface(onClick = { onLesson(lesson.id) }, shape = RoundedCornerShape(20.dp), color = CardWhite, border = BorderStroke(1.dp, Line)) {
                Row(Modifier.fillMaxWidth().padding(16.dp).heightIn(min = 52.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Surface(shape = RoundedCornerShape(14.dp), color = if (completed) Mint else BlueTint) { Text("0${index + 1}", Modifier.padding(12.dp), color = if (completed) Teal else Blue, style = MaterialTheme.typography.labelLarge) }
                    Column(Modifier.weight(1f)) {
                        Text(lesson.title, style = MaterialTheme.typography.titleMedium)
                        Text(if (lesson.id in state.progress.completedLessonIds) "✓ Đã hoàn thành · Có thể học lại" else "3 cụm từ · 4 lượt hội thoại", style = MaterialTheme.typography.bodySmall, color = Subtle)
                    }
                    Icon(if (completed) Icons.Outlined.CheckCircle else Icons.AutoMirrored.Outlined.KeyboardArrowRight, null, Modifier.size(20.dp), tint = if (completed) Teal else Subtle)
                }
            }
        }
        Spacer(Modifier.height(12.dp))
        Muted("Bạn có thể bắt đầu từ bất kỳ bài nào, và học lại bao nhiêu lần tùy thích.")
    }
}
