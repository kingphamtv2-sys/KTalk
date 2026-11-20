package vn.noi.english.vocabulary

import java.io.IOException
import kotlin.random.Random
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.test.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.*
import org.junit.Assert.*
import vn.noi.english.data.VocabularyProgress
import vn.noi.english.data.VocabularyRepository

@OptIn(ExperimentalCoroutinesApi::class)
class VocabularyViewModelTest {
    private val dispatcher = StandardTestDispatcher()
    @Before fun setup() { Dispatchers.setMain(dispatcher) }
    @After fun teardown() { Dispatchers.resetMain() }
    @Test fun revealThenSaveBeforeAdvancingAndIgnoreDoubleTaps() = runTest(dispatcher) {
        val repo = FakeWords(); val vm = VocabularyViewModel(repo); runCurrent()
        vm.startCards(); val first = vm.state.value.currentCard!!
        vm.markCurrent(true); runCurrent()
        assertTrue(repo.saved.value.known.isEmpty())
        vm.reveal(); vm.markCurrent(true); vm.markCurrent(true)
        assertEquals(0, vm.state.value.cardIndex)
        assertTrue(vm.state.value.saving)
        runCurrent()
        assertEquals(setOf(first.id), vm.state.value.progress.known)
        assertEquals(1, vm.state.value.cardIndex)
        assertFalse(vm.state.value.revealed)
    }
    @Test fun failedWriteRetainsCardAndRetryWorks() = runTest(dispatcher) {
        val repo = FakeWords(); val vm = VocabularyViewModel(repo); runCurrent()
        vm.startCards(); vm.reveal(); repo.writeFails = true; vm.markCurrent(false); runCurrent()
        assertEquals(0, vm.state.value.cardIndex)
        assertNotNull(vm.state.value.error)
        assertTrue(vm.state.value.revealed)
        assertFalse(vm.state.value.saving)
        assertTrue(vm.state.value.progress.review.isEmpty())
        repo.writeFails = false; vm.markCurrent(false); runCurrent()
        assertEquals(1, vm.state.value.cardIndex)
        assertEquals(1, vm.state.value.progress.review.size)
    }
    @Test fun emptyReviewIsSafeAndTopicFilterUsesTheRightCards() = runTest(dispatcher) {
        val vm = VocabularyViewModel(FakeWords()); runCurrent()
        vm.startCards(reviewOnly = true)
        assertEquals(VocabularyPage.Home, vm.state.value.page)
        assertNotNull(vm.state.value.error)
        vm.selectTopic("family"); vm.startCards()
        assertTrue(vm.state.value.sessionCards.isNotEmpty())
        assertTrue(vm.state.value.sessionCards.all { "family" in it.topicIds })
        assertTrue(vm.back()); assertEquals(VocabularyPage.Home, vm.state.value.page)
        assertFalse(vm.back())
    }
    @Test fun rememberedAndReviewStatesRestoreAndRemainDisjoint() = runTest(dispatcher) {
        val id = VocabularyCatalog.cards.first().id
        val repo = FakeWords(VocabularyProgress(setOf(id), setOf(id, "unknown-id")))
        val vm = VocabularyViewModel(repo); runCurrent()
        assertEquals(setOf(id), vm.state.value.progress.review)
        assertTrue(vm.state.value.progress.known.isEmpty())
        vm.startCards(true); vm.reveal(); vm.markCurrent(true); runCurrent()
        assertEquals(VocabularyPage.CardsDone, vm.state.value.page)
        assertEquals(setOf(id), vm.state.value.progress.known)
        assertTrue(vm.state.value.progress.review.isEmpty())
        val restored = VocabularyViewModel(repo); runCurrent()
        assertEquals(setOf(id), restored.state.value.progress.known)
    }
    @Test fun readFailureIsNotReportedAsEmptyProgressAndCanRetry() = runTest(dispatcher) {
        val repo = FakeWords(); repo.readFails = true
        val vm = VocabularyViewModel(repo); runCurrent()
        assertFalse(vm.state.value.ready)
        assertNotNull(vm.state.value.error)
        vm.startCards(); assertEquals(VocabularyPage.Home, vm.state.value.page)
        repo.readFails = false; vm.reload(); runCurrent()
        assertTrue(vm.state.value.ready)
    }
    @Test fun quizScoresOncePerQuestionAndDoesNotChangeSelfAssessment() = runTest(dispatcher) {
        val repo = FakeWords(); val vm = VocabularyViewModel(repo, Random(42)); runCurrent()
        vm.selectTopic("greetings"); vm.startQuiz()
        assertEquals(5, vm.state.value.questions.size)
        vm.nextQuestion(); assertEquals(0, vm.state.value.quizIndex)
        vm.answerQuiz("not an option"); assertNull(vm.state.value.quizChoice)
        repeat(5) {
            val q = vm.state.value.currentQuestion!!
            vm.answerQuiz(q.correctMeaning); vm.answerQuiz(q.options.first { it != q.correctMeaning })
            vm.nextQuestion()
        }
        assertEquals(VocabularyPage.QuizResult, vm.state.value.page)
        assertEquals(5, vm.state.value.quizScore)
        assertEquals(VocabularyProgress(), repo.saved.value)
    }
    @Test fun skippingAndRevisitingDoesNotMarkWords() = runTest(dispatcher) {
        val repo = FakeWords(); val vm = VocabularyViewModel(repo); runCurrent()
        vm.startCards(); vm.reveal(); vm.nextCard()
        assertEquals(1, vm.state.value.cardIndex)
        vm.previousCard()
        assertEquals(0, vm.state.value.cardIndex)
        assertFalse(vm.state.value.revealed)
        assertEquals(VocabularyProgress(), repo.saved.value)
    }
    @Test fun quizPrioritizesReviewAndTopicFilteringStillApplies() = runTest(dispatcher) {
        val reviewCard = VocabularyCatalog.cards.last { "family" in it.topicIds }
        val repo = FakeWords(VocabularyProgress(review = setOf(reviewCard.id)))
        val vm = VocabularyViewModel(repo, Random(4)); runCurrent()
        vm.selectTopic("family"); vm.startQuiz()
        assertEquals(reviewCard.id, vm.state.value.questions.first().card.id)
        assertTrue(vm.state.value.questions.all { "family" in it.card.topicIds })
    }
}

class FakeWords(initial: VocabularyProgress = VocabularyProgress()) : VocabularyRepository {
    val saved = MutableStateFlow(initial)
    var readFails = false
    var writeFails = false
    override val progress: Flow<VocabularyProgress> = flow {
        if (readFails) throw IOException("unreadable")
        emitAll(saved)
    }
    override suspend fun mark(wordId: String, known: Boolean) {
        if (writeFails) throw IOException("disk full")
        saved.value = if (known) VocabularyProgress(saved.value.known + wordId, saved.value.review - wordId)
            else VocabularyProgress(saved.value.known - wordId, saved.value.review + wordId)
    }
}
