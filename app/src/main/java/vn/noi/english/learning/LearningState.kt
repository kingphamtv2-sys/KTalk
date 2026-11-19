package vn.noi.english.learning

import vn.noi.english.content.Curriculum
import vn.noi.english.data.Progress

enum class Screen { Home, Progress, Lessons, Vocabulary, Conversation, Completion }
enum class AnswerMode { Speak, Choose, Type }
enum class Feedback { None, Accepted, TryTemplate }
enum class AudioPhase { Idle, Listening, Processing }
data class LearningState(
    val screen: Screen = Screen.Home,
    val topicId: String = "greetings",
    val lessonId: String? = null,
    val turnIndex: Int = 0,
    val mode: AnswerMode = AnswerMode.Speak,
    val answer: String = "",
    val feedback: Feedback = Feedback.None,
    val progress: Progress = Progress(),
    val loading: Boolean = true,
    val ready: Boolean = false,
    val saving: Boolean = false,
    val message: String? = null,
    val audioPhase: AudioPhase = AudioPhase.Idle,
) {
    val lesson get() = Curriculum.lesson(lessonId)
    val turn get() = lesson?.turns?.getOrNull(turnIndex)
    val topic get() = Curriculum.topics.first { it.id == topicId }
}
