package vn.noi.english.learning

import vn.noi.english.content.Turn
import vn.noi.english.content.SlotKind
import java.util.Locale

object AnswerMatcher {
    fun matches(answer: String, turn: Turn): Boolean {
        val normalized = normalize(answer)
        if (normalized.isEmpty()) return false
        if (turn.accepted.any { normalize(it) == normalized }) return true
        // Personalised slots are deliberately limited; never fuzzy-match away negation.
        if (normalized.split(' ').any { it in setOf("not", "never", "no", "and", "but", "please", "coffee") }) return false
        return turn.slotPatterns.any { pattern ->
            val match = Regex(pattern).matchEntire(normalized) ?: return@any false
            if (turn.slotKind == SlotKind.PersonName) {
                val candidate = (if (match.groups.size > 1) match.groups[1]?.value else null) ?: return@any false
                candidate.split(' ').none { it in reservedNameWords }
            } else true
        }
    }

    private val reservedNameWords = setOf("i", "am", "is", "are", "a", "an", "the", "my", "name", "from", "in", "live", "student", "teacher", "engineer", "worker", "fine", "good", "ready", "yes", "thank", "thanks", "you", "your")

    private val contractions = mapOf(
        "i'm" to "i am", "i'd" to "i would", "it's" to "it is", "that's" to "that is",
        "he's" to "he is", "she's" to "she is", "there's" to "there is", "where's" to "where is", "what's" to "what is",
        "you're" to "you are", "we're" to "we are", "they're" to "they are",
        "i'll" to "i will", "you'll" to "you will", "we'll" to "we will", "they'll" to "they will",
        "he'll" to "he will", "she'll" to "she will", "it'll" to "it will",
        "i've" to "i have", "you've" to "you have", "we've" to "we have", "they've" to "they have",
        "don't" to "do not", "doesn't" to "does not", "didn't" to "did not",
        "isn't" to "is not", "aren't" to "are not", "wasn't" to "was not", "weren't" to "were not",
        "can't" to "can not", "cannot" to "can not", "won't" to "will not",
        "couldn't" to "could not", "wouldn't" to "would not", "shouldn't" to "should not",
        "haven't" to "have not", "hasn't" to "has not", "hadn't" to "had not",
    )
    private val contractionPattern = Regex("\\b(?:" + contractions.keys.joinToString("|") { Regex.escape(it) } + ")\\b")
    private val incompleteAffirmative = Regex("\\b(?:" + contractions.filterValues { !it.contains(" not") }.keys.joinToString("|") { Regex.escape(it) } + ")(?=\\s*(?:\\p{P}|$))")
    private fun normalize(text: String): String {
        val plain = text.lowercase(Locale.ROOT).replace('’', '\'').trim()
        if (incompleteAffirmative.containsMatchIn(plain)) return ""
        return contractionPattern.replace(plain) { contractions.getValue(it.value) }
            .replace(Regex("[.,!?;:\\u2014\\u2013]+"), " ").replace(Regex("\\s+"), " ").trim()
    }
}
