package vn.noi.english.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.MenuBook
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material.icons.outlined.Style
import androidx.compose.material.icons.outlined.Info
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
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
                       requestMicrophone: () -> Unit, openVoiceSettings: () -> Unit, vocabularyViewModel: VocabularyViewModel,
                       onSystemSpeech: (() -> Unit)? = null, openSpeechSettings: (() -> Unit)? = null) {
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
            val navColors = NavigationBarItemDefaults.colors(selectedIconColor = Blue, selectedTextColor = Blue,
                indicatorColor = BlueTint, unselectedIconColor = Subtle, unselectedTextColor = Subtle)
            if (state.screen == Screen.Home || state.screen == Screen.Progress || state.screen == Screen.VocabularyMode) NavigationBar(
                modifier = Modifier.heightIn(min = if (LocalDensity.current.fontScale > 1.3f) 112.dp else 80.dp), containerColor = CardWhite, tonalElevation = 0.dp) {
                NavigationBarItem(selected = state.screen == Screen.Home, colors = navColors, enabled = canNavigate, onClick = { navigate(viewModel::home) }, icon = { Icon(Icons.AutoMirrored.Outlined.MenuBook, null) }, label = { Text(if (LocalDensity.current.fontScale > 1.3f) "Học" else "Học mỗi ngày", style = MaterialTheme.typography.labelSmall, textAlign = TextAlign.Center, maxLines = 2) })
                NavigationBarItem(selected = state.screen == Screen.VocabularyMode, colors = navColors, enabled = canNavigate, onClick = { navigate { vocabularyViewModel.openHome(); viewModel.showVocabulary() } }, icon = { Icon(Icons.Outlined.Style, null) }, label = { Text("Từ vựng", style = MaterialTheme.typography.labelSmall, maxLines = 2) })
                NavigationBarItem(selected = state.screen == Screen.Progress, colors = navColors, enabled = canNavigate, onClick = { navigate(viewModel::showProgress) }, icon = { Icon(Icons.Outlined.BarChart, null) }, label = { Text("Tiến độ", style = MaterialTheme.typography.labelSmall, maxLines = 2) })
            }
        }) { padding ->
        BoxWithConstraints(Modifier.fillMaxSize().padding(padding).consumeWindowInsets(padding).imePadding()) {
        val maximumNoticeHeight = (maxHeight * 0.45f).coerceAtLeast(116.dp).coerceAtMost(maxHeight)
        val keyboardVisible = WindowInsets.ime.getBottom(LocalDensity.current) > 0
        Column(Modifier.fillMaxSize()) {
            val displayMessage = speechMessage ?: state.message.takeIf { state.ready || state.screen != Screen.VocabularyMode }
            if (displayMessage != null) Surface(color = Warm, shape = RoundedCornerShape(20.dp), modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp).heightIn(max = maximumNoticeHeight)) {
                Row(Modifier.padding(12.dp), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Icon(Icons.Outlined.Info, null, Modifier.padding(top = 2.dp).size(20.dp), tint = Ink)
                    Column(Modifier.weight(1f)) {
                        key(displayMessage) {
                            Text(displayMessage, Modifier.weight(1f, fill = false).verticalScroll(rememberScrollState()), style = MaterialTheme.typography.bodyMedium, color = Ink)
                        }
                        TextButton({ speechMessage = null; viewModel.message(null) }) { Text("Đã hiểu") }
                    }
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
                            { if (!state.saving) { speechMessage = null; speechOutput.stop(); requestMicrophone() } }, speechInput::stop,
                            { stopAudio(); viewModel.retry() }, { stopAudio(); viewModel.nextTurn() },
                            onSystemSpeech = onSystemSpeech?.let { action -> { stopAudio(); speechMessage = null; action() } },
                            openSpeechSettings = openSpeechSettings) }
                        Screen.Completion -> CompletionScreen(state, { viewModel.openTopic(state.topicId) }, { state.lessonId?.let(viewModel::openLesson) })
                    }
                }
            }
            if (((state.ready && (state.screen == Screen.Vocabulary || state.screen == Screen.Conversation)) ||
                    (state.screen == Screen.VocabularyMode && words.ready && words.page in listOf(VocabularyPage.Cards, VocabularyPage.Quiz))) && voice == VoiceState.Unavailable && !keyboardVisible) {
                Surface(color = BlueTint) { Row(Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text("Chưa có giọng đọc tiếng Anh", Modifier.weight(1f), style = MaterialTheme.typography.bodySmall)
                    TextButton(openVoiceSettings) { Text("Cài giọng") }
                } }
            }
        }
        }
    }
}
