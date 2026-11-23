package vn.noi.english.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.VolumeUp
import androidx.compose.ui.Alignment
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import vn.noi.english.learning.LearningState
import vn.noi.english.content.LessonGuides
import vn.noi.english.ui.components.*
import vn.noi.english.ui.theme.*

@Composable fun VocabularyScreen(state: LearningState, onBack: () -> Unit, onSpeak: (String) -> Unit, onStart: () -> Unit) {
    val lesson = state.lesson ?: return
    val guide = LessonGuides[lesson.id]
    Page {
        BackHeader(onBack = onBack)
        Eyebrow("Chuẩn bị · 4 lượt hội thoại")
        ScreenHeading(lesson.title)
        Muted(lesson.situation)
        Panel(BlueTint, false) {
            Eyebrow("Mục tiêu giao tiếp")
            Text(guide.objective, style = MaterialTheme.typography.titleMedium)
            Muted("Bạn là: ${guide.learnerRole}")
            Muted("Trò chuyện với: ${guide.partnerRole}")
        }
        Text("3 cụm từ để bắt đầu", style = MaterialTheme.typography.titleMedium)
        Muted("Chạm loa để nghe, rồi thử nói theo một lần.")
        Panel {
            lesson.vocabulary.forEachIndexed { index, word ->
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(5.dp)) {
                        Text(word.english, style = MaterialTheme.typography.titleMedium)
                        Muted(word.vietnamese)
                    }
                    FilledTonalIconButton({ onSpeak(word.english) }, Modifier.size(48.dp)) { Icon(Icons.AutoMirrored.Outlined.VolumeUp, "Nghe ${word.english}") }
                }
                if (index != lesson.vocabulary.lastIndex) HorizontalDivider(color = Line)
            }
        }
        Muted("Bạn có thể nói, chọn câu hoặc gõ. Các câu trả lời được kiểm tra theo mẫu của bài.")
        MainButton(if (state.saving) "Đang lưu…" else "Bắt đầu hội thoại", enabled = !state.saving, onClick = onStart)
    }
}
