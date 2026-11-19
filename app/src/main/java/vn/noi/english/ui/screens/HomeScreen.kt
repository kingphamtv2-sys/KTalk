package vn.noi.english.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowForward
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import vn.noi.english.content.Curriculum
import vn.noi.english.learning.LearningState
import vn.noi.english.ui.components.*
import vn.noi.english.ui.theme.*

@Composable fun HomeScreen(state: LearningState, onTopic: (String) -> Unit, onLesson: (String) -> Unit) {
    val suggested = Curriculum.lesson(state.progress.lessonId) ?: Curriculum.lessons.firstOrNull { it.id !in state.progress.completedLessonIds } ?: Curriculum.lessons.first()
    Page {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text("nói.", style = MaterialTheme.typography.headlineLarge, color = Blue)
            Text("Lưu trên thiết bị", style = MaterialTheme.typography.bodySmall, color = Subtle)
        }
        Spacer(Modifier.height(8.dp))
        Eyebrow("Mỗi ngày, một chút tự tin")
        ScreenHeading("Bắt đầu từ\nmột lời chào.")
        Muted("Không cần nói hoàn hảo. Cứ thử nhé.")
        Spacer(Modifier.height(4.dp))
        Surface(color = Navy, shape = RoundedCornerShape(20.dp)) {
            Column(Modifier.padding(22.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("BÀI HỌC GỢI Ý", style = MaterialTheme.typography.labelMedium, color = Color(0xFFD4E2F5), letterSpacing = 1.sp)
                Text(suggested.title, style = MaterialTheme.typography.titleLarge, color = Color.White)
                Text("3 cụm từ · 4 lượt hội thoại", style = MaterialTheme.typography.bodyMedium, color = Color(0xFFC5D4EA))
                Button({ onLesson(suggested.id) }, Modifier.fillMaxWidth().heightIn(min = 52.dp), shape = RoundedCornerShape(12.dp), colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = Navy)) {
                    Text(if (state.progress.lessonId == suggested.id && suggested.id !in state.progress.completedLessonIds) "Tiếp tục học" else "Bắt đầu học")
                    Spacer(Modifier.width(12.dp)); Icon(Icons.AutoMirrored.Outlined.ArrowForward, null, Modifier.size(18.dp))
                }
            }
        }
        Spacer(Modifier.height(8.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text("Chủ đề hằng ngày", style = MaterialTheme.typography.titleMedium)
            Text("${state.progress.completedLessonIds.size} / 15 bài", style = MaterialTheme.typography.bodySmall, color = Subtle)
        }
        Curriculum.topics.forEach { topic ->
            val done = topic.lessons.count { it.id in state.progress.completedLessonIds }
            Row(Modifier.fillMaxWidth().clickable { onTopic(topic.id) }.padding(vertical = 10.dp).heightIn(min = 56.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                TopicIcon(topic.id)
                Column(Modifier.weight(1f)) {
                    Text(topic.title, style = MaterialTheme.typography.titleMedium)
                    Text(if (done == 0) "3 bài · Chưa bắt đầu" else "$done / 3 bài hoàn thành", style = MaterialTheme.typography.bodySmall, color = Subtle)
                }
                Icon(Icons.AutoMirrored.Outlined.KeyboardArrowRight, null, tint = Subtle)
            }
            if (topic != Curriculum.topics.last()) HorizontalDivider(color = Line)
        }
    }
}
