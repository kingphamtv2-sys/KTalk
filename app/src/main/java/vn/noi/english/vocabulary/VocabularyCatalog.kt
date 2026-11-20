package vn.noi.english.vocabulary

import kotlin.random.Random
import java.util.Locale
import vn.noi.english.content.Curriculum
import vn.noi.english.content.Topic

data class WordCard(val id: String, val english: String, val meanings: List<String>, val topicIds: Set<String>, val lessonIds: Set<String>)
data class QuizQuestion(val card: WordCard, val correctMeaning: String, val options: List<String>)
object VocabularyCatalog {
    private data class Source(val english: String, val meaning: String, val topicId: String, val lessonId: String)
    val cards: List<WordCard> = fromTopics(Curriculum.topics)
    fun fromTopics(topics: List<Topic>): List<WordCard> = topics.flatMap { topic -> topic.lessons.flatMap { lesson ->
        lesson.vocabulary.map { Source(it.english.trim(), it.vietnamese.trim(), topic.id, lesson.id) }
    } }.groupBy { wordId(it.english) }.map { (id, sources) ->
        WordCard(id, sources.first().english, sources.map { it.meaning }.distinct(), sources.map { it.topicId }.toSet(), sources.map { it.lessonId }.toSet())
    }
    fun wordId(english: String): String = "word:" + english.trim().lowercase(Locale.ROOT)
        .replace('’', '\'').replace("…", "...").replace(Regex("\\s+"), " ")
    private fun meaningKey(text: String) = text.lowercase(Locale.ROOT).replace(Regex("[\\p{P}\\s]+"), " ").trim()
    private val stopWords = setOf("tôi", "bạn", "của", "một", "các", "những", "là", "ở", "và", "hoặc", "cho", "với", "để", "vào", "khi", "sự", "cái", "chiếc", "người")
    // These translations overlap in ordinary usage even without common Vietnamese tokens.
    private val confusableGroups = listOf(setOf("This is…", "My name is…"), setOf("receipt", "the bill"))
        .map { group -> group.map(::wordId).toSet() }
    private fun contentWords(text: String) = meaningKey(text).split(' ').filter { it.isNotBlank() && it !in stopWords }.toSet()
    /** Caller orders eligible cards by review priority; randomness only chooses/reorders options. */
    fun quiz(eligible: List<WordCard>, random: Random): List<QuizQuestion> = eligible.distinctBy { it.id }.take(5).mapNotNull { card ->
        val knownMeanings = card.meanings.map(::meaningKey).toSet()
        val knownWords = card.meanings.flatMap { contentWords(it) }.toSet()
        val confusable = confusableGroups.filter { card.id in it }.flatten().toSet()
        val wrong = cards.filter { it.id != card.id && it.id !in confusable }.flatMap { it.meanings }
            .distinctBy(::meaningKey)
            .filter { meaningKey(it) !in knownMeanings && contentWords(it).intersect(knownWords).isEmpty() }
            .shuffled(random).take(3)
        if (wrong.size < 3) null else QuizQuestion(card, card.meanings.first(), (wrong + card.meanings.first()).shuffled(random))
    }.take(5)
}
