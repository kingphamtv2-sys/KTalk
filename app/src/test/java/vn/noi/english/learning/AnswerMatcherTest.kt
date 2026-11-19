package vn.noi.english.learning

import org.junit.Assert.*
import org.junit.Test
import vn.noi.english.content.Curriculum
import vn.noi.english.content.Turn

class AnswerMatcherTest {
    private val nameTurn = Turn("name", "What's your name?", "Bạn tên gì?", "My name is…", "My name is Linh.", "Giới thiệu tên", listOf("My name is Linh."), listOf("My name is Linh."), listOf("(?:my name is|i am) [\\p{L}]+(?: [\\p{L}]+){0,3}"))
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
        val turn = nameTurn.copy(accepted = listOf("I'm fine, thank you."), slotPatterns = emptyList(), sample = "I'm fine, thank you.")
        assertTrue(AnswerMatcher.matches(turn.sample, turn))
        assertFalse(AnswerMatcher.matches("I'm not fine, thank you.", turn))
    }
}
