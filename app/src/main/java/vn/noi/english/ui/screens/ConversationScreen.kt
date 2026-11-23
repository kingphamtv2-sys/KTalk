package vn.noi.english.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Mic
import androidx.compose.material.icons.outlined.Stop
import androidx.compose.material.icons.automirrored.outlined.VolumeUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import vn.noi.english.learning.*
import vn.noi.english.content.LessonGuides
import vn.noi.english.ui.components.*
import vn.noi.english.ui.theme.*

@Composable fun ConversationScreen(state: LearningState, onBack: () -> Unit, onSpeak: (String) -> Unit,
    onMode: (AnswerMode) -> Unit, onAnswer: (String) -> Unit, onSubmit: () -> Unit,
    onMicrophone: () -> Unit, onStop: () -> Unit, onRetry: () -> Unit, onNext: () -> Unit,
    onSystemSpeech: (() -> Unit)? = null, openSpeechSettings: (() -> Unit)? = null) {
    val lesson = state.lesson ?: return
    val turn = state.turn ?: return
    var editingTranscript by rememberSaveable(lesson.id, state.turnIndex, state.mode) { mutableStateOf(false) }
    val speaker = LessonGuides[lesson.id].speakerAt(state.turnIndex)
    val focusManager = LocalFocusManager.current
    fun checkAnswer() { focusManager.clearFocus(); onSubmit() }
    fun retryAnswer() { editingTranscript = false; onRetry() }
    val answerKeyboard = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences, imeAction = ImeAction.Done)
    val answerActions = KeyboardActions(onDone = { checkAnswer() })
    var hint by rememberSaveable(lesson.id, state.turnIndex) { mutableStateOf(true) }
    Page(spacing = 10.dp) {
        BackHeader("Lượt ${state.turnIndex + 1} / 4", onBack)
        Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
            Text(lesson.title, style = MaterialTheme.typography.titleMedium)
            Text(state.topic.title, style = MaterialTheme.typography.bodySmall, color = Subtle)
        }
        LinearProgressIndicator(progress = { (state.turnIndex + 1) / 4f }, modifier = Modifier.fillMaxWidth().height(4.dp)
            .semantics { contentDescription = "Đang luyện lượt ${state.turnIndex + 1} trong 4 lượt" }, color = Blue, trackColor = Line, drawStopIndicator = {})
        Panel {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Surface(color = BlueTint, shape = CircleShape) { Box(Modifier.size(36.dp), contentAlignment = Alignment.Center) { Text(speaker.take(1), style = MaterialTheme.typography.titleMedium, color = Blue) } }
                Column(Modifier.weight(1f)) {
                    Text(speaker, style = MaterialTheme.typography.labelMedium)
                    Text("Cùng bạn luyện tập", style = MaterialTheme.typography.bodySmall, color = Subtle)
                }
                FilledTonalIconButton({ onSpeak(turn.prompt) }, Modifier.size(48.dp)) { Icon(Icons.AutoMirrored.Outlined.VolumeUp, "Nghe câu hỏi") }
            }
            Text(turn.prompt, style = MaterialTheme.typography.headlineMedium)
            Muted(turn.translation)
        }
        if (state.feedback == Feedback.Accepted) {
            Panel { Eyebrow("Câu trả lời của bạn"); Text(state.answer, style = MaterialTheme.typography.bodyLarge) }
            Panel(Mint, false) {
                Text("✓ Câu trả lời phù hợp!", style = MaterialTheme.typography.titleMedium)
                Text(turn.explanation)
                AudioButton("Nghe câu mẫu") { onSpeak(turn.sample) }
            }
            MainButton(if (state.saving) "Đang lưu…" else if (state.turnIndex == 3) "Hoàn thành bài" else "Lượt tiếp theo", enabled = !state.saving, onClick = onNext)
            SecondaryButton("Thử nói lại", enabled = !state.saving, onClick = ::retryAnswer)
        } else {
            if (state.feedback == Feedback.TryTemplate) Panel(Warm, false) {
                Text("Thử theo gợi ý nhé", style = MaterialTheme.typography.titleMedium)
                Text(turn.explanation)
                Muted("Ứng dụng chỉ kiểm tra theo mẫu bài học. Bạn có thể thử lại bao nhiêu lần tùy thích.")
            }
            Surface(Modifier.fillMaxWidth(), color = BlueTint, shape = RoundedCornerShape(16.dp)) {
                Row(Modifier.padding(horizontal = 14.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                        Text("Gợi ý dành cho bạn", style = MaterialTheme.typography.labelSmall, color = Blue)
                        if (hint) Text(turn.hint, style = MaterialTheme.typography.bodyMedium, color = Ink)
                    }
                    TextButton({ hint = !hint }, enabled = !state.saving) { Text(if (hint) "Ẩn" else "Hiện") }
                }
            }
            SingleChoiceSegmentedButtonRow(Modifier.fillMaxWidth().heightIn(min = 48.dp).height(IntrinsicSize.Min)) {
                AnswerMode.entries.forEachIndexed { index, mode ->
                    SegmentedButton(selected = state.mode == mode, onClick = { editingTranscript = false; onMode(mode) }, enabled = !state.saving, modifier = Modifier.fillMaxHeight(),
                        shape = SegmentedButtonDefaults.itemShape(index = index, count = 3), icon = {}) {
                        Text(when(mode) { AnswerMode.Speak -> "Nói"; AnswerMode.Choose -> "Chọn câu"; AnswerMode.Type -> "Gõ" })
                    }
                }
            }
            when(state.mode) {
                AnswerMode.Speak -> {
                    if ((state.answer.isNotEmpty() || editingTranscript) && state.audioPhase == AudioPhase.Idle) {
                        OutlinedTextField(state.answer, { editingTranscript = true; onAnswer(it) }, Modifier.fillMaxWidth().testTag("answer-input"), label = { Text("Ứng dụng nghe được · Có thể sửa") }, shape = RoundedCornerShape(12.dp),
                            enabled = !state.saving, maxLines = 4, keyboardOptions = answerKeyboard, keyboardActions = answerActions)
                        MainButton("Kiểm tra câu trả lời", !state.saving, ::checkAnswer)
                        SecondaryButton("Nói lại", !state.saving, ::retryAnswer)
                    } else Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Surface(color = CardWhite, shape = RoundedCornerShape(24.dp), border = BorderStroke(1.dp, Line)) {
                            Row(Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                                Surface(color = BlueTint, shape = CircleShape) {
                                    IconButton(onClick = if (state.audioPhase == AudioPhase.Listening) onStop else onMicrophone,
                                        enabled = !state.saving && state.audioPhase in listOf(AudioPhase.Idle, AudioPhase.Listening),
                                        modifier = Modifier.padding(6.dp).size(64.dp), colors = IconButtonDefaults.filledIconButtonColors(containerColor = if (state.audioPhase == AudioPhase.Listening) Teal else Blue, contentColor = Color.White)) {
                                        Icon(if (state.audioPhase == AudioPhase.Listening) Icons.Outlined.Stop else Icons.Outlined.Mic,
                                            if (state.audioPhase == AudioPhase.Listening) "Dừng nói" else "Bắt đầu nói", Modifier.size(28.dp))
                                    }
                                }
                                Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Text(when(state.audioPhase) { AudioPhase.Idle -> "Chạm để bắt đầu nói"; AudioPhase.Preparing -> "Đang mở micro…"; AudioPhase.Listening -> "Đang nghe… Chạm để dừng"; AudioPhase.Processing -> "Đang nhận diện…" }, style = MaterialTheme.typography.titleMedium)
                                    Text("Cứ nói chậm thôi. Bạn có thể thử lại.", style = MaterialTheme.typography.bodySmall, color = Subtle)
                                }
                            }
                        }
                        if (onSystemSpeech != null) TextButton(onSystemSpeech, enabled = !state.saving && state.audioPhase == AudioPhase.Idle) { Text("Nhận diện bằng Android") }
                        if (state.message != null && openSpeechSettings != null) TextButton(openSpeechSettings, enabled = !state.saving && state.audioPhase == AudioPhase.Idle) { Text("Cài đặt nhận diện") }
                    }
                }
                AnswerMode.Choose -> turn.choices.forEach { choice ->
                    OutlinedButton(onClick = { onAnswer(choice); onSubmit() }, Modifier.fillMaxWidth().heightIn(min = 52.dp), shape = RoundedCornerShape(16.dp), border = BorderStroke(1.dp, Line), contentPadding = PaddingValues(16.dp), colors = ButtonDefaults.outlinedButtonColors(containerColor = CardWhite)) {
                        Text(choice, Modifier.fillMaxWidth(), color = Ink)
                    }
                }
                AnswerMode.Type -> {
                    OutlinedTextField(state.answer, onAnswer, Modifier.fillMaxWidth().testTag("answer-input"), label = { Text("Câu trả lời của bạn") }, placeholder = { Text("Nhập câu tiếng Anh…") }, shape = RoundedCornerShape(12.dp),
                        enabled = !state.saving, maxLines = 4, keyboardOptions = answerKeyboard, keyboardActions = answerActions)
                    MainButton("Kiểm tra câu trả lời", !state.saving, ::checkAnswer)
                }
            }
            if (hint) Panel(BlueTint, false) {
                Text("Ví dụ: ${turn.sample}", style = MaterialTheme.typography.bodyLarge)
                AudioButton("Nghe câu mẫu") { onSpeak(turn.sample) }
            }
        }
        Spacer(Modifier.height(12.dp))
    }
}
