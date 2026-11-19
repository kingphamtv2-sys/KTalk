package vn.noi.english.learning

import java.io.IOException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.Assert.*
import vn.noi.english.content.Curriculum
import vn.noi.english.data.Progress
import vn.noi.english.data.ProgressRepository

@OptIn(ExperimentalCoroutinesApi::class)
class LearningViewModelTest {
    private val dispatcher = StandardTestDispatcher()
    @Before fun setup() { Dispatchers.setMain(dispatcher) }
    @After fun teardown() { Dispatchers.resetMain() }
    private val lesson get() = Curriculum.topics[0].lessons[0]

    @Test fun wrongAndEmptyAnswersNeverAdvance() = runTest(dispatcher) {
        val vm = LearningViewModel(FakeProgress())
        runCurrent(); vm.openLesson(lesson.id); vm.startConversation(); runCurrent()
        vm.submit(); assertNotNull(vm.state.value.message)
        vm.setAnswer("A coffee please"); vm.submit()
        assertEquals(Feedback.TryTemplate, vm.state.value.feedback)
        vm.nextTurn(); runCurrent()
        assertEquals(0, vm.state.value.turnIndex)
        vm.setAnswer(lesson.turns[0].sample); vm.submit()
        assertEquals(Feedback.Accepted, vm.state.value.feedback)
        assertEquals(0, vm.state.value.turnIndex)
    }
    @Test fun fourTurnsPersistCompletionAndReplayIsNotCountedTwice() = runTest(dispatcher) {
        val repo = FakeProgress(); val vm = LearningViewModel(repo)
        runCurrent()
        repeat(2) {
            vm.openLesson(lesson.id); vm.startConversation(); runCurrent()
            assertEquals(0, vm.state.value.turnIndex)
            lesson.turns.forEach { turn ->
                vm.setAnswer(turn.sample); vm.submit(); vm.nextTurn(); runCurrent()
            }
            assertEquals(Screen.Completion, vm.state.value.screen)
        }
        assertEquals(setOf("greetings-morning"), repo.progress.value.completedLessonIds)
    }
    @Test fun failedSaveCannotReportCompletionAndCanRetry() = runTest(dispatcher) {
        val repo = FakeProgress(); val vm = LearningViewModel(repo)
        runCurrent(); vm.openLesson(lesson.id); vm.startConversation(); runCurrent()
        lesson.turns.take(3).forEach { vm.setAnswer(it.sample); vm.submit(); vm.nextTurn(); runCurrent() }
        vm.setAnswer(lesson.turns.last().sample); vm.submit()
        repo.fail = true; vm.nextTurn(); runCurrent()
        assertEquals(Screen.Conversation, vm.state.value.screen)
        assertNotNull(vm.state.value.message)
        assertTrue(repo.progress.value.completedLessonIds.isEmpty())
        repo.fail = false; vm.nextTurn(); runCurrent()
        assertEquals(Screen.Completion, vm.state.value.screen)
    }
    @Test fun restoresPositionWithoutRestoringRecordingOrDraft() = runTest(dispatcher) {
        val repo = FakeProgress(Progress(lessonId = lesson.id, turnIndex = 2))
        val vm = LearningViewModel(repo); runCurrent()
        vm.openLesson(lesson.id); vm.startConversation(); runCurrent()
        assertEquals(2, vm.state.value.turnIndex)
        assertEquals("", vm.state.value.answer)
        assertEquals(AudioPhase.Idle, vm.state.value.audioPhase)
    }
    @Test fun stalePositionIsSafelyResetAndStorageReadCanRetry() = runTest(dispatcher) {
        val vm = LearningViewModel(FakeProgress(Progress(lessonId = lesson.id, turnIndex = 99)))
        runCurrent(); vm.openLesson(lesson.id); vm.startConversation(); runCurrent()
        assertEquals(0, vm.state.value.turnIndex)
    }
    @Test fun leavingConversationAndSwitchingModesClearsUnsubmittedSpeech() = runTest(dispatcher) {
        val vm = LearningViewModel(FakeProgress()); runCurrent()
        vm.openLesson(lesson.id); vm.startConversation(); runCurrent()
        vm.setAudio(AudioPhase.Listening); vm.setMode(AnswerMode.Type)
        assertEquals(AudioPhase.Idle, vm.state.value.audioPhase)
        vm.home(); assertEquals(Screen.Home, vm.state.value.screen)
    }
}

class FakeProgress(initial: Progress = Progress()) : ProgressRepository {
    override val progress = MutableStateFlow(initial)
    var fail = false
    override suspend fun savePosition(lessonId: String, turnIndex: Int) {
        if (fail) throw IOException("disk full")
        progress.value = progress.value.copy(lessonId = lessonId, turnIndex = turnIndex)
    }
    override suspend fun complete(lessonId: String) {
        if (fail) throw IOException("disk full")
        progress.value = progress.value.copy(completedLessonIds = progress.value.completedLessonIds + lessonId, lessonId = null, turnIndex = 0)
    }
}
