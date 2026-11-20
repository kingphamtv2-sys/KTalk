package vn.noi.english.vocabulary

import vn.noi.english.data.VocabularyProgress
import java.text.Normalizer
import java.util.Locale

private val accents = Regex("\\p{M}+")
private val whitespace = Regex("\\s+")
private fun searchKey(text: String): String = Normalizer.normalize(text.lowercase(Locale.ROOT), Normalizer.Form.NFD)
    .replace(accents, "").replace('đ', 'd').replace('’', '\'').trim().replace(whitespace, " ")
private val searchIndex by lazy {
    VocabularyCatalog.cards.associate { it.id to (listOf(searchKey(it.english)) + it.meanings.map(::searchKey)) }
}

enum class VocabularyPage { Home, Cards, CardsDone, Quiz, QuizResult }
data class VocabularyState(
    val page: VocabularyPage = VocabularyPage.Home,
    val topicId: String? = null,
    val query: String = "",
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
    val eligible: List<WordCard> by lazy {
        val term = searchKey(query)
        VocabularyCatalog.cards.filter { card ->
            (topicId == null || topicId in card.topicIds) && (term.isEmpty() || searchIndex.getValue(card.id).any { it.contains(term) })
        }
    }
    val knownCount by lazy { eligible.count { it.id in progress.known } }
    val reviewCount by lazy { eligible.count { it.id in progress.review } }
    val unseenCount by lazy { eligible.size - knownCount - reviewCount }
    val currentCard get() = sessionCards.getOrNull(cardIndex)
    val currentQuestion get() = questions.getOrNull(quizIndex)
}
