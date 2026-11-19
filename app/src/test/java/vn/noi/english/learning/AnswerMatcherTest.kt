package vn.noi.english.learning

import org.junit.Assert.*
import org.junit.Test
import vn.noi.english.content.Curriculum

class AnswerMatcherTest {
    private val nameTurn get() = Curriculum.topics[1].lessons[0].turns[0]
    @Test fun acceptsNamesAndContractions() {
        listOf("My name is Ngọc Anh.", "I’m Linh!", "I am Linh", "  MY  NAME IS  LINH. ").forEach {
            assertTrue(it, AnswerMatcher.matches(it, nameTurn))
        }
    }
    @Test fun rejectsMissingNameAndWrongContext() {
        listOf("", "...", "My name is", "I'm", "A coffee, please", "My name is not Linh", "I'm not Linh", "My name is 123", "My name is Linh! A coffee please").forEach {
            assertFalse(it, AnswerMatcher.matches(it, nameTurn))
        }
    }
    @Test fun neverDiscardsNegation() {
        val turn = Curriculum.topics[0].lessons[1].turns[0]
        assertTrue(AnswerMatcher.matches(turn.sample, turn))
        assertFalse(AnswerMatcher.matches("I'm not fine, thank you.", turn))
    }
}
