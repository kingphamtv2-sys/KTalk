package vn.noi.english.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.MenuBook
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material.icons.outlined.Style
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.testTag
import androidx.activity.compose.BackHandler
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import vn.noi.english.audio.SpeechInput
import vn.noi.english.audio.SpeechOutput
import vn.noi.english.audio.VoiceState
import vn.noi.english.learning.*
import vn.noi.english.ui.screens.*
import vn.noi.english.ui.theme.*
import vn.noi.english.ui.components.*
import vn.noi.english.vocabulary.VocabularyViewModel
import vn.noi.english.vocabulary.VocabularyPage

@Composable fun NoiApp(viewModel: LearningViewModel, speechInput: SpeechInput, speechOutput: SpeechOutput,
                       requestMicrophone: () -> Unit, openVoiceSettings: () -> Unit, vocabularyViewModel: VocabularyViewModel) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val voice by speechOutput.status.collectAsStateWithLifecycle()
    val words by vocabularyViewModel.state.collectAsStateWithLifecycle()
    var speechMessage by remember { mutableStateOf<String?>(null) }
    val canNavigate = !state.saving && !words.saving
    fun stopAudio() { speechInput.cancel(); speechOutput.stop(); viewModel.setAudio(AudioPhase.Idle) }
    fun navigate(action: () -> Unit) {
        if (viewModel.state.value.saving || vocabularyViewModel.state.value.saving) return
        stopAudio(); action()
    }
    fun speak(text: String) { speechMessage = null; speechInput.cancel(); viewModel.setAudio(AudioPhase.Idle); speechOutput.speak(text) { speechMessage = it } }
    DisposableEffect(state.screen, state.lessonId, state.turnIndex, state.mode, words.page, words.currentCard?.id, words.currentQuestion?.card?.id) {
        onDispose { stopAudio(); speechMessage = null }
    }
    BackHandler(state.screen != Screen.Home) {
        navigate {
            if (viewModel.state.value.screen != Screen.VocabularyMode || !vocabularyViewModel.back()) viewModel.back()
        }
    }
    Scaffold(containerColor = Paper, contentWindowInsets = WindowInsets.safeDrawing,
        bottomBar = {
            if (state.screen == Screen.Home || state.screen == Screen.Progress || state.screen == Screen.VocabularyMode) NavigationBar(containerColor = Color.White, tonalElevation = 0.dp) {
                NavigationBarItem(selected = state.screen == Screen.Home, enabled = canNavigate, onClick = { navigate(viewModel::home) }, icon = { Icon(Icons.AutoMirrored.Outlined.MenuBook, null) }, label = { Text("Học mỗi ngày") })
                NavigationBarItem(selected = state.screen == Screen.VocabularyMode, enabled = canNavigate, onClick = { navigate { vocabularyViewModel.openHome(); viewModel.showVocabulary() } }, icon = { Icon(Icons.Outlined.Style, null) }, label = { Text("Từ vựng") })
                NavigationBarItem(selected = state.screen == Screen.Progress, enabled = canNavigate, onClick = { navigate(viewModel::showProgress) }, icon = { Icon(Icons.Outlined.BarChart, null) }, label = { Text("Tiến độ") })
            }
        }) { padding ->
        Column(Modifier.fillMaxSize().padding(padding).consumeWindowInsets(padding).imePadding()) {
            val displayMessage = speechMessage ?: state.message.takeIf { state.ready || state.screen != Screen.VocabularyMode }
            if (displayMessage != null) Surface(color = Color(0xFFF6EEDF), modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(horizontal = 22.dp, vertical = 12.dp)) {
                    Text(displayMessage, style = MaterialTheme.typography.bodyMedium)
                    TextButton({ speechMessage = null; viewModel.message(null) }) { Text("Đã hiểu") }
                }
            }
            Box(Modifier.weight(1f).fillMaxWidth().testTag("screen-content")) {
                when {
                    state.screen != Screen.VocabularyMode && state.loading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
                    state.screen != Screen.VocabularyMode && !state.ready -> Column(Modifier.padding(22.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) { ScreenHeading("Tiến độ của bạn"); Muted("Chưa tải được dữ liệu học. Bạn có thể thử lại."); MainButton("Tải lại tiến độ", onClick = viewModel::reloadProgress) }
                    else -> when(state.screen) {
                        Screen.Home -> HomeScreen(state, viewModel::openTopic, viewModel::openLesson)
                        Screen.Progress -> ProgressScreen(state, viewModel::home)
                        Screen.VocabularyMode -> VocabularyModeScreen(words, vocabularyViewModel, ::speak)
                        Screen.Lessons -> LessonsScreen(state, viewModel::home, viewModel::openLesson)
                        Screen.Vocabulary -> VocabularyScreen(state, viewModel::back, ::speak, viewModel::startConversation)
                        Screen.Conversation -> key(state.lessonId, state.turnIndex) { ConversationScreen(state, { stopAudio(); viewModel.back() }, ::speak,
                            { stopAudio(); viewModel.setMode(it) }, viewModel::setAnswer, viewModel::submit,
                            { if (!state.saving) { speechOutput.stop(); requestMicrophone() } }, speechInput::stop,
                            { stopAudio(); viewModel.retry() }, { stopAudio(); viewModel.nextTurn() }) }
                        Screen.Completion -> CompletionScreen(state, { viewModel.openTopic(state.topicId) }, { state.lessonId?.let(viewModel::openLesson) })
                    }
                }
            }
            if (((state.ready && (state.screen == Screen.Vocabulary || state.screen == Screen.Conversation)) ||
                    (state.screen == Screen.VocabularyMode && words.ready && words.page in listOf(VocabularyPage.Cards, VocabularyPage.Quiz))) && voice == VoiceState.Unavailable) {
                Surface(color = Color(0xFFEAF0F6)) { Row(Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text("Chưa có giọng đọc tiếng Anh", Modifier.weight(1f), style = MaterialTheme.typography.bodySmall)
                    TextButton(openVoiceSettings) { Text("Cài giọng") }
                } }
            }
        }
    }
}
