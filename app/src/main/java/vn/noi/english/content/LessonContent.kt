package vn.noi.english.content

import kotlin.random.Random

/** Shared authoring helpers for the additional beginner dialogues. */
internal object LessonContent {
    fun words(vararg entries: Pair<String, String>) = entries.map { Vocabulary(it.first, it.second) }
    fun turn(question: String, translation: String, sample: String, hint: String, explanation: String,
             distractor: String, alternatives: List<String> = emptyList()) = Turn(
        id = "", prompt = question, translation = translation, hint = hint, sample = sample,
        explanation = explanation, choices = listOf(sample, distractor),
        accepted = (listOf(sample) + alternatives).flatMap { listOf(it, expandContractions(it)) }.distinct(),
    )
    fun lesson(topic: String, slug: String, title: String, situation: String, vocabulary: List<Vocabulary>, vararg turns: Turn): Lesson {
        val id = "$topic-$slug"
        return Lesson(id, topic, title, situation, vocabulary, turns.mapIndexed { index, turn ->
            val turnId = "$id-${index + 1}"
            turn.copy(id = turnId, choices = turn.choices.shuffled(Random(turnId.hashCode())))
        })
    }
    private fun expandContractions(text: String): String {
        var result = text
        mapOf("I'm" to "I am", "I'll" to "I will", "I've" to "I have", "I'd" to "I would",
            "we're" to "we are", "we'll" to "we will", "you're" to "you are", "she's" to "she is",
            "he's" to "he is", "it's" to "it is", "that's" to "that is", "can't" to "cannot",
            "don't" to "do not", "doesn't" to "does not", "isn't" to "is not", "won't" to "will not")
            .forEach { (short, full) -> result = result.replace(Regex("\\b${Regex.escape(short)}\\b", RegexOption.IGNORE_CASE), full) }
        return result
    }
}
