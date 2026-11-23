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
import vn.noi.english.ui.components.*
import vn.noi.english.ui.theme.*

@Composable fun VocabularyScreen(state: LearningState, onBack: () -> Unit, onSpeak: (String) -> Unit, onStart: () -> Unit) {
    val lesson = state.lesson ?: return
    Page {
        BackHeader(onBack = onBack)
        Eyebrow("Trước khi bắt đầu")
        ScreenHeading("Một vài cụm từ\nsẽ giúp bạn.")
        Muted("Nghe và làm quen trước khi trò chuyện.")
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
        Panel(BlueTint, false) {
            Eyebrow("Tình huống luyện tập")
            Text(lesson.title, style = MaterialTheme.typography.titleMedium)
            Muted(lesson.situation)
        }
        MainButton(if (state.saving) "Đang lưu…" else "Bắt đầu hội thoại", enabled = !state.saving, onClick = onStart)
    }
}
