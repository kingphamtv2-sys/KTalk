package vn.noi.english.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import vn.noi.english.content.Curriculum
import vn.noi.english.learning.LearningState
import vn.noi.english.ui.components.*

@Composable fun ProgressScreen(state: LearningState, onBack: () -> Unit) {
    Page {
        BackHeader("Trên điện thoại này", onBack)
        Eyebrow("Hành trình của bạn")
        ScreenHeading("Mỗi bài học,\nmột bước tiến.")
        Panel { Text("${state.progress.completedLessonIds.size} / ${Curriculum.lessons.size} bài", style = MaterialTheme.typography.headlineLarge); ProgressTrack(state.progress.completedLessonIds.size / Curriculum.lessons.size.coerceAtLeast(1).toFloat(), "Tổng số bài đã hoàn thành") }
        Curriculum.topics.forEach { topic ->
            Panel {
                Text(topic.title, style = MaterialTheme.typography.titleMedium)
                val count = topic.lessons.count { it.id in state.progress.completedLessonIds }
                ProgressTrack(count / topic.lessons.size.coerceAtLeast(1).toFloat(), "$count / ${topic.lessons.size} bài hoàn thành")
            }
        }
        Spacer(Modifier.height(4.dp))
        Muted("Tiến độ được lưu trên điện thoại này, chưa đồng bộ qua tài khoản. Xóa dữ liệu hoặc gỡ ứng dụng có thể làm mất tiến độ.")
    }
}
