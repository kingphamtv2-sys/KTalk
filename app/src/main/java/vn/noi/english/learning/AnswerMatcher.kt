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

    private fun normalize(text: String): String = text.lowercase(Locale.ROOT)
        .replace('’', '\'').trim()
        .replace("i'm", "i am").replace("i'd", "i would")
        .replace("it's", "it is").replace("that's", "that is")
        .replace(Regex("[.,!?;:\\u2014\\u2013]+"), " ").replace(Regex("\\s+"), " ").trim()
}
