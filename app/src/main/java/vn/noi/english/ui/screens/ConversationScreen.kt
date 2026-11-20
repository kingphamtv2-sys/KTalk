package vn.noi.english.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Mic
import androidx.compose.material.icons.outlined.Stop
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import vn.noi.english.learning.*
import vn.noi.english.ui.components.*
import vn.noi.english.ui.theme.*

@Composable fun ConversationScreen(state: LearningState, onBack: () -> Unit, onSpeak: (String) -> Unit,
    onMode: (AnswerMode) -> Unit, onAnswer: (String) -> Unit, onSubmit: () -> Unit,
    onMicrophone: () -> Unit, onStop: () -> Unit, onRetry: () -> Unit, onNext: () -> Unit,
    onSystemSpeech: (() -> Unit)? = null, openSpeechSettings: (() -> Unit)? = null) {
    val lesson = state.lesson ?: return
    val turn = state.turn ?: return
    var hint by rememberSaveable(lesson.id, state.turnIndex) { mutableStateOf(true) }
    Page {
        BackHeader("Lượt ${state.turnIndex + 1} / 4", onBack)
        Eyebrow(state.topic.title)
        Muted(lesson.title)
        ProgressTrack((state.turnIndex + 1) / 4f, "Đang luyện lượt ${state.turnIndex + 1} trong 4 lượt")
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Surface(color = Color(0xFFDCE6F5), shape = CircleShape) { Box(Modifier.size(38.dp), contentAlignment = Alignment.Center) { Text("A", color = Navy) } }
            Column { Text("Alex", style = MaterialTheme.typography.titleMedium); Text("Cùng bạn luyện tập", style = MaterialTheme.typography.bodySmall, color = Subtle) }
        }
        Panel {
            Text(turn.prompt, style = MaterialTheme.typography.headlineMedium)
            Muted(turn.translation)
            AudioButton("Nghe câu hỏi") { onSpeak(turn.prompt) }
        }
        if (state.feedback == Feedback.Accepted) {
            Panel { Eyebrow("Câu trả lời của bạn"); Text(state.answer, style = MaterialTheme.typography.bodyLarge) }
            Panel(Mint, false) {
                Text("✓ Câu trả lời phù hợp!", style = MaterialTheme.typography.titleMedium)
                Text(turn.explanation)
                AudioButton("Nghe câu mẫu") { onSpeak(turn.sample) }
            }
            MainButton(if (state.saving) "Đang lưu…" else if (state.turnIndex == 3) "Hoàn thành bài" else "Lượt tiếp theo", enabled = !state.saving, onClick = onNext)
            SecondaryButton("Thử nói lại", enabled = !state.saving, onClick = onRetry)
        } else {
            if (state.feedback == Feedback.TryTemplate) Panel(Color(0xFFF6EEDF), false) {
                Text("Thử theo gợi ý nhé", style = MaterialTheme.typography.titleMedium)
                Text(turn.explanation)
                Muted("Ứng dụng chỉ kiểm tra theo mẫu bài học. Bạn có thể thử lại bao nhiêu lần tùy thích.")
            }
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Eyebrow("Gợi ý dành cho bạn")
                TextButton({ hint = !hint }, enabled = !state.saving) { Text(if (hint) "Ẩn" else "Hiện") }
            }
            if (hint) Panel(Color(0xFFEEF2FA), false) {
                Text(turn.hint, style = MaterialTheme.typography.titleMedium)
                Text("Ví dụ: ${turn.sample}", style = MaterialTheme.typography.bodySmall, color = Subtle)
                AudioButton("Nghe câu mẫu") { onSpeak(turn.sample) }
            }
            SingleChoiceSegmentedButtonRow(Modifier.fillMaxWidth()) {
                AnswerMode.entries.forEachIndexed { index, mode ->
                    SegmentedButton(selected = state.mode == mode, onClick = { onMode(mode) }, enabled = !state.saving,
                        shape = SegmentedButtonDefaults.itemShape(index = index, count = 3), icon = {}) {
                        Text(when(mode) { AnswerMode.Speak -> "Nói"; AnswerMode.Choose -> "Chọn câu"; AnswerMode.Type -> "Gõ" })
                    }
                }
            }
            when(state.mode) {
                AnswerMode.Speak -> {
                    if (state.answer.isNotEmpty() && state.audioPhase == AudioPhase.Idle) {
                        OutlinedTextField(state.answer, onAnswer, Modifier.fillMaxWidth().testTag("answer-input"), label = { Text("Ứng dụng nghe được · Có thể sửa") }, shape = RoundedCornerShape(12.dp))
                        MainButton("Kiểm tra câu trả lời", !state.saving, onSubmit)
                        SecondaryButton("Nói lại", !state.saving, onRetry)
                    } else Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Spacer(Modifier.height(6.dp))
                        Surface(color = Color(0xFFE8EDFC), shape = CircleShape) {
                            IconButton(onClick = if (state.audioPhase == AudioPhase.Listening) onStop else onMicrophone,
                                enabled = !state.saving && state.audioPhase in listOf(AudioPhase.Idle, AudioPhase.Listening),
                                modifier = Modifier.padding(8.dp).size(76.dp), colors = IconButtonDefaults.filledIconButtonColors(containerColor = Blue, contentColor = Color.White)) {
                                Icon(if (state.audioPhase == AudioPhase.Listening) Icons.Outlined.Stop else Icons.Outlined.Mic,
                                    if (state.audioPhase == AudioPhase.Listening) "Dừng nói" else "Bắt đầu nói", Modifier.size(28.dp))
                            }
                        }
                        Text(when(state.audioPhase) { AudioPhase.Idle -> "Chạm để bắt đầu nói"; AudioPhase.Preparing -> "Đang mở micro…"; AudioPhase.Listening -> "Đang nghe… Chạm để dừng"; AudioPhase.Processing -> "Đang nhận diện…" }, style = MaterialTheme.typography.titleMedium)
                        Muted("Cứ nói chậm thôi. Bạn có thể thử lại.")
                        if (onSystemSpeech != null) TextButton(onSystemSpeech, enabled = !state.saving && state.audioPhase == AudioPhase.Idle) { Text("Nhận diện bằng Android") }
                        if (state.message != null && openSpeechSettings != null) TextButton(openSpeechSettings, enabled = !state.saving && state.audioPhase == AudioPhase.Idle) { Text("Cài đặt nhận diện") }
                    }
                }
                AnswerMode.Choose -> turn.choices.forEach { choice ->
                    OutlinedButton(onClick = { onAnswer(choice); onSubmit() }, Modifier.fillMaxWidth().heightIn(min = 52.dp), shape = RoundedCornerShape(12.dp), border = BorderStroke(1.dp, Color(0xFFCAD7E9)), contentPadding = PaddingValues(16.dp)) {
                        Text(choice, Modifier.fillMaxWidth(), color = Ink)
                    }
                }
                AnswerMode.Type -> {
                    OutlinedTextField(state.answer, onAnswer, Modifier.fillMaxWidth().testTag("answer-input"), label = { Text("Câu trả lời của bạn") }, placeholder = { Text("Nhập câu tiếng Anh…") }, shape = RoundedCornerShape(12.dp))
                    MainButton("Kiểm tra câu trả lời", !state.saving, onSubmit)
                }
            }
        }
        Spacer(Modifier.height(12.dp))
    }
}
