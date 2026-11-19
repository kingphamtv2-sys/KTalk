package vn.noi.english.learning

import vn.noi.english.content.Turn
import java.util.Locale

object AnswerMatcher {
    fun matches(answer: String, turn: Turn): Boolean {
        val normalized = normalize(answer)
        if (normalized.isEmpty()) return false
        if (turn.accepted.any { normalize(it) == normalized }) return true
        // Personalised slots are deliberately limited; never fuzzy-match away negation.
        if (normalized.split(' ').any { it in setOf("not", "never", "no", "and", "but", "please", "coffee") }) return false
        return turn.slotPatterns.any { Regex(it).matches(normalized) }
    }

    private fun normalize(text: String): String = text.lowercase(Locale.ROOT)
        .replace('’', '\'').trim().trimEnd('.', '!', '?')
        .replace("i'm", "i am").replace("i'd", "i would")
        .replace("it's", "it is").replace("that's", "that is")
        .replace(",", " ").replace(Regex("\\s+"), " ").trim()
}
