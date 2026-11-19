package vn.noi.english.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import vn.noi.english.learning.LearningState
import vn.noi.english.ui.components.*

@Composable fun VocabularyScreen(state: LearningState, onBack: () -> Unit, onSpeak: (String) -> Unit, onStart: () -> Unit) {
    val lesson = state.lesson ?: return
    Page {
        BackHeader(onBack = onBack)
        Spacer(Modifier.height(8.dp))
        Eyebrow("Trước khi bắt đầu")
        ScreenHeading("Một vài cụm từ\nsẽ giúp bạn.")
        Muted("Nghe và làm quen trước khi trò chuyện.")
        lesson.vocabulary.forEach { word ->
            Panel {
                Text(word.english, style = MaterialTheme.typography.titleLarge)
                Muted(word.vietnamese)
                AudioButton("Nghe ${word.english}") { onSpeak(word.english) }
            }
        }
        Panel(Color(0xFFEDF3F9), false) {
            Eyebrow("Tình huống luyện tập")
            Text(lesson.title, style = MaterialTheme.typography.titleMedium)
            Muted(lesson.situation)
        }
        MainButton(if (state.saving) "Đang lưu…" else "Bắt đầu hội thoại", enabled = !state.saving, onClick = onStart)
    }
}
