package vn.noi.english.vocabulary

import vn.noi.english.data.VocabularyProgress

enum class VocabularyPage { Home, Cards, CardsDone, Quiz, QuizResult }
data class VocabularyState(
    val page: VocabularyPage = VocabularyPage.Home,
    val topicId: String? = null,
    val progress: VocabularyProgress = VocabularyProgress(),
    val loading: Boolean = true,
    val ready: Boolean = false,
    val saving: Boolean = false,
    val error: String? = null,
    val sessionCards: List<WordCard> = emptyList(),
    val cardIndex: Int = 0,
    val revealed: Boolean = false,
    val questions: List<QuizQuestion> = emptyList(),
    val quizIndex: Int = 0,
    val quizChoice: String? = null,
    val quizScore: Int = 0,
) {
    val eligible get() = VocabularyCatalog.cards.filter { topicId == null || topicId in it.topicIds }
    val knownCount get() = eligible.count { it.id in progress.known }
    val reviewCount get() = eligible.count { it.id in progress.review }
    val unseenCount get() = eligible.size - knownCount - reviewCount
    val currentCard get() = sessionCards.getOrNull(cardIndex)
    val currentQuestion get() = questions.getOrNull(quizIndex)
}
