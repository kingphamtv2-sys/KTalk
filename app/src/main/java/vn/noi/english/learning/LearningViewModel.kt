package vn.noi.english.learning

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import vn.noi.english.data.ProgressRepository
import vn.noi.english.content.Curriculum

class LearningViewModel(private val repository: ProgressRepository) : ViewModel() {
    private val mutable = MutableStateFlow(LearningState())
    val state = mutable.asStateFlow()
    private var progressJob: Job? = null
    init { reloadProgress() }
    fun reloadProgress() {
        progressJob?.cancel()
        mutable.update { it.copy(loading = true, message = null) }
        progressJob = viewModelScope.launch {
            try {
                repository.progress.collect { saved ->
                    val validIds = Curriculum.lessons.map { it.id }.toSet()
                    mutable.update { it.copy(progress = saved.copy(completedLessonIds = saved.completedLessonIds.intersect(validIds)), loading = false, ready = true) }
                }
            } catch (cancelled: CancellationException) { throw cancelled }
            catch (_: Exception) { mutable.update { it.copy(loading = false, ready = false, message = "Chưa đọc được tiến độ. Hãy thử tải lại.") } }
        }
    }
    fun openTopic(id: String) {
        if (state.value.saving || !state.value.ready || Curriculum.topics.none { it.id == id }) return
        mutable.update { it.copy(screen = Screen.Lessons, topicId = id, audioPhase = AudioPhase.Idle, message = null) }
    }
    fun openLesson(id: String) {
        val lesson = Curriculum.lesson(id) ?: return
        if (!state.value.ready || state.value.saving) return
        val saved = state.value.progress
        val position = if (saved.lessonId == id && id !in saved.completedLessonIds) saved.turnIndex.takeIf { it in lesson.turns.indices } ?: 0 else 0
        mutable.update { it.copy(screen = Screen.Vocabulary, topicId = lesson.topicId, lessonId = id, turnIndex = position,
            answer = "", feedback = Feedback.None, mode = AnswerMode.Speak, audioPhase = AudioPhase.Idle, message = null) }
    }
    fun startConversation() {
        val s = state.value
        if (s.screen != Screen.Vocabulary || s.lesson == null || s.saving) return
        persist({ repository.savePosition(s.lessonId!!, s.turnIndex) }) { it.copy(screen = Screen.Conversation) }
    }
    fun setMode(mode: AnswerMode) {
        if (state.value.saving) return
        mutable.update { it.copy(mode = mode, answer = "", feedback = Feedback.None, audioPhase = AudioPhase.Idle, message = null) }
    }
    fun setAnswer(text: String) {
        if (!state.value.saving) mutable.update { it.copy(answer = text, feedback = Feedback.None, message = null) }
    }
    fun submit() {
        val s = state.value
        if (s.screen != Screen.Conversation || s.saving || s.audioPhase != AudioPhase.Idle) return
        val turn = s.turn ?: return
        if (s.answer.isBlank()) { message("Bạn hãy nói, chọn hoặc nhập câu trả lời trước nhé."); return }
        mutable.update { it.copy(feedback = if (AnswerMatcher.matches(s.answer, turn)) Feedback.Accepted else Feedback.TryTemplate, message = null) }
    }
    fun nextTurn() {
        val s = state.value
        val lesson = s.lesson ?: return
        if (s.saving || s.feedback != Feedback.Accepted || s.screen != Screen.Conversation) return
        if (s.turnIndex == lesson.turns.lastIndex) {
            persist({ repository.complete(lesson.id) }) { it.copy(screen = Screen.Completion, audioPhase = AudioPhase.Idle) }
        } else {
            persist({ repository.savePosition(lesson.id, s.turnIndex + 1) }) { it.copy(turnIndex = s.turnIndex + 1, answer = "", feedback = Feedback.None, audioPhase = AudioPhase.Idle, mode = AnswerMode.Speak) }
        }
    }
    private fun persist(write: suspend () -> Unit, after: (LearningState) -> LearningState) {
        mutable.update { it.copy(saving = true, message = null, audioPhase = AudioPhase.Idle) }
        viewModelScope.launch {
            try { write(); mutable.update { after(it).copy(saving = false, message = null) } }
            catch (cancelled: CancellationException) { throw cancelled }
            catch (_: Exception) { mutable.update { it.copy(saving = false, message = "Chưa lưu được tiến độ. Bạn hãy thử lại, bài học vẫn được giữ ở đây.") } }
        }
    }
    fun retry() { if (!state.value.saving) mutable.update { it.copy(answer = "", feedback = Feedback.None, audioPhase = AudioPhase.Idle, message = null) } }
    fun home() { if (!state.value.saving) mutable.update { it.copy(screen = Screen.Home, audioPhase = AudioPhase.Idle, message = null) } }
    fun showProgress() { if (!state.value.saving) mutable.update { it.copy(screen = Screen.Progress, audioPhase = AudioPhase.Idle, message = null) } }
    fun back() {
        val s = state.value
        if (s.saving) return
        when (s.screen) {
            Screen.Lessons, Screen.Progress -> home()
            Screen.Vocabulary, Screen.Conversation, Screen.Completion -> openTopic(s.topicId)
            Screen.Home -> Unit
        }
    }
    fun setAudio(phase: AudioPhase) { mutable.update { it.copy(audioPhase = phase) } }
    fun message(text: String?) { mutable.update { it.copy(message = text, audioPhase = AudioPhase.Idle) } }
}
