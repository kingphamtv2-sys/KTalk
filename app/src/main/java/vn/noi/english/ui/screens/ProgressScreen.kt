package vn.noi.english.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import vn.noi.english.content.Curriculum
import vn.noi.english.learning.LearningState
import vn.noi.english.ui.components.*
import vn.noi.english.ui.theme.*
import kotlin.math.roundToInt

@Composable fun ProgressScreen(state: LearningState, onBack: () -> Unit) {
    Page {
        BackHeader("Trên điện thoại này", onBack)
        Eyebrow("Hành trình của bạn")
        ScreenHeading("Mỗi bài học,\nmột bước tiến.")
        val fraction = state.progress.completedLessonIds.size / Curriculum.lessons.size.coerceAtLeast(1).toFloat()
        Panel(BlueTint, false) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp), verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("${state.progress.completedLessonIds.size} / ${Curriculum.lessons.size} bài", style = MaterialTheme.typography.headlineLarge)
                    Text("Tổng số bài đã hoàn thành", style = MaterialTheme.typography.bodyMedium, color = Subtle)
                }
                Box(Modifier.size(76.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(progress = { fraction }, modifier = Modifier.fillMaxSize(), color = Blue, trackColor = Blue.copy(alpha = 0.1f), strokeWidth = 6.dp)
                    Text("${(fraction * 100).roundToInt()}%", style = MaterialTheme.typography.labelLarge, color = Blue)
                }
            }
        }
        Curriculum.topics.forEach { topic ->
            Panel {
                val count = topic.lessons.count { it.id in state.progress.completedLessonIds }
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                    TopicIcon(topic.id)
                    Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(topic.title, style = MaterialTheme.typography.titleMedium)
                        ProgressTrack(count / topic.lessons.size.coerceAtLeast(1).toFloat(), "$count / ${topic.lessons.size} bài hoàn thành")
                    }
                }
            }
        }
        Spacer(Modifier.height(4.dp))
        Muted("Tiến độ được lưu trên điện thoại này, chưa đồng bộ qua tài khoản. Xóa dữ liệu hoặc gỡ ứng dụng có thể làm mất tiến độ.")
    }
}
