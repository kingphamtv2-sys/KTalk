package vn.noi.english.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowForward
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowRight
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Forum
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import vn.noi.english.content.Curriculum
import vn.noi.english.learning.LearningState
import vn.noi.english.ui.components.*
import vn.noi.english.ui.theme.*

@Composable fun HomeScreen(state: LearningState, onTopic: (String) -> Unit, onLesson: (String) -> Unit) {
    val unfinishedSaved = Curriculum.lesson(state.progress.lessonId)?.takeIf { it.id !in state.progress.completedLessonIds }
    val suggested = unfinishedSaved ?: Curriculum.lessons.firstOrNull { it.id !in state.progress.completedLessonIds } ?: Curriculum.lessons.first()
    Page {
        BrandHeader()
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Eyebrow("Mỗi ngày, một chút tự tin")
            ScreenHeading("Tự tin nói,\ntừng chút một.")
            Muted("Không cần hoàn hảo. Bắt đầu từ một câu nhé.")
        }
        Surface(shape = RoundedCornerShape(24.dp), color = Navy) {
            Box(Modifier.fillMaxWidth().background(Brush.linearGradient(listOf(Navy, Blue)))) {
                Icon(Icons.Outlined.Forum, null, Modifier.align(Alignment.TopEnd).padding(20.dp).size(60.dp), tint = Color.White.copy(alpha = 0.12f))
                Column(Modifier.fillMaxWidth().padding(22.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("BÀI HỌC GỢI Ý", style = MaterialTheme.typography.labelSmall, color = Color.White.copy(alpha = 0.9f))
                    Text(suggested.title, style = MaterialTheme.typography.headlineMedium, color = Color.White)
                    Text("3 cụm từ · 4 lượt hội thoại", style = MaterialTheme.typography.bodyMedium, color = Color.White.copy(alpha = 0.9f))
                    Button({ onLesson(suggested.id) }, Modifier.fillMaxWidth().heightIn(min = 52.dp), shape = RoundedCornerShape(16.dp),
                        contentPadding = PaddingValues(16.dp), colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = Navy)) {
                        Text(when {
                            unfinishedSaved?.id == suggested.id -> "Tiếp tục học"
                            suggested.id in state.progress.completedLessonIds -> "Học lại"
                            else -> "Bắt đầu học"
                        })
                        Spacer(Modifier.width(10.dp)); Icon(Icons.AutoMirrored.Outlined.ArrowForward, null, Modifier.size(18.dp))
                    }
                }
            }
        }
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text("Chủ đề hằng ngày", Modifier.weight(1f), style = MaterialTheme.typography.titleMedium)
            Text("${state.progress.completedLessonIds.size} / ${Curriculum.lessons.size} bài", style = MaterialTheme.typography.bodySmall, color = Subtle)
        }
        Curriculum.topics.forEach { topic ->
            val done = topic.lessons.count { it.id in state.progress.completedLessonIds }
            Surface(onClick = { onTopic(topic.id) }, color = CardWhite, shape = RoundedCornerShape(20.dp), border = BorderStroke(1.dp, Line)) {
                Row(Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                    TopicIcon(topic.id)
                    Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(topic.title, style = MaterialTheme.typography.titleMedium)
                        Text(when {
                            unfinishedSaved?.topicId == topic.id -> "Đang học · $done / ${topic.lessons.size} bài hoàn thành"
                            done == 0 -> "${topic.lessons.size} bài · Chưa bắt đầu"
                            else -> "$done / ${topic.lessons.size} bài hoàn thành"
                        }, style = MaterialTheme.typography.bodySmall, color = Subtle)
                    }
                    Icon(if (done == topic.lessons.size) Icons.Outlined.CheckCircle else Icons.AutoMirrored.Outlined.KeyboardArrowRight,
                        null, Modifier.size(20.dp), tint = if (done == topic.lessons.size) Teal else Subtle)
                }
            }
        }
    }
}
