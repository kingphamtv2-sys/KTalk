package vn.noi.english.vocabulary

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlin.random.Random
import vn.noi.english.data.VocabularyRepository
import vn.noi.english.content.Curriculum

class VocabularyViewModel(private val repository: VocabularyRepository, private val random: Random = Random.Default) : ViewModel() {
    private val mutable = MutableStateFlow(VocabularyState())
    val state = mutable.asStateFlow()
    private var progressJob: Job? = null
    init { reload() }
    fun reload() {
        if (state.value.saving) return
        progressJob?.cancel()
        mutable.update { it.copy(loading = true, error = null) }
        progressJob = viewModelScope.launch {
            try {
                repository.progress.collect { saved ->
                    val valid = VocabularyCatalog.cards.map { it.id }.toSet()
                    val review = saved.review.intersect(valid)
                    mutable.update { it.copy(progress = saved.copy(known = saved.known.intersect(valid) - review, review = review), loading = false, ready = true) }
                }
            } catch (cancelled: CancellationException) { throw cancelled }
            catch (_: Exception) { mutable.update { it.copy(loading = false, ready = false, error = "Chưa đọc được tiến độ từ vựng. Hãy thử tải lại.") } }
        }
    }
    fun openHome() {
        if (!state.value.saving) mutable.update { it.copy(page = VocabularyPage.Home, error = null) }
    }
    fun selectTopic(topicId: String?) {
        if (state.value.saving || !state.value.ready || state.value.page != VocabularyPage.Home) return
        if (topicId != null && Curriculum.topics.none { it.id == topicId }) return
        mutable.update { it.copy(topicId = topicId, error = null) }
    }
    private fun priority(card: WordCard): Int = when(card.id) {
        in state.value.progress.review -> 0
        in state.value.progress.known -> 2
        else -> 1
    }
    fun startCards(reviewOnly: Boolean = false) {
        val s = state.value
        if (!s.ready || s.saving) return
        val cards = s.eligible.filter { !reviewOnly || it.id in s.progress.review }.sortedBy(::priority)
        if (cards.isEmpty()) { mutable.update { it.copy(error = "Chưa có từ cần ôn trong chủ đề này. Bạn có thể học thẻ từ trước nhé.") }; return }
        mutable.update { it.copy(page = VocabularyPage.Cards, sessionCards = cards, cardIndex = 0, revealed = false, error = null) }
    }
    fun reveal() {
        if (!state.value.saving && state.value.page == VocabularyPage.Cards) mutable.update { it.copy(revealed = true) }
    }
    fun markCurrent(known: Boolean) {
        val s = state.value
        val card = s.currentCard ?: return
        if (!s.ready || s.saving || !s.revealed || s.page != VocabularyPage.Cards) return
        mutable.update { it.copy(saving = true, error = null) }
        viewModelScope.launch {
            try {
                repository.mark(card.id, known)
                mutable.update { it.copy(saving = false) }
                nextCard()
            } catch (cancelled: CancellationException) { throw cancelled }
            catch (_: Exception) { mutable.update { it.copy(saving = false, error = "Chưa lưu được mức nhớ. Thẻ vẫn ở đây, bạn hãy bấm lại để thử lưu.") } }
        }
    }
    fun nextCard() {
        val s = state.value
        if (s.saving || s.page != VocabularyPage.Cards) return
        mutable.update { it.copy(page = if (s.cardIndex + 1 < s.sessionCards.size) VocabularyPage.Cards else VocabularyPage.CardsDone,
            cardIndex = if (s.cardIndex + 1 < s.sessionCards.size) s.cardIndex + 1 else s.cardIndex, revealed = false, error = null) }
    }
    fun previousCard() {
        val s = state.value
        if (s.saving || s.page != VocabularyPage.Cards || s.cardIndex == 0) return
        mutable.update { it.copy(cardIndex = s.cardIndex - 1, revealed = false, error = null) }
    }
    fun startQuiz() {
        val s = state.value
        if (!s.ready || s.saving) return
        val eligible = s.eligible.shuffled(random).sortedBy(::priority)
        val questions = VocabularyCatalog.quiz(eligible, random)
        if (questions.isEmpty()) { mutable.update { it.copy(error = "Chưa đủ lựa chọn để tạo bài kiểm tra cho chủ đề này.") }; return }
        mutable.update { it.copy(page = VocabularyPage.Quiz, questions = questions, quizIndex = 0, quizChoice = null, quizScore = 0, error = null) }
    }
    fun answerQuiz(choice: String) {
        val s = state.value
        val question = s.currentQuestion ?: return
        if (s.page != VocabularyPage.Quiz || s.quizChoice != null || choice !in question.options) return
        mutable.update { it.copy(quizChoice = choice, quizScore = s.quizScore + if (choice == question.correctMeaning) 1 else 0) }
    }
    fun nextQuestion() {
        val s = state.value
        if (s.page != VocabularyPage.Quiz || s.quizChoice == null) return
        mutable.update { it.copy(page = if (s.quizIndex + 1 < s.questions.size) VocabularyPage.Quiz else VocabularyPage.QuizResult,
            quizIndex = if (s.quizIndex + 1 < s.questions.size) s.quizIndex + 1 else s.quizIndex, quizChoice = null) }
    }
    fun back(): Boolean {
        if (state.value.saving) return true
        if (state.value.page == VocabularyPage.Home) return false
        openHome(); return true
    }
}
